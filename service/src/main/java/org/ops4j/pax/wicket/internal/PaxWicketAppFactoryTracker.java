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

import static java.lang.System.identityHashCode;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

final class PaxWicketAppFactoryTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketAppFactoryTracker.class);
    private static final String SERVICE_NAME = PaxWicketApplicationFactory.class.getName();

    private final HttpTracker httpTracker;
    private final Map<PaxWicketApplicationFactory, String> factories;

    PaxWicketAppFactoryTracker(BundleContext context, HttpTracker httpTracker)
        throws IllegalArgumentException {
        super(context, SERVICE_NAME, null);

        validateNotNull(httpTracker, "httpTracker");
        this.httpTracker = httpTracker;
        factories = new HashMap<PaxWicketApplicationFactory, String>();
    }

    @Override
    public final Object addingService(ServiceReference reference) {
        final PaxWicketApplicationFactory factory =
            (PaxWicketApplicationFactory) super.addingService(reference);

        if (LOGGER.isDebugEnabled()) {
            int factoryHash = identityHashCode(factory);
            String message = "Service Added [" + reference + "], Factory hash [" + factoryHash + "]";
            LOGGER.debug(message);
        }

        factory.setPaxWicketBundle(context.getBundle());

        File tmpDir = context.getDataFile("tmp-dir");
        String mountPoint = factory.getMountPoint();
        Servlet servlet = null;
        FilterDelegator filterDelegator =
            new FilterDelegator(reference.getBundle().getBundleContext(),
                (String) reference.getProperty(APPLICATION_NAME));
        servlet = ServletProxy.newServletProxy(factory, tmpDir, mountPoint, filterDelegator);
        addServlet(mountPoint, servlet, factory.getContextParams(), reference);

        synchronized (factories) {
            factories.put(factory, mountPoint);
        }

        return factory;
    }

    @Override
    public final void modifiedService(ServiceReference reference, Object service) {
        PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) service;
        String oldMountPoint;
        synchronized (factories) {
            oldMountPoint = factories.get(factory);
        }

        String newMountPoint = factory.getMountPoint();
        if (oldMountPoint.equals(newMountPoint)) {
            return;
        }

        Servlet servlet = httpTracker.getServlet(oldMountPoint);
        removedService(reference, service);
        addServlet(newMountPoint, servlet, factory.getContextParams(), reference);
    }

    @Override
    public final void removedService(ServiceReference reference, Object service) {
        PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) service;
        if (LOGGER.isDebugEnabled()) {
            int factoryHash = identityHashCode(factory);
            String message = "Service removed [" + reference + "], Application hash [" + factoryHash + "]";
            LOGGER.debug(message);
        }

        String mountPoint;
        synchronized (factories) {
            mountPoint = factories.remove(factory);
        }

        httpTracker.removeServlet(mountPoint);
        factory.setPaxWicketBundle(null);
    }

    private void addServlet(String mountPoint, Servlet servlet, Map<?, ?> contextParam,
            ServiceReference appFactoryReference) {
        validateNotEmpty(mountPoint, "mountPoint");
        validateNotNull(servlet, "servlet");
        validateNotNull(appFactoryReference, "appFactoryReference");

        Bundle bundle = appFactoryReference.getBundle();
        try {
            httpTracker.addServlet(mountPoint, servlet, contextParam, bundle);
        } catch (NamespaceException e) {
            throw new IllegalArgumentException(
                "Unable to mount [" + appFactoryReference + "] on mount point '" + mountPoint + "'.");
        } catch (ServletException e) {
            String message = "Wicket Servlet for [" + appFactoryReference + "] is unable to initialize. "
                             + "This servlet was tried to be mounted on '" + mountPoint + "'.";
            throw new IllegalArgumentException(message, e);
        }
    }
}
