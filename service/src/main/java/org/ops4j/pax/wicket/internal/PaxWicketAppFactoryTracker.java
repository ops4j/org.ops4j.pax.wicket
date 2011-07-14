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
import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.Servlet;
import javax.servlet.ServletException;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory tracker waits for every new {@link IWebApplicationFactory} class registered as OSGi service. If the
 * services does also contain properties for an application name and a mount point is is registered for a Servlet.
 * Otherwise the problem is logged as a warning and the service is simply ignored.
 */
public class PaxWicketAppFactoryTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketAppFactoryTracker.class);
    private static final String SERVICE_NAME = IWebApplicationFactory.class.getName();

    private final HttpTracker httpTracker;
    private final Map<ServiceReference, PaxWicketApplicationFactory> factories =
        new HashMap<ServiceReference, PaxWicketApplicationFactory>();

    PaxWicketAppFactoryTracker(BundleContext context, HttpTracker httpTracker)
        throws IllegalArgumentException {
        super(context, SERVICE_NAME, null);

        validateNotNull(httpTracker, "httpTracker");
        this.httpTracker = httpTracker;
    }

    @Override
    public final Object addingService(ServiceReference reference) {
        final IWebApplicationFactory factory = (IWebApplicationFactory) super.addingService(reference);
        PaxWicketApplicationFactory internalFactory =
            PaxWicketApplicationFactory.createPaxWicketApplicationFactory(context, factory, reference);
        addApplication(reference, internalFactory);
        return factory;
    }

    @Override
    public final void modifiedService(ServiceReference reference, Object service) {
        removeApplication(reference);
        PaxWicketApplicationFactory internalFactory =
            PaxWicketApplicationFactory.createPaxWicketApplicationFactory(context, (IWebApplicationFactory) service,
                reference);
        addApplication(reference, internalFactory);
    }

    @Override
    public final void removedService(ServiceReference reference, Object service) {
        removeApplication(reference);
    }

    private void addApplication(ServiceReference reference, PaxWicketApplicationFactory internalFactory) {
        if (!internalFactory.isValidFactory()) {
            LOGGER
                .warn("Trying to register ApplicationFactory without application name or mount point is not possible");
            return;
        }
        LOGGER.debug("Service Added [{}], Factory hash [{}]", reference, identityHashCode(internalFactory));
        Servlet servlet = ServletProxy.newServletProxy(internalFactory);
        addServlet(internalFactory.getMountPoint(), servlet, internalFactory.getContextParams(), reference);
        synchronized (factories) {
            factories.put(reference, internalFactory);
        }
    }

    private void removeApplication(ServiceReference reference) {
        PaxWicketApplicationFactory factory;
        synchronized (factories) {
            if (!factories.containsKey(reference)) {
                LOGGER
                    .warn("Trying to unregister ApplicationFactory without application name or mount point is not possible");
                return;
            }
            factory = factories.remove(reference);
        }
        LOGGER.debug("Service Removed [{}], Factory hash [{}]", reference, identityHashCode(factory));
        httpTracker.removeServlet(factory.getMountPoint());
    }

    private void addServlet(String mountPoint, Servlet servlet, Map<?, ?> contextParam,
            ServiceReference appFactoryReference) {
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
