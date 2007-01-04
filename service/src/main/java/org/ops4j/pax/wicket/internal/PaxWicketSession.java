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
package org.ops4j.pax.wicket.internal;

import wicket.authentication.AuthenticatedWebSession;
import wicket.authentication.AuthenticatedWebApplication;
import wicket.authorization.strategies.role.Roles;
import java.io.Serializable;

public class PaxWicketSession extends AuthenticatedWebSession
    implements Serializable
{
    private static final long serialVersionUID = 1L;

    private AuthenticatedToken m_token;

    /**
     * Construct.
     *
     * @param application The web application
     */
    public PaxWicketSession( AuthenticatedWebApplication application )
    {
        super( application );
    }

    /**
     * Authenticates this session using the given username and password
     *
     * @param username The username
     * @param password The password
     *
     * @return True if the user was authenticated successfully
     */
    public boolean authenticate( final String username, final String password )
    {
        PaxAuthenticatedWicketApplication app = (PaxAuthenticatedWicketApplication) getApplication();
        m_token = app.authententicate( username, password );
        return m_token != null;
    }

    /**
     * @return Get the roles that this session can play
     */
    public Roles getRoles()
    {
        PaxAuthenticatedWicketApplication app = (PaxAuthenticatedWicketApplication) getApplication();
        Roles roles = app.getRoles( m_token );
        return roles;
    }

}
