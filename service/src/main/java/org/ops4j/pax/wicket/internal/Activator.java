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

import org.apache.wicket.util.lang.Objects;
import org.ops4j.pax.wicket.internal.extender.BundleDelegatingExtensionTracker;
import org.ops4j.pax.wicket.internal.extender.PaxWicketBundleListener;
import org.ops4j.pax.wicket.util.serialization.PaxWicketObjectStreamFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Activator implements BundleActivator {

    public static final String SYMBOLIC_NAME = "org.ops4j.pax.wicket.service";

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private HttpTracker httpTracker;
    private ServiceTracker applicationFactoryTracker;

    private static BundleContext bundleContext;

    private PaxWicketBundleListener paxWicketBundleListener;

    private BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker;

    public final void start(BundleContext context) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            Bundle bundle = context.getBundle();
            String bundleSymbolicName = bundle.getSymbolicName();

            LOGGER.debug("Initializing [" + bundleSymbolicName + "] bundle.");
        }

        bundleContext = context;

        LOGGER.debug("Set object stream factory");
        //Objects.setObjectStreamFactory(new PaxWicketObjectStreamFactory(true));

        httpTracker = new HttpTracker(context);
        httpTracker.open();

        applicationFactoryTracker = new PaxWicketAppFactoryTracker(context, httpTracker);
        applicationFactoryTracker.open(true);

        bundleDelegatingExtensionTracker = new BundleDelegatingExtensionTracker(context);
        bundleDelegatingExtensionTracker.open(true);

        paxWicketBundleListener = new PaxWicketBundleListener(bundleDelegatingExtensionTracker);
        context.addBundleListener(paxWicketBundleListener);
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
        context.removeBundleListener(paxWicketBundleListener);
        httpTracker.close();
        applicationFactoryTracker.close();
        bundleDelegatingExtensionTracker.close();

        httpTracker = null;
        applicationFactoryTracker = null;
        bundleDelegatingExtensionTracker = null;
        bundleContext = null;

        if (LOGGER.isDebugEnabled()) {
            Bundle bundle = context.getBundle();
            String bundleSymbolicName = bundle.getSymbolicName();

            LOGGER.debug("Bundle [" + bundleSymbolicName + "] stopped.");
        }
    }

}
