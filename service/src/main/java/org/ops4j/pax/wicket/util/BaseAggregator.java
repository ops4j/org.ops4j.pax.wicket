/* 
 * Copyright 2007 Niclas Hedhman.
 * Copyright 2010 David Leangen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util;

import static java.util.Collections.sort;
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.AGGREGATION_POINT;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;

import org.apache.wicket.Session;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;
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

    private Dictionary<String, Object> m_properties;
    private BundleContext m_bundleContext;
    private HashMap<String, List<ContentSource>> m_children;
    private ServiceRegistration m_registration;
    private DefaultContentTracker m_contentTracker;
    private HashMap<String, ContentSource> m_wiredSources;

    public BaseAggregator(BundleContext bundleContext, String applicationName, String aggregationPoint)
        throws IllegalArgumentException {
        // bundle context could be temporary null; some situations only allow to retrieve it lazy
        validateNotEmpty(applicationName, "applicationName");
        validateNotEmpty(aggregationPoint, "aggregationPoint");

        m_children = new HashMap<String, List<ContentSource>>();
        m_wiredSources = new HashMap<String, ContentSource>();
        m_properties = new Hashtable<String, Object>();
        m_properties.put(SERVICE_PID, applicationName + "." + aggregationPoint);
        m_bundleContext = bundleContext;
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
            if (m_contentTracker == null) {
                throw new IllegalStateException("RootContentAggregator [" + this + "] has not been registered.");
            }

            m_contentTracker.close();
            m_registration.unregister();
            onDispose();
            m_contentTracker = null;
        }
    }

    /**
     * Returns the BundleContext that this ContentAggregator belongs to.
     * 
     * @return the BundleContext that this ContentAggregator belongs to.
     */
    protected final BundleContext getInternalBundleContext() {
        return m_bundleContext;
    }

    protected final void setInternalBundleContext(BundleContext bundleContext) {
        m_bundleContext = bundleContext;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void updated(Dictionary config) throws ConfigurationException {
        validateNotNull(m_bundleContext, "bundleContext");
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
        m_properties = config;
        m_registration.setProperties(config);
        m_contentTracker.close();
        m_children.clear();
        m_contentTracker =
            new DefaultContentTracker(m_bundleContext, this, newApplicationName, newAggregationPointName);
        m_contentTracker.open();
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
            m_wiredSources.put(sourceId, source);

            List<ContentSource> contents = m_children.get(wicketId);
            if (contents == null) {
                contents = new ArrayList<ContentSource>();
                m_children.put(wicketId, contents);
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
            m_wiredSources.remove(sourceId);

            List<ContentSource> contents = m_children.get(wicketId);
            if (contents == null) {
                return false;
            }

            contents.remove(content);
            if (contents.isEmpty()) {
                return m_children.remove(wicketId) != null;
            }
        }

        return false;
    }

    public final void register()
        throws IllegalStateException {
        synchronized (this) {
            if (m_contentTracker != null) {
                throw new IllegalStateException("This ContentAggregator [" + this + "] has already been registered.");
            }
            validateNotNull(m_bundleContext, "bundleContext");
            String applicationName = getApplicationName();
            String aggregationPoint = getAggregationPointName();
            m_contentTracker = new DefaultContentTracker(m_bundleContext, this, applicationName, aggregationPoint);
            m_contentTracker.open();

            String[] serviceNames = getServiceNames();
            m_registration = m_bundleContext.registerService(serviceNames, this, m_properties);
        }
    }

    protected abstract String[] getServiceNames();

    protected String[] getStringArrayProperty(String key) {
        return (String[]) m_properties.get(key);
    }

    protected String getStringProperty(String key, String defaultValue) {
        String value = (String) m_properties.get(key);

        if (value == null) {
            return defaultValue;
        } else {
            return value;
        }
    }

    protected void setProperty(String key, Object value) {
        m_properties.put(key, value);
    }

    protected void updateRegistration() {
        if (m_registration != null) {
            m_registration.setProperties(m_properties);
        }
    }

    protected Object getObjectProperty(String key) {
        return m_properties.get(key);
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
            for (Entry<String, ContentSource> contentSource : m_wiredSources.entrySet()) {
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
            contents = (List<V>) m_children.get(groupId);
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
            source = m_wiredSources.get(sourceId);
        }

        if (source == null) {
            String message = "Source [" + sourceId + "] is not wired to [" + this + "]";
            LOGGER.warn(message);
            throw new IllegalArgumentException(message);
        }
        return (ContentSourceType) source;
    }

    /**
     * Returns the Authentication of the current request.
     * 
     * It is possible to obtain the Username of the logged in user as well as which roles that this user has assigned to
     * it.
     * 
     * @return the Authentication of the current request.
     */
    protected PaxWicketAuthentication getAuthentication() {
        Session session = Session.get();
        if (session instanceof PaxWicketAuthentication) {
            return (PaxWicketAuthentication) session;
        }

        return null;
    }

    @Override
    protected void finalize()
        throws Throwable {
        synchronized (this) {
            if (m_contentTracker != null) {
                LOGGER.warn("RootContentAggregator [" + this + "] is not disposed.");
            }
            dispose();
        }
        super.finalize();
    }

    public boolean isEmpty() {
        return m_wiredSources == null || m_wiredSources.size() == 0;
    }

}
