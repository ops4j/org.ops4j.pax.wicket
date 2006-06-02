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
package org.ops4j.pax.wicket.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.useradmin.Authorization;
import org.osgi.service.useradmin.User;
import org.osgi.service.useradmin.UserAdmin;
import org.osgi.util.tracker.ServiceTracker;
import wicket.authorization.strategies.role.Roles;

public class UserAdminAuthenticator
    implements PaxWicketAuthenticator
{

    private static final Log m_logger = LogFactory.getLog( UserAdminAuthenticator.class.getName() );

    private UserAdminTracker m_serviceTracker;
    private BundleContext m_bundleContext;

    public UserAdminAuthenticator( BundleContext bundleContext )
    {
        m_bundleContext = bundleContext;
        m_serviceTracker = new UserAdminTracker();
        m_serviceTracker.open();
    }

    public void dispose()
    {
        m_serviceTracker.close();
    }

    public Roles authenticate( String username, String password )
    {
        UserAdmin userAdmin = m_serviceTracker.getUserAdmin();
        if( userAdmin == null )
        {
            throw new SecurityException( "UserAdmin service not available." );
        }
        User user = userAdmin.getUser( PaxWicketAuthenticator.USERNAME_IDENTITY, username );
        if( user == null )
        {
            m_logger.warn( "No user with the username of '" + username + "'" );
            return null;
        }
        if( ! user.hasCredential( CREDENTIALS_PASSWORD, password ) )
        {
            m_logger.warn( "Wrong password issued by " + username );
            return null;
        }
        Authorization authorization = userAdmin.getAuthorization( user );
        String[] uaRoles = authorization.getRoles();
        Roles wicketRoles = new Roles();
        for( String uaRole : uaRoles )
        {
            wicketRoles.add( uaRole );
        }
        return wicketRoles;
    }

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
        public void removedService( ServiceReference reference, Object service )
        {
            m_userAdmin = null;
        }
    }
}
