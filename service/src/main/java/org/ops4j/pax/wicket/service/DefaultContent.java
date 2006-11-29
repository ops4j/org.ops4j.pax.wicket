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

import java.util.Dictionary;
import java.util.Properties;

import org.ops4j.lang.NullArgumentException;
import org.osgi.service.cm.ManagedService;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import wicket.Component;

public abstract class DefaultContent<E extends Component>
    implements Content<E>, ManagedService
{
    private BundleContext m_bundleContext;
    private Properties m_properties;
    private ServiceRegistration m_registration;

    /**
     * Construct an instance with {@code DefaultContent}.
     * 
     * @param bundleContext The bundle context. This argument must not be {@code null}.
     * @param contentId The content id. This argument must not be {@code null} or empty.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * 
     * @since 1.0.0
     */
    protected DefaultContent( BundleContext bundleContext, String contentId, String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( contentId, "contentId" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        m_properties = new Properties();
        m_properties.put( Constants.SERVICE_PID, CONTENTID + "/" + contentId );
        m_bundleContext = bundleContext;
        setContentId( contentId );
        setApplicationName( applicationName );
    }

    /**
     * Returns the destination id.
     * 
     * @return The destination id.
     * @since 1.0.0
     */
    public final String getDestinationId()
    {
        synchronized ( this )
        {
            return m_properties.getProperty( DESTINATIONID );
        }
    }

    /**
     * Sets the destination id.
     * 
     * @param destinationId The destination id. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the {@code destinationId} argument is not {@code null}.
     * 
     * @since 1.0.0
     */
    public final void setDestinationId( String destinationId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( destinationId, "destinationId" );

        synchronized ( this )
        {
            m_properties.put( DESTINATIONID, destinationId );
        }
    }

    /**
     * Create the wicket component with the specified {@code locale}.
     * 
     * @since 1.0.0
     */
    public final E createComponent()
        throws IllegalArgumentException
    {
        String destinationId = getDestinationId();
        int pos = destinationId.lastIndexOf( '.' );
        String wicketId = destinationId.substring( pos + 1 );

        return createComponent( wicketId );
    }

    /**
     * Returns the content id.
     * 
     * @return The content id.
     * 
     * @since 1.0.0
     */
    public final String getContentId()
    {
        synchronized ( this )
        {
            return m_properties.getProperty( CONTENTID );
        }
    }

    /**
     * Set the content id.
     * 
     * @param contentId The content id. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the {@code contentId} argument is {@code null}.
     * 
     * @since 1.0.0
     */
    private void setContentId( String contentId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( contentId, "contentId" );
        synchronized ( this )
        {
            m_properties.put( CONTENTID, contentId );
        }
    }

    /**
     * Returns the application name.
     * 
     * @return The application name.
     * 
     * @since 1.0.0
     */
    public final String getApplicationName()
    {
        synchronized ( this )
        {
            return m_properties.getProperty( APPLICATION_NAME );
        }
    }

    /**
     * Sets the application name.
     * 
     * @param applicationName The application name. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if the {@code applicationName} argument is {@code null}.
     * 
     * @since 1.0.0
     */
    public final void setApplicationName( String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        synchronized ( this )
        {
            m_properties.put( APPLICATION_NAME, applicationName );
        }
    }

    /**
     * Create component with the specified {@code wicketId}.
     * 
     * @param wicketId The wicket id. This argument must not be {@code null}.
     * 
     * @return The wicket component with the specified {@code wicketId}.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code wicketId} argument is {@code null}.
     * 
     * @since 1.0.0
     */
    protected abstract E createComponent( String wicketId )
        throws IllegalArgumentException;

    public final void updated( Dictionary config )
    {
        if ( config == null )
        {
            synchronized ( this )
            {
                m_registration.setProperties( m_properties );
            }
            return;
        }

        String destinationId = (String) config.get( DESTINATIONID );
        setDestinationId( destinationId );

        String appName = (String) config.get( APPLICATION_NAME );
        setApplicationName( appName );

        synchronized ( this )
        {
            m_registration.setProperties( config );
        }
    }

    /**
     * Register the specified {@code DefaultContent} instance.
     * 
     * @return The specified {@code DefaultContent}.
     * 
     * @since 1.0.0
     */
    public final ServiceRegistration register()
    {
        String[] serviceNames =
        {
            Content.class.getName(), ManagedService.class.getName()
        };

        synchronized ( this )
        {
            m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );

            return m_registration;
        }
    }
}
