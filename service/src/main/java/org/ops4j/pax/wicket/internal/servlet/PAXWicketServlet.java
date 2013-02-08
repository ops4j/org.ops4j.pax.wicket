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
package org.ops4j.pax.wicket.internal.servlet;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.ops4j.pax.wicket.internal.PaxWicketApplicationFactory;
import org.ops4j.pax.wicket.internal.PaxWicketFilter;

/**
 * Extension of the {@link WicketServlet} to provide special Pax Wicket features, new instances are created with the
 * static {@link #createServlet(PaxWicketApplicationFactory)} method
 */
public final class PAXWicketServlet extends WicketServlet {

    private static final long serialVersionUID = 1L;

    private static final String WICKET_REQUIRED_ATTRIBUTE = "javax.servlet.context.tempdir";

    private final PaxWicketApplicationFactory appFactory;

    private PAXWicketServlet(PaxWicketApplicationFactory applicationFactory) throws IllegalArgumentException {
        appFactory = applicationFactory;
        applicationFactory.getFilterDelegator().setServlet(this);
    }

    @Override
    protected WicketFilter newWicketFilter() {
        ServletContext servletContext = getServletContext();
        if (servletContext.getAttribute(WICKET_REQUIRED_ATTRIBUTE) == null) {
            servletContext.setAttribute(WICKET_REQUIRED_ATTRIBUTE, appFactory.getTmpDir());
        }
        return new PaxWicketFilter(appFactory);
    }

    @Override
    public String getServletName() {
        return appFactory.getApplicationName();
    }

    @Override
    public String toString() {
        return "Pax Wicket Servlet";
    }

    /**
     * @param applicationFactory
     * @return a new instance for the given {@link PaxWicketApplicationFactory}
     */
    public static Servlet createServlet(PaxWicketApplicationFactory applicationFactory) {
        return new ServletCallInterceptor(applicationFactory, new PAXWicketServlet(applicationFactory));
    }
}
