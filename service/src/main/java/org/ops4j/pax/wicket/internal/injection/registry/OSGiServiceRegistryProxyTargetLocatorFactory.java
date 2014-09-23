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
package org.ops4j.pax.wicket.internal.injection.registry;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.ops4j.pax.wicket.api.PaxWicketBeanFilter;
import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.internal.injection.BundleAnalysingComponentInstantiationListener;
import org.ops4j.pax.wicket.spi.FutureProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Component(service = { ProxyTargetLocatorFactory.class })
public class OSGiServiceRegistryProxyTargetLocatorFactory implements
        ProxyTargetLocatorFactory.DelayableProxyTargetLocatorFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSGiServiceRegistryProxyTargetLocatorFactory.class);

    public OSGiServiceRegistryProxyTargetLocatorFactory() {
    }

    public String getName() {
        return PaxWicketBeanInjectionSource.INJECTION_SOURCE_SERVICE_REGISTRY;
    }

    public ProxyTargetLocator createProxyTargetLocator(BundleContext callingContext, Field field, Class<?> page,
            Map<String, String> overwrites) {
        BundleContext context;
        if (page.getClassLoader() instanceof BundleReference) {
            // Fetch the Bundlecontext of the page class to locate the service
            BundleReference reference = (BundleReference) page.getClassLoader();
            context = reference.getBundle().getBundleContext();
            LOGGER.debug("Using the Bundlereference of class {} for locating services", page);
        } else {
            context = callingContext;
            LOGGER.debug("Using the PAX Wicket BundlereContext for locating services");
        }
        Filter filter = getFilter(context, field);
        // is it am Iterable?
        Class<?> type = field.getType();
        if (type.equals(Iterable.class)) {
            Class<?> argument = BundleAnalysingComponentInstantiationListener.getGenericTypeArgument(field);
            LOGGER.debug("Inject Iterable for type {}", argument);
            return new StaticProxyTargetLocator(createIterable(argument, filter, context), page);
        }
        // or a supported collection...
        if (type.equals(Collection.class) || type.equals(Set.class) || type.equals(List.class)) {
            Class<?> argument = BundleAnalysingComponentInstantiationListener.getGenericTypeArgument(field);
            LOGGER.debug("Inject Collection, Set or List for type {}", argument);
            return new StaticProxyTargetLocator(createCollection(argument, filter, context), page);
        }
        OSGiServiceRegistryProxyTargetLocator locator =
            new OSGiServiceRegistryProxyTargetLocator(context, filter,
                type, page);
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

    /**
     * Creates an {@link Iterable} for the given type and filter using the specified context
     * 
     * @param <T>
     * @param type
     * @param filter
     * @param context
     * @return
     */
    private static <T> ServiceReferenceIterable<T> createIterable(Class<T> type, Filter filter,
            BundleContext context) {
        return new ServiceReferenceIterable<T>(type, filter != null ? filter.toString() : null, context);
    }

    private static <T> ServiceReferenceCollection<T> createCollection(Class<T> type, Filter filter,
            BundleContext context) {
        ServiceReferenceIterable<T> iterable = createIterable(type, filter, context);
        return new ServiceReferenceCollection<T>(iterable);
    }

}
