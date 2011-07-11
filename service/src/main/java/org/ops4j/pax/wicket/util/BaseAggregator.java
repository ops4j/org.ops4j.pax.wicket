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
package org.ops4j.pax.wicket.util;

import static java.util.Collections.sort;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.ops4j.pax.wicket.api.ContentSource.AGGREGATION_POINT;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.internal.DefaultContentTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

abstract class BaseAggregator implements ContentAggregator, ManagedService, ContentTrackingCallback {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass());

    private Dictionary<String, Object> properties;
    private BundleContext bundleContext;
    private HashMap<String, List<ContentSource>> children;
    private ServiceRegistration registration;
    private DefaultContentTracker contentTracker;
    private HashMap<String, ContentSource> wiredSources;

    public BaseAggregator(BundleContext bundleContext, String applicationName, String aggregationPoint)
        throws IllegalArgumentException {
        // bundle context could be temporary null; some situations only allow to retrieve it lazy
        validateNotEmpty(applicationName, "applicationName");
        validateNotEmpty(aggregationPoint, "aggregationPoint");

        children = new HashMap<String, List<ContentSource>>();
        wiredSources = new HashMap<String, ContentSource>();
        properties = new Hashtable<String, Object>();
        properties.put(SERVICE_PID, applicationName + "." + aggregationPoint);
        this.bundleContext = bundleContext;
        setAggregationPointName(aggregationPoint);
        setApplicationName(applicationName);
    }

    /**
     * Returns the aggregation point id of this {@code AbstractContentAggregator} instance. This method must not return
     * {@code null} object.
     * 
     * @return The aggregation point name.
     * 
     * @since 1.0.0
     */
    public final String getAggregationPointName() {
        return getStringProperty(AGGREGATION_POINT, null);
    }

    /**
     * Set the aggregation id of this {@code AbstractContentAggregator}.
     * <p>
     * Note: aggregation id property must not be set after this {@code AbstractContentAggregator} instance is registered
     * to OSGi framework.
     * </p>
     * 
     * @param aggregationPoint The aggregation point name. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code aggregationId} argument is {@code null} or empty.
     * @since 1.0.0
     */
    public final void setAggregationPointName(String aggregationPoint) {
        setProperty(AGGREGATION_POINT, aggregationPoint);
        updateRegistration();
    }

    /**
     * Returns the application name of this {@code ContentAggregator} instance belongs to.
     * 
     * @return The application name of this {@code ContentAggregator} instance belongs to.
     * 
     * @since 1.0.0
     */
    public final String getApplicationName() {
        return getStringProperty(APPLICATION_NAME, null);
    }

    /**
     * Sets the application name of this {@code AbstractContentAggregator} instant belongs to.
     * <p>
     * Note: Application name property must not be set after this {@code AbstractContentAggregator} instance is
     * registered to OSGi framework.
     * </p>
     * 
     * @param applicationName The application name. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code applicationName} argument is {@code null} or
     *         empty.
     * @since 1.0.0
     */
    public final void setApplicationName(String applicationName) {
        setProperty(APPLICATION_NAME, applicationName);
        updateRegistration();
    }

    /**
     * Dispose this {@code RootContentAggregator} instance.
     * <p>
     * Note: Dispose does not unregister this {@code RootContentAggregator}, and ensure that dispose is only called
     * after this {@code RootContentAggregator} instance is unregistered from OSGi container.
     * </p>
     * 
     * @throws IllegalStateException Thrown if this content tracker has not been registered.
     * @see org.osgi.framework.ServiceRegistration#unregister()
     * @since 1.0.0
     */
    public final void dispose()
        throws IllegalStateException {
        synchronized (this) {
            if (contentTracker == null) {
                throw new IllegalStateException("RootContentAggregator [" + this + "] has not been registered.");
            }

            contentTracker.close();
            registration.unregister();
            onDispose();
            contentTracker = null;
        }
    }

    /**
     * Returns the BundleContext that this ContentAggregator belongs to.
     * 
     * @return the BundleContext that this ContentAggregator belongs to.
     */
    protected final BundleContext getInternalBundleContext() {
        return bundleContext;
    }

    protected final void setInternalBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void updated(Dictionary config) throws ConfigurationException {
        validateNotNull(bundleContext, "bundleContext");
        if (config == null) {
            return;
        }
        String newAggregationPointName = (String) config.get(AGGREGATION_POINT);
        if (newAggregationPointName == null) {
            throw new ConfigurationException(AGGREGATION_POINT, "This property must not be [null].");
        }
        String newApplicationName = (String) config.get(APPLICATION_NAME);
        if (newApplicationName == null) {
            throw new ConfigurationException(APPLICATION_NAME, "This property must not be [null].");
        }

        String existingAggregationPointName = getAggregationPointName();
        if (existingAggregationPointName != null && existingAggregationPointName.equals(newAggregationPointName)) {
            return;
        }
        properties = config;
        registration.setProperties(config);
        contentTracker.close();
        children.clear();
        contentTracker =
            new DefaultContentTracker(bundleContext, this, newApplicationName, newAggregationPointName);
        contentTracker.open();
        onUpdated(config);
    }

    protected void onUpdated(Dictionary<?, ?> config) {
    }

    /**
     * Add the specified {@code source} to this {@code RootContentAggregator} and mapped it as {@code wicketId}.
     * 
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     * @param source The source. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    public final void addContent(String wicketId, ContentSource source)
        throws IllegalArgumentException {
        validateNotEmpty(wicketId, "wicketId");
        validateNotNull(source, "source");

        synchronized (this) {
            String sourceId = source.getSourceId();
            wiredSources.put(sourceId, source);

            List<ContentSource> contents = children.get(wicketId);
            if (contents == null) {
                contents = new ArrayList<ContentSource>();
                children.put(wicketId, contents);
            }

            contents.add(source);
        }
    }

    /**
     * Remove the specified {@code content} to this {@code RootContentAggregator} and unmapped it as {@code wicketId}.
     * 
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     * @param content The content. This argument must not be {@code null}.
     * 
     * @return A {@code boolean} indicator whether removal is successfull.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    public final boolean removeContent(String wicketId, ContentSource content)
        throws IllegalArgumentException {
        validateNotEmpty(wicketId, "wicketId");
        validateNotNull(content, "content");

        synchronized (this) {
            String sourceId = content.getSourceId();
            wiredSources.remove(sourceId);

            List<ContentSource> contents = children.get(wicketId);
            if (contents == null) {
                return false;
            }

            contents.remove(content);
            if (contents.isEmpty()) {
                return children.remove(wicketId) != null;
            }
        }

        return false;
    }

    public final void register()
        throws IllegalStateException {
        synchronized (this) {
            if (contentTracker != null) {
                throw new IllegalStateException("This ContentAggregator [" + this + "] has already been registered.");
            }
            validateNotNull(bundleContext, "bundleContext");
            String applicationName = getApplicationName();
            String aggregationPoint = getAggregationPointName();
            contentTracker = new DefaultContentTracker(bundleContext, this, applicationName, aggregationPoint);
            contentTracker.open();

            String[] serviceNames = getServiceNames();
            registration = bundleContext.registerService(serviceNames, this, properties);
        }
    }

    protected abstract String[] getServiceNames();

    protected String[] getStringArrayProperty(String key) {
        return (String[]) properties.get(key);
    }

    protected String getStringProperty(String key, String defaultValue) {
        String value = (String) properties.get(key);

        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    protected void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    protected void updateRegistration() {
        if (registration != null) {
            registration.setProperties(properties);
        }
    }

    protected Object getObjectProperty(String key) {
        return properties.get(key);
    }

    /**
     * Override this method to handle additional dispose.
     * 
     * @since 1.0.0
     */
    protected void onDispose() {

    }

    public final List<String> getRegisteredSourceIds(String groupId) {
        return getRegisteredSourceIds(groupId, null);
    }

    public final List<String> getRegisteredSourceIds(String groupId, Comparator<ContentSource> comparator)
        throws IllegalArgumentException {
        validateNotEmpty(groupId, "wicketId");
        ArrayList<String> result = new ArrayList<String>();
        List<ContentSource> contents = getContentByGroupId(groupId);
        if (comparator != null) {
            sort(contents, comparator);
        }
        for (ContentSource source : contents) {
            String sourceId = source.getSourceId();
            result.add(sourceId);
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    public final <ContentSourceType extends ContentSource> List<ContentSourceType>
        getEntireAggregationPointContent()
            throws IllegalArgumentException {
        List<ContentSourceType> contents = new ArrayList<ContentSourceType>();
        synchronized (this) {
            for (Entry<String, ContentSource> contentSource : wiredSources.entrySet()) {
                contents.add((ContentSourceType) contentSource.getValue());
            }
        }
        return contents;
    }

    @SuppressWarnings("unchecked")
    public final <V extends ContentSource> List<V> getContentByGroupId(String groupId)
        throws IllegalArgumentException {
        validateNotEmpty(groupId, "wicketId");
        List<V> contents;
        synchronized (this) {
            contents = (List<V>) children.get(groupId);
        }
        if (contents != null) {
            contents = new ArrayList<V>(contents);
        } else {
            contents = new ArrayList<V>();
        }
        return contents;
    }

    @SuppressWarnings("unchecked")
    public final <ContentSourceType extends ContentSource> ContentSourceType getContentBySourceId(String sourceId) {
        validateNotEmpty(sourceId, "sourceId");
        ContentSource source;
        synchronized (this) {
            source = wiredSources.get(sourceId);
        }

        if (source == null) {
            String message = "Source [" + sourceId + "] is not wired to [" + this + "]";
            LOGGER.warn(message);
            throw new IllegalArgumentException(message);
        }
        return (ContentSourceType) source;
    }

    @Override
    protected void finalize()
        throws Throwable {
        synchronized (this) {
            if (contentTracker != null) {
                LOGGER.warn("RootContentAggregator [" + this + "] is not disposed.");
            }
            dispose();
        }
        super.finalize();
    }

    public boolean isEmpty() {
        return wiredSources == null || wiredSources.size() == 0;
    }

}
