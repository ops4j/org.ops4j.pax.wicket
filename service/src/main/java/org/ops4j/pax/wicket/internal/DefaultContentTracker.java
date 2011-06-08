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

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.AGGREGATION_POINT;
import static org.ops4j.pax.wicket.api.ContentSource.DESTINATIONS;
import static org.ops4j.pax.wicket.internal.TrackingUtil.createContentFilter;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.ops4j.pax.wicket.api.ContentSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * {@code DefaultContentTracker} tracks {@link ContentSource} services.
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
public final class DefaultContentTracker extends ServiceTracker {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultContentTracker.class);

    private final BundleContext context;
    private final ContentTrackingCallback callback;
    private final String aggregationId;

    private final List<ServiceReference> m_references;

    /**
     * Construct an instance of {@code DefaultContentTracker} with the specified arguments.
     * 
     * @param context The bundle context. This argument must not be {@code null}.
     * @param callback The callback. This argument must not be {@code null}.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * @param aggregationPointName The aggregation id. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    public DefaultContentTracker(BundleContext context, ContentTrackingCallback callback, String applicationName,
            String aggregationPointName) throws IllegalArgumentException {
        super(context, createContentFilter(context, applicationName), null);

        validateNotEmpty(aggregationPointName, "aggregationPointName");
        validateNotNull(callback, "callback");

        this.context = context;
        this.callback = callback;
        aggregationId = aggregationPointName;

        m_references = new ArrayList<ServiceReference>();
    }

    /**
     * Close the tracker. This method assumes that all {@code ContentSource} that are stored by invoking
     * {@link ContentTrackingCallback#addContent(String,ContentSource)} has been freed.
     * 
     * @since 1.0.0
     */
    @Override
    public final synchronized void close() {
        synchronized (this) {
            int startIndexOfWicketId = aggregationId.length() + 1;

            for (ServiceReference reference : m_references) {
                String[] destinationIds = (String[]) reference.getProperty(DESTINATIONS);

                for (String destinationId : destinationIds) {
                    String wicketId = destinationId.substring(startIndexOfWicketId);
                    ContentSource content = (ContentSource) context.getService(reference);

                    callback.removeContent(wicketId, content);
                }

                context.ungetService(reference); // Removal for the first get during add
                context.ungetService(reference); // Removal for the second get in this loop block
            }

            m_references.clear();
        }

        super.close();
    }

    /**
     * Adding service.
     * 
     * @see ServiceTracker#addingService(ServiceReference)
     * @since 1.0.0
     */
    @Override
    public final Object addingService(ServiceReference serviceReference) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Service Reference [" + serviceReference + "] has been added.");
        }

        String[] destinations = (String[]) serviceReference.getProperty(DESTINATIONS);
        Object service = context.getService(serviceReference);
        if (destinations != null) {
            for (String destination : destinations) {
                if (destination.startsWith("regexp(")) {
                    matchRegularExpression(destination, service, serviceReference);
                } else if (destination.startsWith(aggregationId)) {
                    matchDirect(destination, service, serviceReference);
                }
            }
        }

        return service;
    }

    private void matchRegularExpression(String dest, Object service, ServiceReference serviceReference) {
        int lastParan = dest.lastIndexOf(").");
        if (lastParan < 0) {
            String message = "Regular Expressions must have the format: \"regexp(\"[expression]\").\"[wicketId]";
            throw new IllegalArgumentException(message);
        }
        String expression = dest.substring(7, lastParan);
        if (Pattern.matches(expression, aggregationId)) {
            synchronized (this) {
                String id = dest.substring(lastParan + 2);
                callback.addContent(id, (ContentSource) service);
                m_references.add(serviceReference);
            }
        }
    }

    private void matchDirect(String dest, Object service, ServiceReference serviceReference) {
        int aggregationIdLength = aggregationId.length();
        if (dest.length() == aggregationIdLength) {
            String message = "The '" + DESTINATIONS + "' property have the form [" + AGGREGATION_POINT
                             + "].[wicketId] but was " + dest;
            throw new IllegalArgumentException(message);
        }

        if (dest.charAt(aggregationIdLength) != '.') {
            return;
        }

        String id = dest.substring(aggregationIdLength + 1);

        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Attaching content with wicket:id [" + id + "] to aggregation [" + aggregationId + "]");
        }

        synchronized (this) {
            callback.addContent(id, (ContentSource) service);
            m_references.add(serviceReference);
        }
    }

    /**
     * Handle removed service.
     * 
     * @see ServiceTracker#removedService(ServiceReference,Object)
     * @since 1.0.0
     */
    @Override
    public void removedService(ServiceReference serviceReference, Object object) {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Service Reference [" + serviceReference + "] has been removed.");
        }

        if (!(object instanceof ContentSource)) {
            String message = "OSGi Framework not passing a ContentSource object as specified in R4 spec.";
            throw new IllegalArgumentException(message);
        }

        ContentSource content = (ContentSource) object;
        String[] destionationIds = content.getDestinations();
        if (destionationIds != null) {
            for (String destinationId : destionationIds) {
                int pos = destinationId.lastIndexOf('.');
                String id = destinationId.substring(pos + 1);
                boolean wasContentRemoved = callback.removeContent(id, content);

                if (LOGGER.isInfoEnabled() && wasContentRemoved) {
                    LOGGER.info("Detaching content with wicket:id [" + id + "] from aggregation [" + aggregationId
                             + "]"
                        );
                }
            }
        }

        synchronized (this) {
            context.ungetService(serviceReference);
            m_references.remove(serviceReference);
        }
    }
}
