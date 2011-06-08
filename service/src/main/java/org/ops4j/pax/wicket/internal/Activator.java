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
import org.ops4j.pax.wicket.internal.serialization.SerializationActivator;
import org.ops4j.pax.wicket.util.serialization.PaxWicketObjectStreamFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    private HttpTracker httpTracker;
    private ServiceTracker applicationFactoryTracker;
    private SerializationActivator serializationActivator;

    public final void start(BundleContext context) throws Exception {
        if (LOGGER.isDebugEnabled()) {
            Bundle bundle = context.getBundle();
            String bundleSymbolicName = bundle.getSymbolicName();

            LOGGER.debug("Initializing [" + bundleSymbolicName + "] bundle.");
        }

        LOGGER.debug("Set object stream factory");
        Objects.setObjectStreamFactory(new PaxWicketObjectStreamFactory(true));

        httpTracker = new HttpTracker(context);
        httpTracker.open();

        applicationFactoryTracker = new PaxWicketAppFactoryTracker(context, httpTracker);
        applicationFactoryTracker.open();

        serializationActivator = new SerializationActivator();
        serializationActivator.start(context);
        context.getBundle().getResources("pathToResource");
    }

    public final void stop(BundleContext context) throws Exception {
        serializationActivator.stop(context);
        httpTracker.close();
        applicationFactoryTracker.close();

        serializationActivator = null;
        httpTracker = null;
        applicationFactoryTracker = null;

        if (LOGGER.isDebugEnabled()) {
            Bundle bundle = context.getBundle();
            String bundleSymbolicName = bundle.getSymbolicName();

            LOGGER.debug("Bundle [" + bundleSymbolicName + "] stopped.");
        }
    }

}
