/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util.authorization;

import org.apache.wicket.Component;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.*;
import org.apache.wicket.authorization.strategies.role.IRoleCheckingStrategy;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;
import org.osgi.service.useradmin.*;

/**
 * Implementation of IAuthorizationStrategy that is backed directly by the
 * OSGi UserAdmin service. Note that this is a simple and probably incomplete
 * implementation. Some issues not addressed:
 * 
 *   - how do we handle anonymous classes?
 *   - how do we handle class hierarchies?
 * 
 * The approach is to create a {@code Group} with the name of the action to
 * authorize (or not). This is done by using the Role annotation. Since
 * there are several actions for each wicket component, this is the 
 * convention:
 * 
 *    NameOfAction --> instantiation of that component
 *    NameOfAction.ENABLE --> enable for that component
 *    NameOfAction.RENDER --> render for that component
 * 
 * This will of course be expanded if/when the wicket team adds additional
 * {@code Action}s.
 * 
 * As for the actual authorization logic, this is handled normally via
 * the UserAdmin service.
 * 
 * @author David Leangen
 */
public class UserAdminAuthorizationStrategy
    implements IAuthorizationStrategy
{
    /**
     * The default parameter to use for obtaining a user from the UserAdmin
     * service. Use like so:
     * <pre>
     *     User user = userAdmin.getUser( PAX_WICKET_USER_PARAM, loginName );
     * </pre>
     */
    public static final String PAX_WICKET_USER_PARAM = "wicket.username";

    private final UserAdmin m_userAdmin;

    public UserAdminAuthorizationStrategy( UserAdmin userAdmin )
    {
        m_userAdmin = userAdmin;
    }

    public final boolean isActionAuthorized( Component component, Action action )
    {
        final Class< ? extends Component> componentClass = component.getClass();
        final Role roleAnnotation = componentClass.getAnnotation( Role.class );
        return isAuthorized( action, roleAnnotation );
    }

    public final boolean isInstantiationAuthorized( Class componentClass )
    {
        final Role roleAnnotation = (Role)componentClass.getAnnotation( Role.class );
        return isAuthorized( roleAnnotation );
    }

    private boolean isAuthorized( Role roleAnnotation )
    {
        if ( roleAnnotation != null )
        {
            final String role = roleAnnotation.value();
            return isAuthorized( role );
        }

        return true;
    }

    private boolean isAuthorized( Action action, Role roleAnnotation )
    {
        if ( roleAnnotation != null )
        {
            final StringBuilder s = new StringBuilder();
            s.append( roleAnnotation.value() );
            s.append( "." );
            s.append( action.getName() );
            return isAuthorized( s.toString() );
        }

        return true;
    }

    private boolean isAuthorized( String role )
    {
        // This is totall hackish.
        // The only way to avoid this is to use something other than
        // AuthenticatedWebSession. As it stands, we are trying to force
        // a use case on AuthenticatedWebSession that it was not intended for.
        final PaxWicketAuthentication paxWicketAuth = (PaxWicketAuthentication) AuthenticatedWebSession.get();
        final String loginName = paxWicketAuth.getLoggedInUser();
        final User user = getUser( m_userAdmin, loginName );
        if( null == user )
            return false;

        final Authorization auth = m_userAdmin.getAuthorization( user );
        return auth.hasRole( role );
    }

    /**
     * Override this method in order to provide a different way
     * of obtaining the User from the UserAdmin service.
     * 
     * @param userAdmin  UserAdmin service
     * @param loginName  the name under which the user is logged in
     * 
     * @return the User object, or {@code null} if no such user
     */
    protected User getUser( UserAdmin userAdmin, String loginName )
    {
        if( null == loginName )
            return null;

        return userAdmin.getUser( PAX_WICKET_USER_PARAM, loginName );
    }
}
