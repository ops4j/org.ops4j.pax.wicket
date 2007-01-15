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
package org.ops4j.pax.wicket.util;

import java.util.Dictionary;
import java.util.Properties;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

import wicket.Component;

public abstract class AbstractContentSource<E extends Component>
    implements ContentSource<E>, ManagedService
{
    private BundleContext m_bundleContext;
    private Properties m_properties;
    private ServiceRegistration m_registration;

    /**
     * Construct an instance with {@code AbstractContentSource}.
     *
     * @param bundleContext The bundle context. This argument must not be {@code null}.
     * @param contentId The content id. This argument must not be {@code null} or empty.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     *
     * @since 1.0.0
     */
    protected AbstractContentSource( BundleContext bundleContext, String contentId, String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( contentId, "contentId" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        m_properties = new Properties();
        m_properties.put( Constants.SERVICE_PID, SOURCE_ID + "/" + contentId );
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
            return m_properties.getProperty( DESTINATION );
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
            m_properties.put( DESTINATION, destinationId );
        }
    }

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * <li>In the use case of Wicket 2 environment. The parent is passed through constructor during creational of the
     * component created by this method.</li>
     * </ul>
     * </p>
     *
     * @param parent The parent component of the component to be created by this method. This argument must not be
     *            {@code null}.
     *
     * @return The wicket component represented by this {@code ContentSource} instance.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code parent} arguement is {@code null}.
     * @since 1.0.0
     */
    public final <T extends Component> E createComponent( T parent )
        throws IllegalArgumentException
    {
        String destinationId = getDestinationId();
        int pos = destinationId.lastIndexOf( '.' );
        String contentId = destinationId.substring( pos + 1 );

        return createComponent( contentId, parent );
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
            return m_properties.getProperty( SOURCE_ID );
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
            m_properties.put( SOURCE_ID, contentId );
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
     * Create component with the specified {@code contentId}.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * <li>In the use case of Wicket 2 environment. The parent is passed through constructor during creational of the
     * component created by this method.</li>
     * </ul>
     * </p>
     *
     * @param contentId The wicket id. This argument must not be {@code null}.
     * @param parent The parent component of created component of this method. This argument must not be {@code null}.
     *
     * @return The wicket component with the specified {@code contentId}.
     *
     * @throws IllegalArgumentException Thrown if the either or both arguments are {@code null}.
     *
     * @since 1.0.0
     */
    protected abstract <T extends Component> E createComponent( String contentId, T parent )
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

        String destinationId = (String) config.get( DESTINATION );
        setDestinationId( destinationId );

        String appName = (String) config.get( APPLICATION_NAME );
        setApplicationName( appName );

        synchronized ( this )
        {
            m_registration.setProperties( config );
        }
    }

    /**
     * Register the specified {@code AbstractContentSource} instance.
     *
     * @return The specified {@code AbstractContentSource}.
     *
     * @since 1.0.0
     */
    public final ServiceRegistration register()
    {
        String[] serviceNames =
        {
            ContentSource.class.getName(), ManagedService.class.getName()
        };

        synchronized ( this )
        {
            m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );

            return m_registration;
        }
    }
}
