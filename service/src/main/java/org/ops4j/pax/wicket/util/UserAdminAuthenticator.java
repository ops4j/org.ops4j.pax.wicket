/*
 * Copyright 2006 Niclas Hedhman.
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

import java.util.Arrays;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is a PaxWicketAuthenticator that uses the User Admin Service as specified in the OSGi R4 specification.
 *
 * <p>
 * The <i>ApplicationName</i> must be provided in the constructor, and it will be used as the name space of the
 * users and credentials in the User Admin service.
 * </p>
 * <p>
 * The user will be obtained thru the call;
 * </p>
 * <code><pre>
 *     User user = userAdmin.getUser( m_applicationName + ".userid", username );
 * </pre></code>
 * <p>
 * and the credentials are checked with;
 * </p>
 * <code><pre>
 *   boolean authenticated = user.hasCredential( m_applicationName + ".password", password ) );
 * </pre></code>
 *
 * <p>
 * The Roles returned lacks any name space or prefixing.
 * </p>
 * <p>
 * All authentication will fail if no <code>UserAdmin</code> service is registered in the OSGi framework.
 * </p>
 */
public class UserAdminAuthenticator
    implements PaxWicketAuthenticator
{

    private static final Logger LOGGER = LoggerFactory.getLogger( UserAdminAuthenticator.class );

    private UserAdminTracker m_serviceTracker;
    private BundleContext m_bundleContext;
    private String m_applicationName;

    /**
     * Constructor for the UserAdminAuthenticator.
     *
     * @param bundleContext   The bundleContext of the Bundle where this class is used.
     * @param applicationName The name of the Pax Wicket application. This is the same as the name
     *                        registered in the <code>PaxWicketApplicationFactory</code> and used in all
     *                        <i>ContentAggregator</i>s and <i>ContentSource</i>s.
     *
     * @throws IllegalArgumentException if the <code>bundleContext</code> or the <code>applicationName</code>
     *                                  arguments are <code>null</code>.
     */
    public UserAdminAuthenticator( BundleContext bundleContext, String applicationName )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotNull( applicationName, "applicationName" );
        m_applicationName = applicationName;

        m_bundleContext = bundleContext;
        m_serviceTracker = new UserAdminTracker();
        m_serviceTracker.open();
    }

    /**
     * Releases all the resources used by this class.
     */
    public void dispose()
    {
        m_serviceTracker.close();
    }

    /**
     * Authenticate the specified {@code userName} with the specified {@code password}. Returns {@code null} if the
     * specified {@code userName} is not authenticate.
     *
     * @param username The username to authenticate.
     * @param password The password to authenticate.
     *
     * @return An instance of {@code Roles} if the arguments can be authenticated, {@code null} otherwise.
     *
     * @since 1.0.0
     */
    public Roles authenticate( String username, String password )
    {
        UserAdmin userAdmin = m_serviceTracker.getUserAdmin();
        if( userAdmin == null )
        {
            throw new SecurityException( "UserAdmin service not available." );
        }

        User user = userAdmin.getUser( m_applicationName + ".userid", username );
        if( user == null )
        {
            LOGGER.warn( "No user with the username of '" + username + "'" );
            return null;
        }

        if( !user.hasCredential( m_applicationName + ".password", password ) )
        {
            LOGGER.warn( "Wrong password issued by " + username );
            return null;
        }
        Authorization authorization = userAdmin.getAuthorization( user );
        if( authorization == null )
        {
            // anonymous user == no roles.
            return new Roles();
        }
        String[] uaRoles = authorization.getRoles();
        Roles wicketRoles = new Roles();
        wicketRoles.addAll( Arrays.asList( uaRoles ) );
        return wicketRoles;
    }

    /**
     * A Tracker for the UserAdmin service in the OSGi framework.
     *
     * <p>
     * This tracker will only hold the reference to the <code>UserAdmin</code>
     * found in the framework, and the user of the tracker will receive a <code>null</code>
     * in the <code>getUserAdmin</code> method call if no <code>UserAdmin</code> is registered.
     * </p>
     */
    private class UserAdminTracker extends ServiceTracker
    {

        private UserAdmin m_userAdmin;

        public UserAdminTracker()
        {
            super( m_bundleContext, UserAdmin.class.getName(), null );
        }

        public UserAdmin getUserAdmin()
        {
            return m_userAdmin;
        }

        /**
         * Default implementation of the
         * <code>ServiceTrackerCustomizer.addingService</code> method.
         *
         * <p>
         * This method is only called when this <code>ServiceTracker</code> object
         * has been constructed with a <code>null ServiceTrackerCustomizer</code>
         * argument.
         *
         * The default implementation returns the result of calling
         * <code>getService</code>, on the <code>BundleContext</code> object
         * with which this <code>ServiceTracker</code> object was created, passing
         * the specified <code>ServiceReference</code> object.
         * <p>
         * This method can be overridden in a subclass to customize the service
         * object to be tracked for the service being added. In that case, take care
         * not to rely on the default implementation of removedService that will
         * unget the service.
         *
         * @param reference Reference to service being added to this
         *                  <code>ServiceTracker</code> object.
         *
         * @return The service object to be tracked for the service added to this
         *         <code>ServiceTracker</code> object.
         *
         * @see org.osgi.util.tracker.ServiceTrackerCustomizer
         */
        @Override
        public Object addingService( ServiceReference reference )
        {
            m_userAdmin = (UserAdmin) m_bundleContext.getService( reference );
            return m_userAdmin;
        }

        /**
         * Default implementation of the
         * <code>ServiceTrackerCustomizer.removedService</code> method.
         *
         * <p>
         * This method is only called when this <code>ServiceTracker</code> object
         * has been constructed with a <code>null ServiceTrackerCustomizer</code>
         * argument.
         *
         * The default implementation calls <code>ungetService</code>, on the
         * <code>BundleContext</code> object with which this
         * <code>ServiceTracker</code> object was created, passing the specified
         * <code>ServiceReference</code> object.
         * <p>
         * This method can be overridden in a subclass. If the default
         * implementation of <code>addingService</code> method was used, this
         * method must unget the service.
         *
         * @param reference Reference to removed service.
         * @param service   The service object for the removed service.
         *
         * @see org.osgi.util.tracker.ServiceTrackerCustomizer
         */
        @Override
        public void removedService( ServiceReference reference, Object service )
        {
            m_userAdmin = null;
        }
    }
}
