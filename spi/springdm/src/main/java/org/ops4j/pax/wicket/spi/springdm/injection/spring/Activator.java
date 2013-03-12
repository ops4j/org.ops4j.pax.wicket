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
package org.ops4j.pax.wicket.spi.springdm.injection.spring;

import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Activator implements BundleActivator {

    private static final Logger LOGGER = LoggerFactory.getLogger(Activator.class);
    private ServiceRegistration<ProxyTargetLocatorFactory> serviceRegistration;

    public final void start(BundleContext context) throws Exception {
        serviceRegistration =
            context.registerService(ProxyTargetLocatorFactory.class, new SpringDMProxyTargetLocatorFactory(), null);
        LOGGER.info("registered Spring DM injection SPI for PAX Wicket.");
    }

    public final void stop(BundleContext context) throws Exception {
        serviceRegistration.unregister();
        LOGGER.info("unregistered Spring DM injection SPI for PAX Wicket.");
    }

}
