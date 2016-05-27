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

import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.internal.BundleDelegatingClassResolver;
import org.ops4j.pax.wicket.internal.BundleDelegatingPageMounter;
import org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.util.tracker.BundleTracker;
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
 *
 * @author nmw
 * @version $Id: $Id
 */
@Component
public class BundleDelegatingExtensionTracker extends PaxWicketBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingExtensionTracker.class);

    private final Map<String, ExtendedBundle> relvantBundles = new HashMap<String, ExtendedBundle>();
    private final Map<WebApplicationFactory<?>, BundleDelegatingClassResolver> classResolvers =
        new HashMap<WebApplicationFactory<?>, BundleDelegatingClassResolver>();
    private final Map<WebApplicationFactory<?>, BundleDelegatingComponentInstanciationListener> componentInstanciationListener =
        new HashMap<WebApplicationFactory<?>, BundleDelegatingComponentInstanciationListener>();
    private final Map<WebApplicationFactory<?>, BundleDelegatingPageMounter> pageMounter =
        new HashMap<WebApplicationFactory<?>, BundleDelegatingPageMounter>();

    private BundleTracker<ExtendedBundle> bundleExtensionTracker;

    /** {@inheritDoc} */
    @Override
    @Activate
    public void activate(BundleContext bundleContext) {
        super.activate(bundleContext);
        bundleExtensionTracker = new BundleTracker<ExtendedBundle>(bundleContext, Bundle.ACTIVE, this);
        bundleExtensionTracker.open();
    }

    /**
     * <p>deactivate.</p>
     *
     * @since 3.0.5
     */
    @Deactivate
    public void deactivate() {
        bundleExtensionTracker.close();
    }

    /**
     * <p>addWebApplicationFactory.</p>
     *
     * @param webApplicationFactory a {@link org.ops4j.pax.wicket.api.WebApplicationFactory} object.
     * @param properties a {@link java.util.Map} object.
     * @since 3.0.5
     */
    @Reference(service = WebApplicationFactory.class, unbind = "removeWebApplicationFactory",
        updated = "modifiedWebApplicationFactory",
        cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC)
    public void addWebApplicationFactory(WebApplicationFactory<?> webApplicationFactory, Map<String, ?> properties) {
        synchronized (this) {
            addServicesForServiceReference(webApplicationFactory, properties);
            reevaluateAllBundles(webApplicationFactory);
        }
    }

    /**
     * <p>modifiedWebApplicationFactory.</p>
     *
     * @param webApplicationFactory a {@link org.ops4j.pax.wicket.api.WebApplicationFactory} object.
     * @param properties a {@link java.util.Map} object.
     * @since 3.0.5
     */
    public void
        modifiedWebApplicationFactory(WebApplicationFactory<?> webApplicationFactory, Map<String, ?> properties) {
        // TODO check if this is really needed or if we are fine with the normal remove/add provided by DS...
        synchronized (this) {
            removeServicesForServiceReference(webApplicationFactory);
            addServicesForServiceReference(webApplicationFactory, properties);
            reevaluateAllBundles(webApplicationFactory);
        }
    }

    /**
     * <p>removeWebApplicationFactory.</p>
     *
     * @param webApplicationFactory a {@link org.ops4j.pax.wicket.api.WebApplicationFactory} object.
     * @since 3.0.5
     */
    public void removeWebApplicationFactory(WebApplicationFactory<?> webApplicationFactory) {
        synchronized (this) {
            removeServicesForServiceReference(webApplicationFactory);
        }
    }

    private void addServicesForServiceReference(WebApplicationFactory<?> webApplicationFactory,
            Map<String, ?> properties) {
        Object applicationName = properties.get(Constants.APPLICATION_NAME);
        if (applicationName == null) {
            throw new IllegalArgumentException("The service must provide a '" + Constants.APPLICATION_NAME
                    + "' property");
        }
        // fetch it via the classloader because of the dynamic nature of service adding might happen before component is
        // activated. We should change this later to use a more generic aproach eg. extracting this to different
        // (independent) components
        BundleContext paxWicketBundleContext =
            ((BundleReference) BundleDelegatingClassResolver.class.getClassLoader()).getBundle().getBundleContext();
        classResolvers.put(webApplicationFactory, new BundleDelegatingClassResolver(paxWicketBundleContext,
            applicationName.toString()));
        classResolvers.get(webApplicationFactory).start();
        componentInstanciationListener.put(webApplicationFactory, new BundleDelegatingComponentInstanciationListener(
            paxWicketBundleContext, applicationName.toString()));
        componentInstanciationListener.get(webApplicationFactory).start();
        pageMounter.put(webApplicationFactory, new BundleDelegatingPageMounter(applicationName.toString(),
            paxWicketBundleContext));
        pageMounter.get(webApplicationFactory).start();
    }

    private void removeServicesForServiceReference(WebApplicationFactory<?> webApplicationFactory) {
        classResolvers.get(webApplicationFactory).stop();
        classResolvers.remove(webApplicationFactory);
        componentInstanciationListener.get(webApplicationFactory).stop();
        componentInstanciationListener.remove(webApplicationFactory);
        pageMounter.get(webApplicationFactory).stop();
        pageMounter.remove(webApplicationFactory);
    }

    private void reevaluateAllBundles(WebApplicationFactory<?> webApplicationFactory) {
        Collection<ExtendedBundle> bundles = relvantBundles.values();
        for (ExtendedBundle bundle : bundles) {
            addBundleToServicesReference(bundle, webApplicationFactory);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addRelevantBundle(ExtendedBundle bundle) {
        synchronized (this) {
            LOGGER.debug("this bundle is relevant {}",bundle.getID());
            relvantBundles.put(bundle.getID(), bundle);
            for (WebApplicationFactory<?> serviceReference : classResolvers.keySet()) {
                addBundleToServicesReference(bundle, serviceReference);
            }
        }
    }

    private void addBundleToServicesReference(ExtendedBundle bundle,
            WebApplicationFactory<?> webApplicationFactory) {
        try {
            classResolvers.get(webApplicationFactory).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn("A specific reference could not be added to the classResolvers; might not be too bad", e);
        }
        try {
            componentInstanciationListener.get(webApplicationFactory).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn(
                "A specific reference could not be added to the componentInstanciationListener; might not be too bad",
                e);
        }
        try {
            pageMounter.get(webApplicationFactory).addBundle(bundle);
        } catch (Throwable e) {
            LOGGER.warn("A specific reference could not be added to pageMounter; might not be too bad", e);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void removeRelevantBundle(ExtendedBundle bundle) {
        synchronized (this) {
            relvantBundles.remove(bundle.getID());
            removeBundleFromAllServices(bundle);
        }
    }

    private void removeBundleFromAllServices(ExtendedBundle bundle) {
        for (WebApplicationFactory<?> reference : classResolvers.keySet()) {
            classResolvers.get(reference).removeBundle(bundle);
            componentInstanciationListener.get(reference).removeBundle(bundle);
            pageMounter.get(reference).removeBundle(bundle);
        }
    }

}
