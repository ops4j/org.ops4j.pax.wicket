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
/**
 * 
 */
package org.ops4j.pax.wicket.samples.ds.internal;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.ops4j.pax.wicket.api.ConfigurableFilterConfig;
import org.ops4j.pax.wicket.api.FilterFactory;

/**
 * @author Christoph Läubrich
 */
public class PAXWicketFilterFactory implements FilterFactory {

    /* (non-Javadoc)
     * @see org.ops4j.pax.wicket.api.FilterFactory#createFilter(org.ops4j.pax.wicket.api.ConfigurableFilterConfig)
     */
    public Filter createFilter(ConfigurableFilterConfig filterConfig) throws ServletException {
        return new Filter() {

            public void init(FilterConfig filterConfig) throws ServletException {
                //Do init tasks here
            }

            public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
                //do filter here
                chain.doFilter(request, response);
            }

            public void destroy() {
                //do destroy tasks here
            }
        };
    }

}
