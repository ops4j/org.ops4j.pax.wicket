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
package org.ops4j.pax.wicket.spi.support;

import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

public class BundleInjectionProviderHelperDecorator implements InjectionAwareDecorator {

    private String applicationName;
    private BundleContext bundleContext;
    private BundleInjectionProviderHelper bundleInjectionProviderHelper;
    private String injectionSource;
    private ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> tracker;

    public BundleInjectionProviderHelperDecorator() {
    }

    public void start() throws Exception {
        tracker = new ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory>(bundleContext,
            ProxyTargetLocatorFactory.class, null);
        tracker.open();
        bundleInjectionProviderHelper =
            new BundleInjectionProviderHelper(bundleContext, applicationName, injectionSource, tracker);
        bundleInjectionProviderHelper.register();
    }

    public void stop() throws Exception {
        bundleInjectionProviderHelper.dispose();
        tracker.close();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setInjectionSource(String injectionSource) {
        this.injectionSource = injectionSource;
    }

}
