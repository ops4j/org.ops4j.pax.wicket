/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 * Copyright 2007 David Leangen
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
import java.util.Hashtable;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Session;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ManagedService;

public abstract class AbstractContentSource<E extends Component>
    implements ContentSource<E>, ManagedService
{

    private static final String[] ROLES_TYPE = new String[0];
    private static final PaxWicketAuthentication DUMMY_AUTHENTICATION = new PaxWicketAuthentication()
    {
        public String getLoggedInUser()
        {
            return null;
        }

        public Roles getRoles()
        {
            return new Roles();
        }
    };

    private BundleContext m_bundleContext;
    private Dictionary<String, Object> m_properties;
    private ServiceRegistration m_registration;
    private Roles m_requiredRoles;
    private Roles m_basicRoles;

    /**
     * Construct an instance with {@code AbstractContentSource}.
     *
     * @param bundleContext   The bundle context. This argument must not be {@code null}.
     * @param wicketId        The WicketId. This argument must not be {@code null} or empty.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     *
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    protected AbstractContentSource( BundleContext bundleContext, String wicketId, String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        m_properties = new Hashtable<String, Object>();
        m_properties.put( Constants.SERVICE_PID, SOURCE_ID + "/" + wicketId );
        m_bundleContext = bundleContext;

        setWicketId( wicketId );
        setApplicationName( applicationName );
    }

    /**
     * Returns the destinations.
     *
     * @return The destinations.
     *
     * @since 1.0.0
     */
    public final String[] getDestinations()
    {
        return getStringArrayProperty( DESTINATIONS );
    }

    /**
     * Sets the destination id.
     *
     * @param destinationIds The destination ids. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the {@code destinationId} argument is not {@code null}.
     * @since 1.0.0
     */
    public final void setDestination( String... destinationIds )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( destinationIds, "destinationIds" );

        m_properties.put( DESTINATIONS, destinationIds );
    }

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * </ul>
     * </p>
     *
     * @return The wicket component represented by this {@code ContentSource} instance, or null if user has no access to
     *         this ContentSource.
     *
     * @since 1.0.0
     */
    public final E createSourceComponent( String wicketId )
        throws IllegalArgumentException
    {
        boolean isRolesApproved = isRolesAuthorized();
        if( isRolesApproved )
        {
            return createWicketComponent( wicketId );
        }
        else
        {
            return onAuthorizationFailed( wicketId );
        }
    }

    public final E createSourceComponent( String wicketId, MarkupContainer parent )
        throws IllegalArgumentException
    {
        boolean isRolesApproved = isRolesAuthorized();
        if( isRolesApproved )
        {
            return createWicketComponent( wicketId, parent );
        }
        else
        {
            return onAuthorizationFailed( wicketId );
        }
    }

    /**
     * This method is called when the Authorization of the ContentSource has failed.
     *
     * @param wicketId The WicketId of the content to be created.
     *
     * @return null by default. Override to return a customized <i>protected</i> component, such as a label
     *         without the link.
     */
    protected E onAuthorizationFailed( String wicketId )
    {
        return null;
    }

    /**
     * Returns {@code true} if the user roles is authorized to create this content source component, {@code false}
     * otherwise.
     *
     * @return A {@code boolean} indicator whether the user roles can create this content source component.
     *
     * @since 1.0.0
     */
    private boolean isRolesAuthorized()
    {
        PaxWicketAuthentication authentication = getAuthentication();
        Roles userRoles = authentication.getRoles();

        boolean isRequiredRolesAuthorized = true;
        if( m_requiredRoles != null )
        {
            isRequiredRolesAuthorized = m_requiredRoles.hasAllRoles( userRoles );
        }

        boolean isBasicRolesAuthorized = true;
        if( m_basicRoles != null && !m_basicRoles.isEmpty() )
        {
            isBasicRolesAuthorized = userRoles.hasAnyRole( m_basicRoles );
        }

        return isRequiredRolesAuthorized && isBasicRolesAuthorized;
    }

    private String[] getStringArrayProperty( String key )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( key, "key" );

        return (String[]) m_properties.get( key );
    }

    private String getStringProperty( String key, String defaultValue )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( key, "key" );

        String value = (String) m_properties.get( key );
        if( value == null )
        {
            return defaultValue;
        }
        return value;
    }

    /**
     * Returns the content source id.
     *
     * @return The content source id.
     *
     * @since 1.0.0
     */
    public final String getSourceId()
    {
        return getStringProperty( SOURCE_ID, null );
    }

    /**
     * Set the WicketId.
     *
     * @param wicketId The WicketId. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the {@code wicketId} argument is {@code null}.
     * @since 1.0.0
     */
    private void setWicketId( String wicketId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( wicketId, "wicketId" );
        m_properties.put( SOURCE_ID, wicketId );
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
        return getStringProperty( APPLICATION_NAME, null );
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

        return DUMMY_AUTHENTICATION;
    }

    /**
     * Sets the application name.
     *
     * @param applicationName The application name. This argument must not be {@code null}.
     *
     * @throws IllegalArgumentException Thrown if the {@code applicationName} argument is {@code null}.
     * @since 1.0.0
     */
    public final void setApplicationName( String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );

        m_properties.put( APPLICATION_NAME, applicationName );
    }

    /**
     * Create component with the specified {@code wicketId}.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * </ul>
     * </p>
     *
     * @param wicketId The WicketId. This argument must not be {@code null}.
     *
     * @return The wicket component with the specified {@code wicketId}.
     *
     * @throws IllegalArgumentException Thrown if the either or both arguments are {@code null}.
     * @since 1.0.0
     */
    protected abstract E createWicketComponent( String wicketId )
        throws IllegalArgumentException;

    /**
     * Default implementation that ignores the parent component.
     * Override this if you want to inject the parent component into your
     * created Wicket {@code Component}
     * 
     * @param wicketId The WicketId. This argument must not be {@code null}.
     * @param parent the parent {@code MarkupContainer} 
     * 
     * @return The wicket component with the specified {@code wicketId}.
     *
     * @throws IllegalArgumentException Thrown if the either or both arguments are {@code null}.
     */
    protected <C extends MarkupContainer>E createWicketComponent( String wicketId, C parent )
        throws IllegalArgumentException
    {
        return createWicketComponent( wicketId );
    }

    @SuppressWarnings( "unchecked" )
    public final void updated( Dictionary config )
    {
        synchronized( this )
        {
            if( config != null )
            {
                m_properties = config;
                String[] required = (String[]) m_properties.get( REQUIRED_ROLES );
                if( required != null )
                {
                    m_requiredRoles = new Roles( required );
                }
                else
                {
                    m_requiredRoles = new Roles();
                }
                String[] basic = (String[]) m_properties.get( BASIC_ROLES );
                if( basic != null )
                {
                    m_basicRoles = new Roles( basic );
                }
                else
                {
                    m_basicRoles = new Roles();
                }
                m_registration.setProperties( m_properties );
            }
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
        synchronized( this )
        {
            String[] serviceNames =
                {
                    ContentSource.class.getName(), ManagedService.class.getName()
                };
            m_registration = m_bundleContext.registerService( serviceNames, this, m_properties );
            return m_registration;
        }
    }

    public Roles getRequiredRoles()
    {
        return m_requiredRoles;
    }

    public Roles getBasicRoles()
    {
        return m_basicRoles;
    }

    public final void setRoles( Roles requiredRoles, Roles basicRoles )
    {
        boolean changed = false;
        if( requiredRoles != null )
        {
            changed = true;
            m_requiredRoles = requiredRoles;
            m_properties.put( REQUIRED_ROLES, requiredRoles.toArray( ROLES_TYPE ) );
        }

        if( basicRoles != null )
        {
            m_basicRoles = basicRoles;
            m_properties.put( REQUIRED_ROLES, basicRoles.toArray( ROLES_TYPE ) );
            changed = true;
        }

        if( changed && m_registration != null )
        {
            m_registration.setProperties( m_properties );
        }
    }
}
