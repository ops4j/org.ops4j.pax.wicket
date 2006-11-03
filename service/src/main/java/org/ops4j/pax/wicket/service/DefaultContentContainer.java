/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License. 
 */
package org.ops4j.pax.wicket.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.service.internal.ContentTrackingCallback;
import org.ops4j.pax.wicket.service.internal.DefaultContentTracker;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

import wicket.Component;

public abstract class DefaultContentContainer
    implements ContentContainer, Content, ContentTrackingCallback, ManagedService
{
    protected final Logger m_logger = Logger.getLogger( getClass() );

    private Properties m_properties;
    private HashMap<String, List<Content>> m_children;
    private BundleContext m_bundleContext;
    private DefaultContentTracker m_contentTracker;
    private ServiceRegistration m_registration;

    /**
     * Construct an instnace of {@code DefaultContentContainer} with the specified arguments.
     * 
     * @param bundleContext The bundle context. This argument must not be {@code null}.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * @param containmentId The containment id. This argument must not be {@code null} or empty.
     * @param destinationId The destination id. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null} or empty.
     * @since 1.0.0
     */
    protected DefaultContentContainer( BundleContext bundleContext, String applicationName, String containmentId,
                                       String destinationId
    )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( containmentId, "containmentId" );
        NullArgumentException.validateNotEmpty( destinationId, "destinationId" );
        
        m_bundleContext = bundleContext;
        m_children = new HashMap<String, List<Content>>();
        m_properties = new Properties();
        
        setContainmentId( containmentId );
        setDestinationId( destinationId );
        setApplicationName( applicationName );
        
        m_properties.put( Constants.SERVICE_PID, applicationName + "." + containmentId );
    }

    /**
     * Returns the application name of this {@code DefaultContentContainer} instance belongs to.
     *  
     * @return The application name of this {@code DefaultContentContainer} instance belongs to.
     * @since 1.0.0
     */
    public String getApplicationName()
    {
        return m_properties.getProperty( Content.APPLICATION_NAME );
    }

    /**
     * Sets the application name of this {@code DefaultContentContainer} instant belongs to.
     * <p>
     * Note:
     * Application name property must not be set after this {@code DefaultContentContainer} instance is registered to
     * OSGi framework.
     * </p>
     * 
     * @param applicationName The application name. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code applicationName} argument is {@code null} or
     *                                  empty.
     * @since 1.0.0
     */
    public final void setApplicationName( String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        
        m_properties.put( Content.APPLICATION_NAME, applicationName );
    }

    public final void dispose()
    {
        m_contentTracker.close();
    }

    /**
     * Returns the containment id of this {@code DefaultContentContainer} instance. This method must not return 
     * {@code null} object.
     * 
     * @since 1.0.0
     */
    public final String getContainmentId()
    {
        return m_properties.getProperty( CONTAINMENTID );
    }

    /**
     * Set the containment id of this {@code DefaultContentContainer}.
     * <p>
     * Note:
     * Containment id property must not be set after this {@code DefaultContentContainer} instance is registered to
     * OSGi framework.
     * </p>
     * 
     * @param containmentId The containment id. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code containmentId} argument is {@code null} or empty.
     * @since 1.0.0
     */
    public final void setContainmentId( String containmentId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( containmentId, "containmentId" );
        
        m_properties.put( CONTAINMENTID, containmentId );
    }

    /**
     * Create the wicket component represented by this {@code Content} instance. This method must not return 
     * {@code null} object.
     * 
     * @return The wicket component represented by this {@code Content} instance.
     * @since 1.0.0 
     */
    public final <T extends Component> List<T> createComponents( String id )
    {
        ArrayList<T> result = new ArrayList<T>();
        List<Content> contents = m_children.get( id );

        if( contents != null )
        {
            for( Content content : contents )
            {
                T component = ( T ) content.createComponent();
                result.add( component );
            }
        }
        
        Comparator<T> comparator = getComparator( id );
        if( comparator != null )
        {
            Collections.sort( result, comparator );
        }
        
        return result;
    }
    
    /**
     * Overrides this method to create a sorting mechanism for content with the specified {@code contentId}. 
     * Returns {@code null} if the comparator is not defined. By default, this comparator returns {@code null}.
     * 
     * @param contentId The content id. This argument must not be {@code null}.
     * 
     * @return The comparator for the specified {@code contentId}.
     * @see ContentContainer#createComponents(String)
     */
    public <T extends Component> Comparator<T> getComparator( String contentId )
    {
        return null;
    }

    public final void updated( Dictionary config ) 
        throws ConfigurationException
    {
        if( config == null )
        {
            m_registration.setProperties( m_properties );
            
            return;
        }
        
        String existingContainmentId = getContainmentId();
        String newContainmentId = (String) config.get( CONTAINMENTID );
        if( newContainmentId == null )
        {
            throw new ConfigurationException( CONTAINMENTID, "This property must not be [null]." );
        }
        
        String newApplicationName = (String) config.get( APPLICATION_NAME );
        if( newApplicationName == null )
        {
            throw new ConfigurationException( APPLICATION_NAME, "This property must not be [null]." );
        }
        
        String existingApplicationName = getApplicationName();
        if( existingContainmentId.equals( newContainmentId ) && existingApplicationName.equals( newApplicationName ) )
        {
            return;
        }
        
        m_children.clear();
        setApplicationName( newApplicationName );
        setContainmentId( newContainmentId );
        
        String filter = "(&(" + Content.APPLICATION_NAME + "=" + getApplicationName() + ")"
                        + "(" + Content.DESTINATIONID + "=" + getContainmentId() + ".*)"
                        + ")";
        
        try
        {
            ServiceReference[] services = m_bundleContext.getServiceReferences( Content.class.getName(), filter );
            if( null == services )
            {
                return;
            }
            
            for( ServiceReference service : services )
            {
                m_contentTracker.addingService( service );
            }
        } catch( InvalidSyntaxException e )
        {
            // Can not happen. Right!
            m_logger.warn( "Invalid filter [" + filter + "]. This probably caused by either the [application name] " +
                    "or the containement id] contains osgi filter keywords.", e );
        }
        
        m_registration.setProperties( config );
    }

    public final void addContent( String id, Content content )
    {
        List<Content> contents = m_children.get( id );
        if( contents == null )
        {
            contents = new ArrayList<Content>();
            m_children.put( id, contents );
        }
        contents.add( content );
    }

    public final boolean removeContent( String id, Content content )
    {
        List<Content> contents = m_children.get( id );
        if( contents == null )
        {
            return false;
        }
        contents.remove( content );
        if( contents.isEmpty() )
        {
            return m_children.remove( id ) != null;
        }
        return false;
    }

    /**
     * Returns the destination id of this {@code DefaultContentContainer} instance. This method must not return 
     * {@code null} object.
     * 
     * @since 1.0.0
     */
    public final String getDestinationId()
    {
        return m_properties.getProperty( Content.DESTINATIONID );
    }

    /**
     * Set the destination id of this {@code DefaultContentContainer}.
     * <p>
     * Note:
     * Destination id property must not be set after this {@code DefaultContentContainer} instance is registered to
     * OSGi framework.
     * </p>
     * 
     * @param destinationId The destination id. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code destinationId} argument is {@code null} or 
     *                                  empty.
     * @since 1.0.0
     */
    public final void setDestinationId( String destinationId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( destinationId, "destinationId" );
        
        m_properties.put( Content.DESTINATIONID, destinationId );
    }

    public final ServiceRegistration register()
    {
        m_contentTracker = new DefaultContentTracker( m_bundleContext, this, getApplicationName() );
        m_contentTracker.setContainmentId( getContainmentId() );
        m_contentTracker.open();

        String[] serviceNames =
            {
                Content.class.getName(), ContentContainer.class.getName(), ManagedService.class.getName()
            };
        m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
        return m_registration;
    }

    public final Component createComponent()
    {
        String destinationId = getDestinationId();
        int pos = destinationId.lastIndexOf( '.' );
        String id = destinationId.substring( pos + 1 );
        return createComponent( id );
    }

    protected abstract Component createComponent( String id );
}

