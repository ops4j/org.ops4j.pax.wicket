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

import java.util.Dictionary;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.ops4j.pax.wicket.internal.GenericContext;
import org.ops4j.pax.wicket.internal.util.MapAsDictionary;
import org.osgi.framework.Bundle;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Encapsulate the registration information for a servlet
 */
public final class ServletDescriptor {

    private static final Logger LOG = LoggerFactory.getLogger(ServletDescriptor.class);

    private final Servlet servlet;
    private final HttpContext httpContext;
    private final Dictionary<?, ?> contextParams;
    private final String alias;
    private HttpService service;

    public ServletDescriptor(Servlet servlet, String alias, Bundle bundle,
                Map<?, ?> contextParams) {
        this.servlet = servlet;
        this.alias = alias;
        this.httpContext = new GenericContext(bundle, alias);
        this.contextParams = contextParams == null ? null
                : MapAsDictionary.wrap(contextParams);
    }

    /**
     * register the service with the given {@link HttpService} if not already registered and the given service is not
     * <code>null</code>
     * 
     * @param service
     * @throws NamespaceException if the registration fails because the alias is already in use.
     * @throws ServletException if the servlet's init method throws an exception, or the given servlet object has
     *         already been registered at a different alias.
     * @throws NamespaceException when the servlet is currently registered under a different {@link HttpService}
     */
    public synchronized void register(HttpService service) throws ServletException, NamespaceException,
            IllegalStateException {
        if (service != null) {
            if (this.service == null) {
                LOG.info("register new servlet on mountpoint {} with contextParams {}", getAlias(),
                        contextParams);
                service.registerServlet(getAlias(), servlet, contextParams, httpContext);
                this.service = service;
            } else {
                if (this.service != service) {
                    throw new IllegalStateException("the servlet is already registered with another HttpService");
                }
            }
        }
    }

    /**
     * Unregister a servlet if already registered. After this call it is save to register the servlet again
     */
    public synchronized void unregister() {
        if (this.service != null) {
            this.service.unregister(getAlias());
            this.service = null;
        }
    }

    public String getAlias() {
        return alias;
    }
}
