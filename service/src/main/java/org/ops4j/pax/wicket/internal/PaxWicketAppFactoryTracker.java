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

import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.internal.servlet.PAXWicketServlet;
import org.ops4j.pax.wicket.internal.util.ServiceTrackerAggregatorReadyChildren;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The factory tracker waits for every new {@link WebApplicationFactory} class registered as OSGi service. If the
 * services does also contain properties for an application name and a mount point is is registered for a Servlet.
 * Otherwise the problem is logged as a warning and the service is simply ignored.
 */
public class PaxWicketAppFactoryTracker implements ServiceTrackerAggregatorReadyChildren<WebApplicationFactory<?>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketAppFactoryTracker.class);

    private final HttpTracker httpTracker;
    private final Map<ServiceReference<WebApplicationFactory<?>>, PaxWicketApplicationFactory> factories =
        new HashMap<ServiceReference<WebApplicationFactory<?>>, PaxWicketApplicationFactory>();
    private final BundleContext context;

    PaxWicketAppFactoryTracker(BundleContext context, HttpTracker httpTracker) throws IllegalArgumentException {
        this.context = context;
        validateNotNull(httpTracker, "httpTracker");
        this.httpTracker = httpTracker;
    }

    public void addingService(ServiceReference<WebApplicationFactory<?>> reference, WebApplicationFactory<?> service) {
        PaxWicketApplicationFactory internalFactory =
            PaxWicketApplicationFactory.createPaxWicketApplicationFactory(context, service, reference);
        addApplication(reference, internalFactory);
    }

    public void modifiedService(ServiceReference<WebApplicationFactory<?>> reference,
            WebApplicationFactory<?> service) {
        removeApplication(reference);
        PaxWicketApplicationFactory internalFactory =
            PaxWicketApplicationFactory.createPaxWicketApplicationFactory(context, service,
                reference);
        addApplication(reference, internalFactory);
    }

    public void removedService(ServiceReference<WebApplicationFactory<?>> reference, WebApplicationFactory<?> service) {
        removeApplication(reference);
    }

    private void addApplication(ServiceReference<WebApplicationFactory<?>> reference,
            PaxWicketApplicationFactory internalFactory) {
        if (!internalFactory.isValidFactory()) {
            throw new IllegalArgumentException(
                "Trying to register ApplicationFactory without application name or mount point is not possible");
        }
        LOGGER.debug("Service Added [{}], Factory hash [{}]", reference, identityHashCode(internalFactory));
        Servlet servlet = PAXWicketServlet.createServlet(internalFactory);
        addServlet(internalFactory.getMountPoint(), servlet, internalFactory.getContextParams(), reference);
        synchronized (factories) {
            factories.put(reference, internalFactory);
        }
    }

    private void removeApplication(ServiceReference<WebApplicationFactory<?>> reference) {
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
            ServiceReference<WebApplicationFactory<?>> appFactoryReference) {
        Bundle bundle = appFactoryReference.getBundle();
        httpTracker.addServlet(mountPoint, servlet, contextParam, bundle);
    }
}
