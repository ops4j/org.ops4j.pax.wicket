/*  Copyright 2007 Niclas Hedhman.
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
package org.ops4j.pax.wicket.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;
import org.osgi.framework.BundleContext;
import static org.osgi.framework.Constants.SERVICE_PID;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import wicket.Component;
import wicket.Session;

public abstract class BaseAggregator
    implements ContentAggregator, ManagedService, ContentTrackingCallback
{

    protected final Logger m_logger = Logger.getLogger( getClass() );

    private Dictionary<String, Object> m_properties;
    private BundleContext m_bundleContext;
    private HashMap<String, List<ContentSource>> m_children;
    private ServiceRegistration m_registration;
    private DefaultContentTracker m_contentTracker;
    private HashMap<String, ContentSource> m_wiredSources;

    public BaseAggregator( BundleContext bundleContext, String applicationName, String aggregationPoint )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( aggregationPoint, "aggregationPoint" );

        m_children = new HashMap<String, List<ContentSource>>();
        m_wiredSources = new HashMap<String, ContentSource>();
        m_properties = new Hashtable<String, Object>();
        m_properties.put( SERVICE_PID, applicationName + "." + aggregationPoint );
        m_bundleContext = bundleContext;
        setAggregationPointName( aggregationPoint );
        setApplicationName( applicationName );
    }

    /**
     * Returns the aggregation point id of this {@code AbstractContentAggregator} instance. This method must not return
     * {@code null} object.
     *
     * @return The aggregation point name.
     *
     * @since 1.0.0
     */
    public final String getAggregationPointName()
    {
        return getStringProperty( ContentSource.AGGREGATION_POINT, null );
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
    public final void setAggregationPointName( String aggregationPoint )
    {
        setProperty( ContentSource.AGGREGATION_POINT, aggregationPoint );
        updateRegistration();
    }

    /**
     * Returns the application name of this {@code ContentAggregator} instance belongs to.
     *
     * @return The application name of this {@code ContentAggregator} instance belongs to.
     *
     * @since 1.0.0
     */
    public final String getApplicationName()
    {
        return getStringProperty( ContentSource.APPLICATION_NAME, null );
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
     *                                  empty.
     * @since 1.0.0
     */
    public final void setApplicationName( String applicationName )
    {
        setProperty( ContentSource.APPLICATION_NAME, applicationName );
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
        throws IllegalStateException
    {
        synchronized( this )
        {
            if( m_contentTracker == null )
            {
                throw new IllegalStateException( "RootContentAggregator [" + this + "] has not been registered." );
            }
            
            m_contentTracker.close();
            onDispose();
            m_contentTracker = null;
        }
    }

    /**
     * Returns the BundleContext that this ContentAggregator belongs to.
     *
     * @return the BundleContext that this ContentAggregator belongs to.
     */
    protected final BundleContext getBundleContext()
    {
        return m_bundleContext;
    }

    @SuppressWarnings( "unchecked" )
    public final void updated( Dictionary config )
        throws ConfigurationException
    {
        if( config == null )
        {
            return;
        }
        String newAggregationPointName = (String) config.get( ContentSource.AGGREGATION_POINT );
        if( newAggregationPointName == null )
        {
            throw new ConfigurationException( ContentSource.AGGREGATION_POINT, "This property must not be [null]." );
        }
        String newApplicationName = (String) config.get( ContentSource.APPLICATION_NAME );
        if( newApplicationName == null )
        {
            throw new ConfigurationException( ContentSource.APPLICATION_NAME, "This property must not be [null]." );
        }

        String existingAggregationPointName = getAggregationPointName();
        if( existingAggregationPointName != null && existingAggregationPointName.equals( newAggregationPointName ) )
        {
            return;
        }
        m_properties = config;
        m_registration.setProperties( config );
        m_contentTracker.close();
        m_children.clear();
        m_contentTracker =
            new DefaultContentTracker( m_bundleContext, this, newApplicationName, newAggregationPointName );
        m_contentTracker.open();
        onUpdated( config );
    }

    protected void onUpdated( Dictionary config )
    {
    }

    /**
     * Add the specified {@code source} to this {@code RootContentAggregator} and mapped it as {@code wicketId}.
     *
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     * @param source   The source. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    public final void addContent( String wicketId, ContentSource source )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        NullArgumentException.validateNotNull( source, "source" );

        synchronized( this )
        {
            String sourceId = source.getSourceId();
            m_wiredSources.put( sourceId, source );

            List<ContentSource> contents = m_children.get( wicketId );
            if( contents == null )
            {
                contents = new ArrayList<ContentSource>();
                m_children.put( wicketId, contents );
            }

            contents.add( source );
        }
    }

    /**
     * Remove the specified {@code content} to this {@code RootContentAggregator} and unmapped it as {@code wicketId}.
     *
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     * @param content  The content. This argument must not be {@code null}.
     *
     * @return A {@code boolean} indicator whether removal is successfull.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    public final boolean removeContent( String wicketId, ContentSource content )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        NullArgumentException.validateNotNull( content, "content" );

        synchronized( this )
        {
            String sourceId = content.getSourceId();
            m_wiredSources.remove( sourceId );

            List<ContentSource> contents = m_children.get( wicketId );
            if( contents == null )
            {
                return false;
            }

            contents.remove( content );
            if( contents.isEmpty() )
            {
                return m_children.remove( wicketId ) != null;
            }
        }

        return false;
    }

    public final ServiceRegistration register()
        throws IllegalStateException
    {
        synchronized( this )
        {
            if( m_contentTracker != null )
            {
                throw new IllegalStateException( "This ContentAggregator [" + this + "] has already been registered." );
            }

            String applicationName = getApplicationName();
            String aggregationPoint = getAggregationPointName();
            m_contentTracker = new DefaultContentTracker( m_bundleContext, this, applicationName, aggregationPoint );
            m_contentTracker.open();

            String[] serviceNames = getServiceNames();
            m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
            return m_registration;
        }
    }

    protected abstract String[] getServiceNames();

    protected String[] getStringArrayProperty( String key )
    {
        return (String[]) m_properties.get( key );
    }

    protected String getStringProperty( String key, String defaultValue )
    {
        String value = (String) m_properties.get( key );

        if( value == null )
        {
            return defaultValue;
        }
        else
        {
            return value;
        }
    }

    protected void setProperty( String key, Object value )
    {
        m_properties.put( key, value );
    }

    protected void updateRegistration()
    {
        if( m_registration != null )
        {
            m_registration.setProperties( m_properties );
        }
    }

    protected Object getObjectProperty( String key )
    {
        return m_properties.get( key );
    }

    /**
     * Override this method to handle additional dispose.
     *
     * @since 1.0.0
     */
    protected void onDispose()
    {

    }

    /**
     * @param wicketId   The WicketID that we want to find the wiring for.
     * @param comparator The sorting comparator.
     *
     * @return A List of SourceIDs, identifying the ContentSource that has been wired to this ContentAggregator.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code wicketId} argument is {@code null}.
     * @see #createWiredComponent(String,String)
     * @since 1.0.0
     */
    public final List<String> getWiredSourceIds( String wicketId, Comparator<ContentSource> comparator )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );

        ArrayList<String> result = new ArrayList<String>();
        List<ContentSource> contents = getContents( wicketId );
        if( comparator != null )
        {
            Collections.sort( contents, comparator );
        }

        for( ContentSource source : contents )
        {
            String sourceId = source.getSourceId();
            result.add( sourceId );
        }
        return result;
    }

    /**
     * @param sourceId The SourceID of the ContentSource that we want to create a component.
     * @param wicketId The WicketID of the component to be created by the ContentSource. This responds to the
     *                 wicket:id in the markup.
     *
     * @return The wicket component build for the specified {@code sourceId} and {@code wicketId}.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code sourceId} (or/and) {@code wicketId} argument is {@code null}.
     * @see #getWiredSourceIds(String,java.util.Comparator)
     * @since 1.0.0
     */
    public final Component createWiredComponent( String sourceId, String wicketId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( sourceId, "sourceId" );
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        ContentSource<?> source;
        synchronized( this )
        {
            source = m_wiredSources.get( sourceId );
        }

        if( source == null )
        {
            String message = "Source [" + sourceId + "] is not wired to [" + this + "]";
            m_logger.warn( message );
            throw new IllegalArgumentException( message );
        }
        return source.createSourceComponent( wicketId );
    }

    /**
     * Returns list of {@code ContentSource} instnaces of the specified {@code wicketId}. Returns an empty list if
     * there is no content for the specified {@code wicketId}.
     *
     * @param wicketId The wicket id. This argument must not be {@code null} or empty.
     *
     * @return List of {@code ContentSource} of the specified {@code wicketId}.
     *
     * @throws IllegalArgumentException if the wicketId is null or empty.
     */
    @SuppressWarnings( "unchecked" )
    public final <V extends ContentSource> List<V> getContents( String wicketId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );

        List<V> contents;
        synchronized( this )
        {
            contents = (List<V>) m_children.get( wicketId );
        }

        if( contents != null )
        {
            contents = new ArrayList<V>( contents );
        }
        else
        {
            contents = new ArrayList<V>();
        }

        return contents;
    }

    /**
     * Returns the Authentication of the current request.
     *
     * It is possible to obtain the Username of the logged in user as well as which roles that this user has assigned to
     * it.
     *
     * @return the Authentication of the current request.
     */
    protected PaxWicketAuthentication getAuthentication()
    {
        Session session = Session.get();
        if( session instanceof PaxWicketAuthentication )
        {
            return (PaxWicketAuthentication) session;
        }

        return null;
    }

    public final ContentSource<? extends Component> getContentById( String wicketId, String sourceId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        NullArgumentException.validateNotEmpty( sourceId, "sourceId" );

        synchronized( this )
        {
            List<ContentSource> sourceList = m_children.get( wicketId );

            if( sourceList != null )
            {
                for( ContentSource source : sourceList )
                {
                    String sourceIdToCompare = source.getSourceId();
                    if( sourceId.equals( sourceIdToCompare ) )
                    {
                        return (ContentSource<?>) source;
                    }
                }
            }
        }

        return null;
    }

    @Override
    protected void finalize()
        throws Throwable
    {
        synchronized( this )
        {
            if( m_contentTracker != null )
            {
                m_logger.warn( "RootContentAggregator [" + this + "] is not disposed." );
            }
            dispose();
        }
        super.finalize();
    }
}
