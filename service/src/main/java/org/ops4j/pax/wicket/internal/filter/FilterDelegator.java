
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.internal.filter;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ops4j.pax.wicket.api.FilterFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public final class FilterDelegator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterDelegator.class);

    private final ServiceTracker<FilterFactory, FilterFactoryReference> filterTracker;
    private final String applicationName;

    private Servlet servlet;

    /**
     * <p>Constructor for FilterDelegator.</p>
     *
     * @param context a {@link org.osgi.framework.BundleContext} object.
     * @param applicationName a {@link java.lang.String} object.
     */
    public FilterDelegator(BundleContext context, String applicationName) {
        this.applicationName = applicationName;
        FilterTrackerCustomizer customizer = new FilterTrackerCustomizer(context, applicationName);
        filterTracker =
            new ServiceTracker<FilterFactory, FilterFactoryReference>(context, customizer.createOsgiFilter(),
                customizer);
    }

    /**
     * <p>Getter for the field <code>applicationName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * Start delegating calls to filter services
     */
    public void start() {
        filterTracker.open();
    }

    /**
     * Stop (and dispose) delegated filters
     */
    public void stop() {
        filterTracker.close();
    }

    /**
     * <p>doFilter.</p>
     *
     * @param superFilter an array of {@link javax.servlet.Filter} objects.
     * @param servletRequest a {@link javax.servlet.ServletRequest} object.
     * @param servletResponse a {@link javax.servlet.ServletResponse} object.
     * @throws javax.servlet.ServletException if any.
     * @throws java.io.IOException if any.
     */
    public void doFilter(Filter[] superFilter, ServletRequest servletRequest, ServletResponse servletResponse)
        throws ServletException, IOException {
        List<Filter> filters = new ArrayList<Filter>();
        if (superFilter != null && superFilter.length > 0) {
            // First add all superfilter...
            filters.addAll(Arrays.asList(superFilter));
        }
        List<Filter> filterList = getFiltersSortedWithHighestPriorityAsFirstFilter(filters, servlet.getServletConfig());
        FilterChain chain = new PAXWicketFilterChain(filterList, servlet);
        chain.doFilter(servletRequest, servletResponse);
    }

    private List<Filter> getFiltersSortedWithHighestPriorityAsFirstFilter(List<Filter> filters,
            ServletConfig servletConfig) {
        FilterFactoryReference[] factories = filterTracker.getServices(new FilterFactoryReference[0]);
        if (factories != null && factories.length > 0) {
            LOGGER.debug("Retrieved {} factories to create filters to apply", factories.length);
            Arrays.sort(factories);
            for (FilterFactoryReference filterFactory : factories) {
                try {
                    filters.add(filterFactory.getFilter(servletConfig));
                } catch (ServletException e) {
                    LOGGER.error("Problem while creating filter: {}", e.getMessage(), e);
                } catch (RuntimeException e) {
                    LOGGER.error("Problem while creating filter: {}", e.getMessage(), e);
                }
            }
        }
        return filters;
    }

    /**
     * <p>Setter for the field <code>servlet</code>.</p>
     *
     * @param servlet a {@link javax.servlet.Servlet} object.
     */
    public void setServlet(Servlet servlet) {
        validateNotNull(servlet, "servlet");
        this.servlet = servlet;
    }

}
