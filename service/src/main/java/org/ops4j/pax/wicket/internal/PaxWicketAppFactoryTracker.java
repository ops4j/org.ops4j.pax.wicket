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
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;

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
    private final Map<ServiceReference, String> factories = new HashMap<ServiceReference, String>();
    private Map<ServiceReference, FilterDelegator> filterDelegators = new HashMap<ServiceReference, FilterDelegator>();

    PaxWicketAppFactoryTracker(BundleContext context, HttpTracker httpTracker)
        throws IllegalArgumentException {
        super(context, SERVICE_NAME, null);

        validateNotNull(httpTracker, "httpTracker");
        this.httpTracker = httpTracker;
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

        File tmpDir = context.getDataFile("tmp-dir");
        String mountPoint = factory.getMountPoint();
        String applicationName = (String) reference.getProperty(APPLICATION_NAME);
        FilterDelegator filterDelegator =
            new FilterDelegator(reference.getBundle().getBundleContext(), applicationName);
        Servlet servlet = ServletProxy.newServletProxy(factory, tmpDir, mountPoint, filterDelegator, applicationName);
        addServlet(mountPoint, servlet, factory.getContextParams(), reference);

        synchronized (factories) {
            factories.put(reference, mountPoint);
            filterDelegators.put(reference, filterDelegator);
        }

        return factory;
    }

    @Override
    public final void modifiedService(ServiceReference reference, Object service) {
        PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) service;
        String oldMountPoint;
        String oldApplicationName;
        synchronized (factories) {
            oldMountPoint = factories.get(factory);
            oldApplicationName = filterDelegators.get(reference).getApplicationName();
        }

        String newMountPoint = factory.getMountPoint();
        if (!oldMountPoint.equals(newMountPoint)) {
            Servlet servlet = httpTracker.getServlet(oldMountPoint);
            FilterDelegator delegator = filterDelegators.get(reference);
            removedService(reference, service);
            addServlet(newMountPoint, servlet, factory.getContextParams(), reference);
            synchronized (factories) {
                factories.put(reference, newMountPoint);
                filterDelegators.put(reference, delegator);
            }
        }

        String newApplicationName = (String) reference.getProperty(APPLICATION_NAME);
        if (!oldApplicationName.equals(newApplicationName)) {
            File tmpDir = context.getDataFile("tmp-dir");
            FilterDelegator filterDelegator =
                new FilterDelegator(reference.getBundle().getBundleContext(), newApplicationName);
            Servlet servlet =
                ServletProxy.newServletProxy(factory, tmpDir, newMountPoint, filterDelegator, newApplicationName);
            addServlet(newMountPoint, servlet, factory.getContextParams(), reference);

            synchronized (factories) {
                factories.put(reference, newMountPoint);
                filterDelegators.put(reference, filterDelegator);
            }
        }

    }

    @Override
    public final void removedService(ServiceReference reference, Object service) {
        String mountPoint;
        synchronized (factories) {
            mountPoint = factories.remove(reference);
            filterDelegators.get(reference).dispose();
            filterDelegators.remove(reference);
        }

        httpTracker.removeServlet(mountPoint);
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
