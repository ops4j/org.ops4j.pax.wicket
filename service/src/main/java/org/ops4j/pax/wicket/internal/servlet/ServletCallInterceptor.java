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

import static java.lang.reflect.Proxy.newProxyInstance;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.ops4j.pax.wicket.api.SuperFilter;
import org.ops4j.pax.wicket.internal.DefaultConfigurableFilterConfig;
import org.ops4j.pax.wicket.internal.PaxWicketApplicationFactory;
import org.ops4j.pax.wicket.internal.filter.FilterDelegator;
import org.ops4j.pax.wicket.internal.filter.PAXWicketFilterChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Intercepts calls to a servlet before they are given to an underlying servlet, the initilization of
 * {@link SuperFilter}s is done here
 * 
 */
public class ServletCallInterceptor implements Servlet {

    private static final Logger LOG = LoggerFactory.getLogger(ServletCallInterceptor.class);

    private static final Class<?>[] REQUEST_INTERFACES = new Class[]{
        HttpServletRequest.class
    };

    private final PaxWicketApplicationFactory applicationFactory;
    private final Servlet delegateServlet;

    private Filter[] superFilter;

    /**
     * @param applicationFactory
     * @param delegateServlet
     */
    public ServletCallInterceptor(PaxWicketApplicationFactory applicationFactory, Servlet delegateServlet) {
        this.applicationFactory = applicationFactory;
        this.delegateServlet = delegateServlet;
    }

    public void init(ServletConfig config) throws ServletException {
        LOG.info("Init servlet...");
        // We must init the superfilters first...
        List<SuperFilter> superFilterList = applicationFactory.getSuperFilterList();
        Filter[] superFilter = new Filter[superFilterList.size()];
        if (superFilter.length > 0) {
            LOG.info("Init {} superfilters...", superFilter.length);
            for (int i = 0; i < superFilter.length; i++) {
                SuperFilter annotation = superFilterList.get(i);
                Class<? extends Filter> filterClass = annotation.filter();
                String initParameterPath = annotation.initParameter();
                // Create a new instance...
                try {
                    superFilter[i] = filterClass.newInstance();
                } catch (InstantiationException e) {
                    throw new ServletException("can't instantiate Filter " + filterClass, e);
                } catch (IllegalAccessException e) {
                    throw new ServletException("can't access Filter " + filterClass + " constructor", e);
                }
                // init it ...
                DefaultConfigurableFilterConfig filterConfig =
                    new DefaultConfigurableFilterConfig(filterClass.getName() + "-" + i, config);
                if (initParameterPath != null && initParameterPath.length() > 0) {
                    // Fetch all classlaoder...
                    ClassLoader appLoader = applicationFactory.getWebApplicationFactory().getClass()
                        .getClassLoader();
                    ClassLoader filterLoader = filterClass.getClassLoader();
                    ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                    // read the properties
                    Properties properties =
                        readProperties(initParameterPath, appLoader, filterLoader, contextClassLoader);
                    // create filter config
                    Set<String> propertyNames = properties.stringPropertyNames();
                    for (String property : propertyNames) {
                        filterConfig.putInitParameter(property, properties.getProperty(property));
                    }
                }
                if (LOG.isInfoEnabled()) {
                    StringBuilder sb = new StringBuilder();
                    Enumeration<?> parameterNames = filterConfig.getInitParameterNames();
                    while (parameterNames.hasMoreElements()) {
                        sb.append("\r\n\t");
                        Object object = parameterNames.nextElement();
                        sb.append(object);
                        sb.append(": ");
                        sb.append(filterConfig.getInitParameter(object.toString()));
                    }
                    LOG.info("Init super filter {}/{} (class = {}) init parameter = {}", new Object[]{ i + 1,
                        superFilter.length, filterClass, sb });
                }
                superFilter[i].init(filterConfig);
            }
        }
        this.superFilter = superFilter;
        // now init the delegate
        delegateServlet.init(config);
    }

    /**
     * @param initParameterPath
     * @param classLoader
     * @return
     * @throws ServletException
     */
    private Properties readProperties(String initParameterPath, ClassLoader... classLoader) throws ServletException {
        Properties properties = new Properties();
        for (ClassLoader loader : classLoader) {
            InputStream stream = loader.getResourceAsStream(initParameterPath);
            if (stream != null) {
                try {
                    properties.load(stream);
                } catch (IOException e) {
                    throw new ServletException("can't load init parameter '" + initParameterPath + "'", e);
                }
            }
        }
        return properties;
    }

    public ServletConfig getServletConfig() {
        return delegateServlet.getServletConfig();
    }

    public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
        // Check if we should replace this request
        if (req instanceof HttpServletRequest) {
            HttpServletRequest httpServletRequest = (HttpServletRequest) req;
            if (!isProxied(httpServletRequest)) {
                req = newProxyRequest(httpServletRequest);
            }
        }
        // Start the filter process...
        FilterDelegator filterDelegator = applicationFactory.getFilterDelegator();
        if (filterDelegator != null) {
            filterDelegator.doFilter(superFilter, req, res);
        } else if (superFilter.length > 0) {
            FilterChain chain = new PAXWicketFilterChain(Arrays.asList(superFilter), delegateServlet);
            chain.doFilter(req, res);
        } else {
            delegateServlet.service(req, res);
        }
    }

    /**
     * Check if this request is already proxied by pax wicket
     * 
     * @param httpServletRequest
     * @return true if the instance is a proxy and invocationhandler is a {@link ServletRequestInvocationHandler}
     */
    private boolean isProxied(HttpServletRequest httpServletRequest) {
        if (Proxy.isProxyClass(httpServletRequest.getClass())) {
            InvocationHandler handler = Proxy.getInvocationHandler(httpServletRequest);
            return handler instanceof ServletRequestInvocationHandler;
        }
        return false;
    }

    public String getServletInfo() {
        return delegateServlet.getServletInfo();
    }

    public void destroy() {
        // destroy all filter first...
        for (Filter filter : superFilter) {
            try {
                filter.destroy();
            } catch (RuntimeException e) {
                LOG.warn("destroying a filter failed", e);
            }
        }
        // destroy the delegate...
        delegateServlet.destroy();
    }

    private HttpServletRequest newProxyRequest(HttpServletRequest request) {
        ClassLoader loader = getClass().getClassLoader();
        ServletRequestInvocationHandler ih =
                new ServletRequestInvocationHandler(request, applicationFactory.getMountPoint());
        return (HttpServletRequest) newProxyInstance(loader, REQUEST_INTERFACES, ih);
    }

}
