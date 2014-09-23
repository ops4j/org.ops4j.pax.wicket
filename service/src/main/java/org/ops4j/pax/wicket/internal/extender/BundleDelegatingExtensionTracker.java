/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal.extender;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.internal.BundleDelegatingClassResolver;
import org.ops4j.pax.wicket.internal.BundleDelegatingPageMounter;
import org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Right now it listens on all pax-wicket applications. In addition it is "feeded" by a bundleListeners with all bundles
 * implementing org.apache.wicket.
 * 
 * If a service is added a new BundleDelegatingVersion of the classloaders, injection handlers and mount point listeners
 * is added to the service reference. Initally all currently registered bundles are checked then if they should be added
 * into the specific lifecycle for a specific application.
 * 
 * If an application get updated the check if bundles are still valid for this package are repeated.
 * 
 * Every time a bundle is added it is evaluated to which BundleDelegatingServices this bundle should be added (and is
 * added to the matching services).
 * 
 * Everytime a bundle is removed it is simply removed from all applications from all services.
 */
@Component
public class BundleDelegatingExtensionTracker extends PaxWicketBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingExtensionTracker.class);

    private BundleContext paxWicketBundleContext;
    private final Map<String, ExtendedBundle> relvantBundles = new HashMap<String, ExtendedBundle>();
    private final Map<ServiceReference<WebApplicationFactory<?>>, BundleDelegatingClassResolver> classResolvers =
        new HashMap<ServiceReference<WebApplicationFactory<?>>, BundleDelegatingClassResolver>();
    private final Map<ServiceReference<WebApplicationFactory<?>>, BundleDelegatingComponentInstanciationListener> componentInstanciationListener =
        new HashMap<ServiceReference<WebApplicationFactory<?>>, BundleDelegatingComponentInstanciationListener>();
    private final Map<ServiceReference<WebApplicationFactory<?>>, BundleDelegatingPageMounter> pageMounter =
        new HashMap<ServiceReference<WebApplicationFactory<?>>, BundleDelegatingPageMounter>();

    private ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> factoryTracker;

    private BundleTracker<ExtendedBundle> bundleExtensionTracker;

    @Override
    @Activate
    public void activate(BundleContext bundleContext) {
        super.activate(bundleContext);
        paxWicketBundleContext = bundleContext;
        // TODO replace this by a DS injection, we just keep this for now to allow easier transition
        factoryTracker = new ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory>(bundleContext,
                ProxyTargetLocatorFactory.class, null);
        factoryTracker.open();
        bundleExtensionTracker = new BundleTracker<ExtendedBundle>(bundleContext, Bundle.ACTIVE, this);
        bundleExtensionTracker.open();
    }

    @Deactivate
    public void deactivate() {
        factoryTracker.close();
        bundleExtensionTracker.close();
    }

    @Reference(service = WebApplicationFactory.class, unbind = "removedService", updated = "modifiedService",
        cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addingService(ServiceReference<WebApplicationFactory<?>> reference) {
        synchronized (this) {
            addServicesForServiceReference(reference);
            reevaluateAllBundles(reference);
        }
    }

    public void modifiedService(ServiceReference<WebApplicationFactory<?>> reference) {
        // TODO check if this is really needed or if we are fine with the normal remove/add provided by DS...
        synchronized (this) {
            removeServicesForServiceReference(reference);
            addServicesForServiceReference(reference);
            reevaluateAllBundles(reference);
        }
    }

    public void removedService(ServiceReference<WebApplicationFactory<?>> reference) {
        synchronized (this) {
            removeServicesForServiceReference(reference);
        }
    }

    private void addServicesForServiceReference(ServiceReference<WebApplicationFactory<?>> reference) {
        String applicationName = (String) reference.getProperty(Constants.APPLICATION_NAME);
        if (applicationName == null) {
            throw new IllegalArgumentException("The service must provide a '" + Constants.APPLICATION_NAME
                    + "' property");
        }
        classResolvers.put(reference, new BundleDelegatingClassResolver(paxWicketBundleContext, applicationName));
        classResolvers.get(reference).start();
        componentInstanciationListener.put(reference, new BundleDelegatingComponentInstanciationListener(
            paxWicketBundleContext, applicationName, factoryTracker));
        componentInstanciationListener.get(reference).start();
        pageMounter.put(reference, new BundleDelegatingPageMounter(applicationName, paxWicketBundleContext));
        pageMounter.get(reference).start();
    }

    private void removeServicesForServiceReference(ServiceReference<WebApplicationFactory<?>> reference) {
        classResolvers.get(reference).stop();
        classResolvers.remove(reference);
        componentInstanciationListener.get(reference).stop();
        componentInstanciationListener.remove(reference);
        pageMounter.get(reference).stop();
        pageMounter.remove(reference);
    }

    private void reevaluateAllBundles(ServiceReference<WebApplicationFactory<?>> reference) {
        Collection<ExtendedBundle> bundles = relvantBundles.values();
        for (ExtendedBundle bundle : bundles) {
            addBundleToServicesReference(bundle, reference);
        }
    }

    @Override
    public void addRelevantBundle(ExtendedBundle bundle) {
        synchronized (this) {
            relvantBundles.put(bundle.getID(), bundle);
            Set<ServiceReference<WebApplicationFactory<?>>> services = classResolvers.keySet();
            for (ServiceReference<WebApplicationFactory<?>> serviceReference : services) {
                addBundleToServicesReference(bundle, serviceReference);
            }
        }
    }

    private void addBundleToServicesReference(ExtendedBundle bundle,
            ServiceReference<WebApplicationFactory<?>> reference) {
        try {
            classResolvers.get(reference).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn("A specific reference could not be added to the classResolvers; might not be too bad", e);
        }
        try {
            componentInstanciationListener.get(reference).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn(
                "A specific reference could not be added to the componentInstanciationListener; might not be too bad",
                e);
        }
        try {
            pageMounter.get(reference).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn("A specific reference could not be added to pageMounter; might not be too bad", e);
        }
    }

    @Override
    public void removeRelevantBundle(ExtendedBundle bundle) {
        synchronized (this) {
            relvantBundles.remove(bundle.getID());
            removeBundleFromAllServices(bundle);
        }
    }

    private void removeBundleFromAllServices(ExtendedBundle bundle) {
        Set<ServiceReference<WebApplicationFactory<?>>> services = classResolvers.keySet();
        for (ServiceReference<WebApplicationFactory<?>> reference : services) {
            classResolvers.get(reference).removeBundle(bundle);
            componentInstanciationListener.get(reference).removeBundle(bundle);
            pageMounter.get(reference).removeBundle(bundle);
        }
    }

}
