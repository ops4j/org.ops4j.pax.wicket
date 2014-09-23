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
package org.ops4j.pax.wicket.internal;

import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.internal.extender.BundleDelegatingExtensionTracker;
import org.ops4j.pax.wicket.internal.extender.ExtendedBundle;
import org.ops4j.pax.wicket.internal.extender.PaxWicketBundleListener;
import org.ops4j.pax.wicket.internal.injection.registry.OSGiServiceRegistryProxyTargetLocatorFactory;
import org.ops4j.pax.wicket.internal.util.BundleTrackerAggregator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Activator implements BundleActivator {

    public static final String SYMBOLIC_NAME = "org.ops4j.pax.wicket.service";

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private HttpTracker httpTracker;
    private PaxWicketAppFactoryTracker applicationFactoryTracker;

    private static BundleContext bundleContext;

    private BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker;

    private BundleTrackerAggregator<WebApplicationFactory<?>> bundleTrackerAggregator;

    private BundleTracker<ExtendedBundle> bundleExtensionTracker;

    // private BundleImportExtender bundleImportExtender;

    // private ServiceRegistration<WeavingHook> weavingHockRegistration;

    private ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> proxyFactoryTracker;

    private ServiceRegistration<ProxyTargetLocatorFactory> proxyFactoryService;

    @SuppressWarnings("unchecked")
    public final void start(BundleContext context) throws Exception {
        LOGGER
            .info("Pax Wicket makes uses of Decarative Services starting with this release. Make sure a suitable implementation (e.g. Felix SCR or Equinox DS) is present and started in your framework!");
        LOGGER.debug("Initializing [{}] bundle.", context.getBundle().getSymbolicName());
        bundleContext = context;

        // bundleImportExtender = new BundleImportExtender(context);
        // context.addBundleListener(bundleImportExtender);
        // weavingHockRegistration = context.registerService(WeavingHook.class, bundleImportExtender, null);

        httpTracker = new HttpTracker(context);
        httpTracker.open();

        OSGiServiceRegistryProxyTargetLocatorFactory internalLocatorFactory =
            new OSGiServiceRegistryProxyTargetLocatorFactory();
        proxyFactoryService = context.registerService(ProxyTargetLocatorFactory.class, internalLocatorFactory, null);

        proxyFactoryTracker = new ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory>(bundleContext,
                ProxyTargetLocatorFactory.class, null);
        proxyFactoryTracker.open();
        bundleDelegatingExtensionTracker = new BundleDelegatingExtensionTracker(context, proxyFactoryTracker);
        applicationFactoryTracker = new PaxWicketAppFactoryTracker(context, httpTracker);

        PaxWicketBundleListener paxWicketBundleListener =
            new PaxWicketBundleListener(context, bundleDelegatingExtensionTracker);

        bundleExtensionTracker = new BundleTracker<ExtendedBundle>(context, Bundle.ACTIVE, paxWicketBundleListener);
        bundleExtensionTracker.open();

        bundleTrackerAggregator =
            new BundleTrackerAggregator<WebApplicationFactory<?>>(context, WebApplicationFactory.class.getName(), null,
                bundleDelegatingExtensionTracker, applicationFactoryTracker);
        bundleTrackerAggregator.open(true);
    }

    public static BundleContext getBundleContext() {
        return bundleContext;
    }

    public static BundleContext getBundleContextByBundleId(long bundleId) {
        Bundle bundle = bundleContext.getBundle(bundleId);
        if (bundle != null) {
            return bundle.getBundleContext();
        } else {
            return null;
        }
    }

    public final void stop(BundleContext context) throws Exception {
        // weavingHockRegistration.unregister();
        proxyFactoryService.unregister();
        // context.removeBundleListener(bundleImportExtender);
        bundleExtensionTracker.close();
        bundleTrackerAggregator.close();
        httpTracker.close();
        bundleContext = null;
        LOGGER.debug("Stopped [{}] bundle.", context.getBundle().getSymbolicName());
    }

}
