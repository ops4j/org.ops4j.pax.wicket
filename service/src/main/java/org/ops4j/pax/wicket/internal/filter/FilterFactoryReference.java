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
package org.ops4j.pax.wicket.internal.filter;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.ops4j.pax.wicket.api.FilterFactory;
import org.ops4j.pax.wicket.internal.DefaultConfigurableFilterConfig;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A {@link FilterFactoryReference} is a reference to a {@link FilterFactory} and maintains creation/caching nad
 * detroing of new Filterinstances
 * 
 */
public class FilterFactoryReference implements Comparable<FilterFactoryReference> {

    private static final Logger LOGGER = LoggerFactory.getLogger(FilterFactoryReference.class);

    private final FilterFactory factory;

    private final Map<ServletConfig, Filter> filterCache = new HashMap<ServletConfig, Filter>(1);

    private boolean maintainLifeCycle;

    private long serviceRanking;

    private long priority;

    private long serviceID;

    /**
     * @param factory
     */
    public FilterFactoryReference(FilterFactory factory) {
        this.factory = factory;
    }

    /**
     * set the properties for this reference from the given {@link ServiceReference}
     * 
     * @param reference
     */
    public void setProperties(ServiceReference<FilterFactory> reference) {
        { // set the lifecycle property
            Object property = reference.getProperty(FilterFactory.MAINTAIN_LIFECYCLE);
            if (property != null) {
                maintainLifeCycle = Boolean.parseBoolean(property.toString());
            } else {
                maintainLifeCycle = true;
            }
            serviceRanking = getInteger(reference.getProperty(Constants.SERVICE_RANKING), 0);
            priority = getInteger(reference.getProperty(FilterFactory.FILTER_PRIORITY), 0);
            serviceID = getInteger(reference.getProperty(Constants.SERVICE_ID), 0);
        }
    }

    /**
     * @param property
     * @param defaultValue
     * @return
     */
    private long getInteger(Object property, int defaultValue) {
        if (property instanceof Number) {
            return ((Number) property).longValue();
        }
        if (property instanceof String) {
            try {
                return Long.parseLong((String) property);
            } catch (NumberFormatException nfe) {
                // We don't care then...
                LOGGER.debug("can't parse property as integer: {}", property, nfe);
            }
        }
        return defaultValue;
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

    /**
     * @return the current value of factory
     */
    public FilterFactory getFactory() {
        return factory;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(FilterFactoryReference o) {
        long cmp = serviceRanking - o.serviceRanking;
        if (cmp == 0) {
            cmp = priority - o.priority;
            if (cmp == 0) {
                // We use the service id here, to archive consistent/stable ordering of filters
                cmp = serviceID - o.serviceID;
            }
        }
        // "convert" the long to the integer contract of compareTo
        if (cmp > 0) {
            return 1;
        }
        if (cmp < 0) {
            return -1;
        }
        return 0;
    }
}
