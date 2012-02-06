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

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.internal.BundleDelegatingClassResolver;
import org.ops4j.pax.wicket.internal.BundleDelegatingPageMounter;
import org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
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
public class BundleDelegatingExtensionTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingExtensionTracker.class);

    private BundleContext paxWicketBundleContext;
    private Map<String, Bundle> relvantBundles = new HashMap<String, Bundle>();
    private Map<ServiceReference, BundleDelegatingClassResolver> classResolvers =
        new HashMap<ServiceReference, BundleDelegatingClassResolver>();
    private Map<ServiceReference, BundleDelegatingComponentInstanciationListener> componentInstanciationListener =
        new HashMap<ServiceReference, BundleDelegatingComponentInstanciationListener>();
    private Map<ServiceReference, BundleDelegatingPageMounter> pageMounter =
        new HashMap<ServiceReference, BundleDelegatingPageMounter>();

    public BundleDelegatingExtensionTracker(BundleContext context) {
        super(context, IWebApplicationFactory.class.getName(), null);
        paxWicketBundleContext = context;
    }

    @Override
    public Object addingService(ServiceReference reference) {
        synchronized (this) {
            addServicesForServiceReference(reference);
            reevaluateAllBundles(reference);
        }
        return super.addingService(reference);
    }

    @Override
    public void modifiedService(ServiceReference reference, Object service) {
        synchronized (this) {
            removeServicesForServiceReference(reference);
            addServicesForServiceReference(reference);
            reevaluateAllBundles(reference);
        }
        super.modifiedService(reference, service);
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        synchronized (this) {
            removeServicesForServiceReference(reference);
        }
        super.removedService(reference, service);
    }

    private void addServicesForServiceReference(ServiceReference reference) {
        String applicationName = (String) reference.getProperty(Constants.APPLICATION_NAME);
        classResolvers.put(reference, new BundleDelegatingClassResolver(paxWicketBundleContext, applicationName));
        classResolvers.get(reference).start();
        componentInstanciationListener.put(reference, new BundleDelegatingComponentInstanciationListener(
            paxWicketBundleContext, applicationName));
        componentInstanciationListener.get(reference).start();
        pageMounter.put(reference, new BundleDelegatingPageMounter(applicationName, paxWicketBundleContext));
        pageMounter.get(reference).start();
    }

    private void removeServicesForServiceReference(ServiceReference reference) {
        classResolvers.get(reference).stop();
        classResolvers.remove(reference);
        componentInstanciationListener.get(reference).stop();
        componentInstanciationListener.remove(reference);
        pageMounter.get(reference).stop();
        pageMounter.remove(reference);
    }

    private void reevaluateAllBundles(ServiceReference reference) {
        Collection<Bundle> bundles = relvantBundles.values();
        for (Bundle bundle : bundles) {
            addBundleToServicesReference(bundle, reference);
        }
    }

    public void addRelevantBundle(Bundle bundle) {
        synchronized (this) {
            relvantBundles.put(bundle.getSymbolicName(), bundle);
            Set<ServiceReference> services = classResolvers.keySet();
            for (ServiceReference serviceReference : services) {
                addBundleToServicesReference(bundle, serviceReference);
            }
        }
    }

    private void addBundleToServicesReference(Bundle bundle, ServiceReference reference) {
        try {
            classResolvers.get(reference).addBundle(bundle);
            componentInstanciationListener.get(reference).addBundle(bundle);
            pageMounter.get(reference).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn("A specific reference could not be added; might not be too bad", e);
        }
    }

    public void removeRelevantBundle(Bundle bundle) {
        synchronized (this) {
            relvantBundles.remove(bundle.getSymbolicName());
            removeBundleFromAllServices(bundle);
        }
    }

    private void removeBundleFromAllServices(Bundle bundle) {
        Set<ServiceReference> services = classResolvers.keySet();
        for (ServiceReference reference : services) {
            classResolvers.get(reference).removeBundle(bundle);
            componentInstanciationListener.get(reference).removeBundle(bundle);
            pageMounter.get(reference).removeBundle(bundle);
        }
    }

}
