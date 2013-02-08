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

import java.io.IOException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The {@link PAXWicketFilterChain} is responsible for dispatching registered filters if applicable and finally to the
 * {@link Servlet} if all filters are respected
 */
public class PAXWicketFilterChain implements FilterChain {

    private static final Logger LOGGER = LoggerFactory.getLogger(PAXWicketFilterChain.class);

    private int filterIndex = 0;
    private final List<Filter> filters;

    private final Servlet delegateServlet;

    public PAXWicketFilterChain(List<Filter> filter, Servlet delegateServlet) {
        filters = filter;
        this.delegateServlet = delegateServlet;
    }

    public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
        int size = filters.size();
        if (filterIndex < size) {
            Filter filter = filters.get(filterIndex);
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("call filter {}/{} of type {} ", new Object[]{ (filterIndex + 1), size,
                    filter.getClass().getName() });
            }
            filterIndex++;
            filter.doFilter(request, response, this);
        } else {
            LOGGER.debug("No more filters in chain, delegate to servlet");
            delegateServlet.service(request, response);
        }
    }
}
