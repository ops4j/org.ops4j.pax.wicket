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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.Servlet;
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

    private final Map<ServiceReference, FilterFactory> filterFactories = new HashMap<ServiceReference, FilterFactory>();
    private final String applicationName;
    private Servlet servlet;

    public FilterTracker(BundleContext bundleContext, String applicationName) {
        super(bundleContext, createOsgiFilter(bundleContext, applicationName), null);
        this.applicationName = applicationName;
    }

    @Override
    public final Object addingService(ServiceReference reference) {
        FilterFactory filterFactory = (FilterFactory) super.addingService(reference);
        synchronized (this) {
            filterFactories.put(reference, filterFactory);
        }
        LOGGER.debug("added filterFactory for application {}", applicationName);
        return filterFactory;
    }

    @Override
    public void removedService(ServiceReference reference, Object service) {
        synchronized (this) {
            filterFactories.remove(reference);
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

    public List<Filter> getFiltersSortedWithHighestPriorityAsFirstFilter() {
        FilterFactory[] factories;
        synchronized (this) {
            factories = filterFactories.values().toArray(new FilterFactory[0]);
        }
        List<Filter> filters = new ArrayList<Filter>();
        LOGGER.debug("Retrieved {} factories to create filters to apply", factories.length);
        Arrays.sort(factories);
        for (FilterFactory filterFactory : factories) {
            try {
                filters
                    .add(filterFactory.createFilter(new DefaultConfigurableFilterConfig(servlet.getServletConfig())));
            } catch (RuntimeException e) {
                LOGGER.error("Problem while creating filter: {}", e.getMessage(), e);
            }
        }
        return filters;
    }

    public void setServlet(Servlet servlet) {
        this.servlet = servlet;
    }

}
