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

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.osgi.framework.Constants.OBJECTCLASS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import javax.servlet.Filter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.ops4j.pax.wicket.api.FilterFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FilterTracker extends ServiceTracker {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterTracker.class);

    private final Map<ServiceReference, FilterFactoryReference> filterFactories =
        new HashMap<ServiceReference, FilterFactoryReference>();
    private final String applicationName;

    public FilterTracker(BundleContext bundleContext, String applicationName) {
        super(bundleContext, createOsgiFilter(bundleContext, applicationName), null);
        this.applicationName = applicationName;
    }

    @Override
    public final Object addingService(ServiceReference reference) {
        FilterFactory filterFactory = (FilterFactory) super.addingService(reference);
        if (filterFactory != null) {
            Object property = reference.getProperty(FilterFactory.MAINTAIN_LIFECYCLE);
            boolean maintainLifeCycle;
            if (property != null) {
                maintainLifeCycle = Boolean.parseBoolean(property.toString());
            } else {
                maintainLifeCycle = true;
            }
            synchronized (this) {
                filterFactories.put(reference, new FilterFactoryReference(filterFactory, maintainLifeCycle));
            }
            LOGGER.debug("added filterFactory for application {}", applicationName);
        }
        return filterFactory;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        synchronized (this) {
            FilterFactoryReference removed = filterFactories.remove(reference);
            if (removed != null) {
                removed.dispose();
            }
        }
        LOGGER.debug("removed filterFactory for application {}", applicationName);
        super.removedService(reference, service);
    }

    private static org.osgi.framework.Filter createOsgiFilter(BundleContext bundleContext, String applicationName)
        throws IllegalArgumentException {
        validateNotNull(bundleContext, "bundleContext");
        validateNotEmpty(applicationName, "applicationName");

        org.osgi.framework.Filter filter;
        try {
            String filterString = String.format("(&(%s=%s)(%s=%s))", APPLICATION_NAME, applicationName,
                OBJECTCLASS, FilterFactory.class.getName());
            LOGGER.debug("apply FilterTracker with OsgiFilter={} for application {}", filterString, applicationName);
            filter = bundleContext.createFilter(filterString);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("applicationName can not contain '*', '(' or ')' : " + applicationName);
        }
        return filter;
    }

    public List<Filter> getFiltersSortedWithHighestPriorityAsFirstFilter(ServletConfig servletConfig) {
        FilterFactoryReference[] factories;
        synchronized (this) {
            factories = filterFactories.values().toArray(new FilterFactoryReference[0]);
        }
        List<Filter> filters = new ArrayList<Filter>();
        LOGGER.debug("Retrieved {} factories to create filters to apply", factories.length);
        Arrays.sort(factories);
        for (FilterFactoryReference filterFactory : factories) {
            try {
                filters
                    .add(filterFactory.getFilter(servletConfig));
            } catch (ServletException e) {
                LOGGER.error("Problem while creating filter: {}", e.getMessage(), e);
            } catch (RuntimeException e) {
                LOGGER.error("Problem while creating filter: {}", e.getMessage(), e);
            }
        }
        return filters;
    }

    /**
     * A {@link FilterFactoryReference} is a reference to a {@link FilterFactory} and maintains creation/caching nad
     * detroing of new Filterinstances
     * 
     * @author Christoph Läubrich
     * 
     */
    private static class FilterFactoryReference {
        private final FilterFactory factory;

        // TODO: Is there a way PAX Wicket can inform us when it no longer uses a ServletConfig? Maybe in the destroy
        // method of a servlet/application?
        /**
         * We use a {@link WeakHashMap} here since we have no ide when the {@link ServletConfig} might be no longer in
         * use! The only problem with this is, that the destroy method of the filter is not called in such a case.
         */
        private final Map<ServletConfig, Filter> filterCache = new WeakHashMap<ServletConfig, Filter>(1);

        private final boolean maintainLifeCycle;

        /**
         * @param maintainLifeCycle
         * 
         */
        public FilterFactoryReference(FilterFactory factory, boolean maintainLifeCycle) {
            this.factory = factory;
            this.maintainLifeCycle = maintainLifeCycle;
        }

        /**
         * dispose this {@link FilterFactoryReference} and free all resources
         */
        public void dispose() {
            LOGGER.debug("dispose all Filters for FilterFactory {}...", factory.getClass().getName());
            synchronized (filterCache) {
                if (maintainLifeCycle) {
                    Collection<Filter> values = filterCache.values();
                    for (Filter filter : values) {
                        try {
                            filter.destroy();
                        } catch (RuntimeException e) {
                            LOGGER.warn("RuntimeException while calling destroy() of filter {}", filter.getClass()
                                .getName(), e);
                        }
                    }
                }
                filterCache.clear();
            }
        }

        public Filter getFilter(ServletConfig servletConfig) throws ServletException {
            synchronized (filterCache) {
                Filter f = filterCache.get(servletConfig);
                if (f == null) {
                    DefaultConfigurableFilterConfig filterConfig = new DefaultConfigurableFilterConfig(servletConfig);
                    f = factory.createFilter(filterConfig);
                    if (maintainLifeCycle) {
                        f.init(filterConfig);
                    }
                    filterCache.put(servletConfig, f);
                }
                return f;
            }

        }
    }

}
