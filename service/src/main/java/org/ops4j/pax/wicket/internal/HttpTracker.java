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

import java.util.Dictionary;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.ops4j.pax.wicket.internal.util.MapAsDictionary;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class HttpTracker extends ServiceTracker {

    private static final Logger LOG = LoggerFactory.getLogger(HttpTracker.class);

    private HttpService httpService;
    private final HashMap<String, ServletDescriptor> servlets;

    HttpTracker(BundleContext context) {
        super(context, HttpService.class.getName(), null);
        servlets = new HashMap<String, HttpTracker.ServletDescriptor>();
    }

    @Override
    public final Object addingService(ServiceReference serviceReference) {
        httpService = (HttpService) super.addingService(serviceReference);
        HashMap<String, ServletDescriptor> servletsClone = new HashMap<String, HttpTracker.ServletDescriptor>();
        synchronized (this) {
            servletsClone.putAll(servlets);
        }
        for (Map.Entry<String, ServletDescriptor> entry : servletsClone.entrySet()) {
            ServletDescriptor descriptor = entry.getValue();
            String mountpoint = entry.getKey();
            try {
                registerServletDescriptor(httpService, mountpoint, descriptor);
            } catch (NamespaceException e) {
                throw new IllegalArgumentException(
                    "Unable to mount [" + descriptor.servlet + "] on mount point '" + mountpoint + "'.");
            } catch (ServletException e) {
                String message = "Wicket Servlet [" + descriptor.servlet + "] is unable to initialize. "
                        + "This servlet was tried to be mounted on '" + mountpoint + "'.";
                throw new IllegalArgumentException(message, e);
            }
        }
        return httpService;
    }

    @Override
    public final void removedService(ServiceReference serviceReference, Object httpService) {
        Set<String> mountPoints;
        synchronized (this) {
            mountPoints = new HashSet<String>(servlets.keySet());
        }

        for (String mountpoint : mountPoints) {
            this.httpService.unregister(mountpoint);
        }

        super.removedService(serviceReference, httpService);
    }

    final void addServlet(String mountPoint, Servlet servlet, Map<?, ?> contextParams, Bundle paxWicketBundle)
        throws NamespaceException, ServletException {
        mountPoint = normalizeMountPoint(mountPoint);
        HttpContext httpContext = new GenericContext(paxWicketBundle, mountPoint);
        ServletDescriptor descriptor =
            new ServletDescriptor(servlet, httpContext, contextParams == null ? null
                    : MapAsDictionary.wrap(contextParams));
        synchronized (this) {
            servlets.put(mountPoint, descriptor);
        }
        registerServletDescriptor(httpService, mountPoint, descriptor);

    }

    final synchronized void removeServlet(String mountPoint) {
        mountPoint = normalizeMountPoint(mountPoint);
        if (servlets.remove(mountPoint) != null) {
            if (httpService != null) {
                httpService.unregister(mountPoint);
            }
        }
    }

    /**
     * Register a servlet descriptor with an {@link HttpService} using a {@link ServletDescriptor}
     * 
     * @param service
     * @param descriptor
     * @throws NamespaceException
     * @throws ServletException
     */
    private static void registerServletDescriptor(HttpService service, String mountpoint, ServletDescriptor descriptor)
        throws ServletException, NamespaceException {
        if (service != null) {
            LOG.info("register new servlet on mountpoint {} with contextParams {}", mountpoint,
                descriptor.contextParams);
            service.registerServlet(mountpoint, descriptor.servlet, descriptor.contextParams, descriptor.httpContext);
        }
    }

    private String normalizeMountPoint(String mountPoint) {
        if (!mountPoint.startsWith("/")) {
            mountPoint = "/" + mountPoint;
        }
        return mountPoint;
    }

    final synchronized Servlet getServlet(String mountPoint) {
        mountPoint = normalizeMountPoint(mountPoint);
        ServletDescriptor descriptor = servlets.get(mountPoint);
        return descriptor.servlet;
    }

    private static final class ServletDescriptor {

        private final Servlet servlet;
        private final HttpContext httpContext;
        private final Dictionary<?, ?> contextParams;

        public ServletDescriptor(Servlet aServlet, HttpContext aContext, Dictionary<?, ?> contextParams) {
            servlet = aServlet;
            httpContext = aContext;
            this.contextParams = contextParams;
        }
    }
}
