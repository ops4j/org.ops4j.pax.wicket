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
 *
 * @author nmw
 * @version $Id: $Id
 */
public final class ServletDescriptor {

    private static final Logger LOG = LoggerFactory.getLogger(ServletDescriptor.class);

    private final Servlet servlet;
    private final HttpContext httpContext;
    private final Dictionary<?, ?> contextParams;
    private final String alias;
    private HttpService service;

    /**
     * <p>Constructor for ServletDescriptor.</p>
     *
     * @param servlet a {@link javax.servlet.Servlet} object.
     * @param alias a {@link java.lang.String} object.
     * @param bundle a {@link org.osgi.framework.Bundle} object.
     * @param contextParams a {@link java.util.Map} object.
     */
    public ServletDescriptor(Servlet servlet, String alias, Bundle bundle,
                Map<?, ?> contextParams) {
        this.servlet = servlet;
        this.alias = alias;
        this.httpContext = new GenericContext(bundle, alias);
        this.contextParams = contextParams == null ? null
                : MapAsDictionary.wrap(contextParams);
    }

    /**
     * register the service with the given {@link org.osgi.service.http.HttpService} if not already registered and the given service is not
     * <code>null</code>
     *
     * @param service a {@link org.osgi.service.http.HttpService} object.
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws javax.servlet.ServletException if the servlet's init method throws an exception, or the given servlet object has
     *         already been registered at a different alias.
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws org.osgi.service.http.NamespaceException if the registration fails because the alias is already in use.
     * @throws org.osgi.service.http.NamespaceException when the servlet is currently registered under a different {@link org.osgi.service.http.HttpService}
     * @throws java.lang.IllegalStateException if any.
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
            LOG.info("unregister servlet on mountpoint {} with contextParams {}", getAlias(),
                contextParams);
            this.service.unregister(getAlias());
            this.service = null;
        }
    }

    /**
     * <p>Getter for the field <code>alias</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getAlias() {
        return alias;
    }
}
