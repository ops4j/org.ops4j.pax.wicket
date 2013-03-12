/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal.injection.registry;

import java.lang.reflect.Field;
import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBeanFilter;
import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.spi.FutureProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public class OSGiServiceRegistryProxyTargetLocatorFactory implements
        ProxyTargetLocatorFactory.DelayableProxyTargetLocatorFactory {

    public OSGiServiceRegistryProxyTargetLocatorFactory() {
    }

    public String getName() {
        return PaxWicketBeanInjectionSource.INJECTION_SOURCE_SERVICE_REGISTRY;
    }

    public ProxyTargetLocator createProxyTargetLocator(BundleContext context, Field field, Class<?> page,
            Map<String, String> overwrites) {
        OSGiServiceRegistryProxyTargetLocator locator =
            new OSGiServiceRegistryProxyTargetLocator(context, getFilter(context, field),
                field.getType(), page);
        if (locator.fetchReferences() != null) {
            return locator;
        } else {
            return null;
        }
    }

    public FutureProxyTargetLocator createFutureProxyTargetLocator(BundleContext context, Field field,
            Class<?> realFieldType, Class<?> page,
            Map<String, String> overwrites) {
        return new OSGiServiceRegistryProxyTargetLocator(context, getFilter(context, field),
            realFieldType, page);
    }

    private static Filter getFilter(BundleContext context, Field field) {
        PaxWicketBeanFilter annotation = field.getAnnotation(PaxWicketBeanFilter.class);
        if (annotation != null) {
            String value = annotation.value();
            if (value != null && !value.isEmpty()) {
                try {
                    return context.createFilter(value);
                } catch (InvalidSyntaxException e) {
                    throw new IllegalArgumentException("Filterstring is invalid", e);
                }
            }
        }
        return null;
    }

}
