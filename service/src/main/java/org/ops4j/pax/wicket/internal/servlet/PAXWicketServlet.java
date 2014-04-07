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

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.ops4j.pax.wicket.internal.PaxWicketApplicationFactory;

/**
 * This Servlet sets required Wicket {@link ServletContext#setAttribute(String, Object)} values and delegates to the
 * underlying {@link WicketFilter}, new instances are created with the static
 * {@link #createServlet(PaxWicketApplicationFactory)} method
 */
public final class PAXWicketServlet implements Servlet {

    private static final long serialVersionUID = 1L;

    private static final String WICKET_REQUIRED_ATTRIBUTE = "javax.servlet.context.tempdir";

    private final PaxWicketApplicationFactory appFactory;

    private ServletConfig config;

    private final Filter wickFilter;

    private PAXWicketServlet(PaxWicketApplicationFactory applicationFactory, Filter wickFilter)
        throws IllegalArgumentException {
        appFactory = applicationFactory;
        this.wickFilter = wickFilter;
        applicationFactory.getFilterDelegator().setServlet(this);
    }

    public void init(final ServletConfig config) throws ServletException {
        this.config = config;
        ServletContext servletContext = config.getServletContext();
        if (servletContext.getAttribute(WICKET_REQUIRED_ATTRIBUTE) == null) {
            servletContext.setAttribute(WICKET_REQUIRED_ATTRIBUTE, appFactory.getTmpDir());
        }
        wickFilter.init(new FilterConfig() {

            public ServletContext getServletContext() {
                return config.getServletContext();
            }

            public Enumeration<?> getInitParameterNames() {
                return config.getInitParameterNames();
            }

            public String getInitParameter(String name) {
                return config.getInitParameter(name);
            }

            public String getFilterName() {
                return appFactory.getApplicationName();
            }
        });
    }

    public ServletConfig getServletConfig() {
        return config;
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        // First delegate to wicket, at last resort serve 404 error
        wickFilter.doFilter(req, res, new FilterChain() {

            public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
                if (!response.isCommitted()) {
                    if (response instanceof HttpServletResponse) {
                        response.reset();
                        ((HttpServletResponse) response).sendError(404);
                    }
                }
            }
        });
    }

    public String getServletInfo() {
        return toString() + " - " + appFactory.getApplicationName();
    }

    public void destroy() {
        wickFilter.destroy();
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
        Enhancer e = new Enhancer();
        e.setSuperclass(applicationFactory.getFilterClass());
        e.setCallback(new WicketFilterCallback(applicationFactory));
        PAXWicketServlet delegateServlet = new PAXWicketServlet(applicationFactory, (Filter) e.create());
        return new ServletCallInterceptor(applicationFactory, delegateServlet);
    }

    private static class WicketFilterCallback implements MethodInterceptor {

        private final IWebApplicationFactory applicationFactory;

        public WicketFilterCallback(IWebApplicationFactory applicationFactory) {
            this.applicationFactory = applicationFactory;
        }

        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            method.setAccessible(true);
            if (method.getName().equals("getApplicationFactory")) {
                return applicationFactory;
            }
            try {
                return methodProxy.invokeSuper(object, args);
            } catch (InvocationTargetException e) {
                throw e.getTargetException();
            }
        }

    }
}
