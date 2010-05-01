/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2010 David Leangen.
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

import java.io.Serializable;
import org.apache.wicket.Request;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.ops4j.pax.wicket.api.PaxWicketAuthentication;

public final class PaxWicketSession extends AuthenticatedWebSession
    implements Serializable, PaxWicketAuthentication
{

    private static final long serialVersionUID = 1L;

    private AuthenticatedToken m_token;
    private String m_loggedInUser;

    /**
     * Construct the instance of pax wicket session.
     *
     * @param request The incoming request.
     */
    public PaxWicketSession( Request request )
    {
        super( request );
    }

    /**
     * Authenticates this session using the given username and password
     *
     * @param username The username
     * @param password The password
     *
     * @return True if the user was authenticated successfully
     */
    @Override
    public final boolean authenticate( String username, String password )
    {
        PaxAuthenticatedWicketApplication app = (PaxAuthenticatedWicketApplication) getApplication();

        m_token = app.authenticate( username, password );
        if( m_token != null )
        {
            m_loggedInUser = username;
            return true;
        }

        m_loggedInUser = null;

        return false;
    }

    public final String getLoggedInUser()
    {
        return m_loggedInUser;
    }

    @Override
    public final void invalidateNow()
    {
        m_token = null;
        m_loggedInUser = null;

        super.invalidateNow();
    }

    /**
     * @return Get the roles that this session can play
     */
    @Override
    public final Roles getRoles()
    {
        PaxAuthenticatedWicketApplication app = (PaxAuthenticatedWicketApplication) getApplication();
        return app.getRoles( m_token );
    }

}
