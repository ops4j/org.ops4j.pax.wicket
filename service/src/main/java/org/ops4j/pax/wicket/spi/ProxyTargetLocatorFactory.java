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
package org.ops4j.pax.wicket.spi;

import java.lang.reflect.Field;
import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.osgi.framework.BundleContext;

/**
 * Services registered under this interface can participate in providing {@link ProxyTargetLocator}s
 * 
 */
public interface ProxyTargetLocatorFactory {

    /**
     * @return the name for this factory. The name could be used to specify a special injection source see
     *         {@link PaxWicketBeanInjectionSource#value()}
     */
    public String getName();

    /**
     * This creates the {@link ProxyTargetLocator} for the given field, page and overrides
     * 
     * @param context the bundle context of the requesting bundle
     * @param field the field that sould be injected
     * @param page the page the field will be injected into
     * @param overwrites TODO what is this?
     * @return the {@link ProxyTargetLocator} used to inject content to field or <code>null</code> if this factory can't
     *         inject the field
     */
    public ProxyTargetLocator createProxyTargetLocator(BundleContext context, Field field, Class<?> page,
            Map<String, String> overwrites);

    /**
     * 
     * Services registered unter the {@link ProxyTargetLocatorFactory} interface, that register this interface also ar
     * promoting their capability to create locators that might resolve in the future
     * 
     */
    public interface DelayableProxyTargetLocatorFactory extends ProxyTargetLocatorFactory {

        public FutureProxyTargetLocator createFutureProxyTargetLocator(BundleContext context, Field field,
                Class<?> realFieldType,
                Class<?> page,
                Map<String, String> overwrites);
    }
}
