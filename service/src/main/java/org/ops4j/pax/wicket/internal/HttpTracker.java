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

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.ops4j.pax.wicket.internal.servlet.ServletDescriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class tracks the HTTPService and provide methods to add/remove a servlet under a given mount-point. Servlets are
 * added/removed whenever a http service is registered
 * 
 * @author Christoph Läubrich
 * 
 */
final class HttpTracker extends ServiceTracker<HttpService, HttpService> {

    private static final Logger LOG = LoggerFactory.getLogger(HttpTracker.class);

    private HttpService httpService;
    private final HashMap<String, ServletDescriptor> servlets = new HashMap<String, ServletDescriptor>();

    HttpTracker(BundleContext context) {
        super(context, HttpService.class.getName(), null);
    }

    @Override
    public final HttpService addingService(ServiceReference<HttpService> serviceReference) {
        // TODO This does not work well with multiple http services!
        httpService = super.addingService(serviceReference);
        synchronized (servlets) {
            for (ServletDescriptor servletDescriptor : servlets.values()) {
                registerServletDescriptor(servletDescriptor);
            }
        }
        return httpService;
    }

    @Override
    public final void removedService(ServiceReference<HttpService> serviceReference, HttpService httpService) {
        // TODO This does not work well with multiple http services!
        synchronized (servlets) {
            for (ServletDescriptor servletDescriptor : servlets.values()) {
                unregisterServletDescriptor(servletDescriptor);
            }
        }
        super.removedService(serviceReference, httpService);
    }

    /**
     * Unregister a servlet descriptor handling runtime exceptions
     * 
     */
    private void unregisterServletDescriptor(ServletDescriptor servletDescriptor) {
        try {
            servletDescriptor.unregister();
        } catch (RuntimeException e) {
            LOG.error(
                "Unregistration of ServletDescriptor under mountpoint {} fails with unexpected RuntimeException!",
                servletDescriptor.getAlias(), e);
        }
    }

    /**
     * Register a servlet descriptor with an {@link HttpService} using a {@link ServletDescriptor}
     * 
     */
    private void registerServletDescriptor(ServletDescriptor servletDescriptor) {
        try {
            servletDescriptor.register(httpService);
        } catch (RuntimeException e) {
            LOG.error(
                "Registration of ServletDescriptor under mountpoint {} fails with unexpected RuntimeException!",
                servletDescriptor.getAlias(), e);
        } catch (ServletException e) {
            LOG.error(
                "Unable to mount servlet on mount point '{}', either it was already registered under the same alias or the init method throws an exception",
                servletDescriptor.getAlias(), e);
        } catch (NamespaceException e) {
            LOG.error(
                "Unable to mount servlet on mount point '{}', another resource is already bound to this alias",
                servletDescriptor.getAlias(), e);
        }
    }

    public final void addServlet(String mountPoint, Servlet servlet, Map<?, ?> contextParams, Bundle paxWicketBundle) {
        mountPoint = GenericContext.normalizeMountPoint(mountPoint);
        ServletDescriptor descriptor =
            new ServletDescriptor(servlet, mountPoint, paxWicketBundle, contextParams);
        synchronized (servlets) {
            ServletDescriptor put = servlets.put(mountPoint, descriptor);
            if (put != null) {
                LOG.warn(
                    "Two servlets are registered under the same mountpoint '{}' the first of them is overwritten by the second call",
                    mountPoint);
                unregisterServletDescriptor(put);
            }
            registerServletDescriptor(descriptor);
        }
    }

    public final void removeServlet(String mountPoint) {
        mountPoint = GenericContext.normalizeMountPoint(mountPoint);
        synchronized (servlets) {
            ServletDescriptor remove = servlets.remove(mountPoint);
            if (remove != null) {
                unregisterServletDescriptor(remove);
            }
        }

    }

}
