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

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FilterDelegator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterDelegator.class);

    private final FilterTracker filterTracker;
    private final String applicationName;

    private Servlet servlet;

    public FilterDelegator(BundleContext context, String applicationName) {
        this.applicationName = applicationName;
        filterTracker = new FilterTracker(context, applicationName);
        filterTracker.open();
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws ServletException, IOException {
        FilterChain chain = new Chain(filterTracker.getFiltersSortedWithHighestPriorityAsFirstFilter());
        chain.doFilter(servletRequest, servletResponse);
    }

    private class Chain implements FilterChain {
        private int filterIndex = 0;
        private List<Filter> filters;

        public Chain(List<Filter> filter) {
            filters = filter;
        }

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (filterIndex < filters.size()) {
                Filter filter = filters.get(filterIndex);
                LOGGER.debug("call filter {} of type {} ", filterIndex, filter.getClass().getName());
                filterIndex++;
                filter.doFilter(request, response, this);
            } else {
                servlet.service(request, response);
            }
        }
    }

    public void setServlet(Servlet servlet) {
        validateNotNull(servlet, "servlet");
        this.servlet = servlet;
        filterTracker.setServlet(servlet);
    }

    public void dispose() {
        filterTracker.close();
    }

}
