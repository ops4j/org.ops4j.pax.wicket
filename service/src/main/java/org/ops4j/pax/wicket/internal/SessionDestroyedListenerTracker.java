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

import static org.ops4j.pax.wicket.internal.TrackingUtil.createApplicationFilter;

import org.ops4j.pax.wicket.api.SessionDestroyedListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks {@link SessionDestroyedListener}s and registers them to an Application.
 * 
 * @author David Leangen
 */
public final class SessionDestroyedListenerTracker extends
        ServiceTracker<SessionDestroyedListener, SessionDestroyedListener> {

    private static final Logger LOGGER = LoggerFactory.getLogger(SessionDestroyedListenerTracker.class);

    private final BundleContext context;
    private final SessionDestroyedHander handler;

    /**
     * Construct an instance of {@code SessionDestroyedListenerTracker} with the specified arguments.
     * 
     * @param context The bundle context. This argument must not be {@code null}.
     * @param handler The handler that will handle the listeners.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     */
    public SessionDestroyedListenerTracker(BundleContext context, SessionDestroyedHander handler)
        throws IllegalArgumentException {
        super(context, createApplicationFilter(context, handler.getApplicationName()), null);

        this.context = context;
        this.handler = handler;
    }

    /**
     * Adding service.
     * 
     * @see ServiceTracker#addingService(ServiceReference)
     */
    @Override
    public final SessionDestroyedListener addingService(ServiceReference<SessionDestroyedListener> serviceReference) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Listener [" + serviceReference + "] has been added.");
        }

        final SessionDestroyedListener listener = context.getService(serviceReference);
        handler.addListener(listener);

        return listener;
    }

    /**
     * Handle removed service.
     * 
     * @see ServiceTracker#removedService(ServiceReference,Object)
     */
    @Override
    public void removedService(ServiceReference<SessionDestroyedListener> serviceReference,
            SessionDestroyedListener object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Listener [" + serviceReference + "] has been removed.");
        }

        final SessionDestroyedListener listener = context.getService(serviceReference);
        handler.removeListener(listener);
    }
}
