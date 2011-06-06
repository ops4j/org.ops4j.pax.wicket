/*
 * Copyright 2011 Fabian Souczek
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

package org.ops4j.pax.wicket.internal;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.io.File;
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

import org.ops4j.pax.wicket.api.FilterConfiguration;
import org.ops4j.pax.wicket.api.FilterDescription;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class FilterDelegator {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilterDelegator.class);

    private FilterConfiguration filterConfiguration;
    private FilterTracker filterTracker;

    private Servlet servlet;

    public FilterDelegator(BundleContext context, FilterConfiguration filterConfiguration, File tmpDir,
            String mountPoint, String applicationName) {
        validateNotNull(filterConfiguration, "filterConfiguration");

        this.filterConfiguration = filterConfiguration;
        filterTracker = new FilterTracker(context, applicationName);
        filterTracker.open();
    }

    public void doFilter(HttpServletRequest servletRequest, HttpServletResponse servletResponse)
        throws ServletException, IOException {
        FilterChain chain = new Chain(filterConfiguration.getFilters());
        chain.doFilter(servletRequest, servletResponse);
    }

    private class Chain implements FilterChain {
        private int m_filterIndex = 0;
        private final List<FilterDescription> m_filterDescList;

        Chain(final List<FilterDescription> filterDescList) {
            m_filterDescList = filterDescList;
        }

        public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
            if (m_filterIndex < m_filterDescList.size()) {
                FilterDescription filterDesc = m_filterDescList.get(m_filterIndex);
                LOGGER.debug("call filter {} of type {} ", m_filterIndex, filterDesc.getClassName());
                m_filterIndex++;

                Filter filter = getFilter(filterDesc);
                filter.doFilter(request, response, this);
            } else {
                servlet.service(request, response);
            }
        }
    }

    private Filter getFilter(FilterDescription filterDesc) throws ServletException {
        Filter filter = filterTracker.getFilter(filterDesc.getClassName());
        if (filter == null && filterDesc.isRequired()) {
            throw new ServletException(
                String.format("required filter %s is not available", filterDesc.getClassName()));
        }
        return filter;
    }

    public void setServlet(Servlet servlet) {
        validateNotNull(servlet, "servlet");
        this.servlet = servlet;
    }

}
