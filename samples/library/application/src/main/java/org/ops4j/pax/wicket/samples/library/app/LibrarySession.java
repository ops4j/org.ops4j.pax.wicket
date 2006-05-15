/*
 * $Id: LibrarySession.java 3156 2005-11-08 06:39:28Z jdonnerstag $
 * $Revision: 3156 $
 * $Date: 2005-11-08 14:39:28 +0800 (Tue, 08 Nov 2005) $
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.library.app;

import java.util.HashMap;
import java.util.Map;
import wicket.authentication.AuthenticatedWebApplication;
import wicket.authentication.AuthenticatedWebSession;
import wicket.authorization.strategies.role.Roles;

/**
 * Session class for library example.  Holds User object and authenticates users.
 *
 * @author Jonathan Locke
 */
public final class LibrarySession extends AuthenticatedWebSession
{
    private Map<String, String> m_passwords;

    /**
     * Constructor
     *
     * @param application The application
     */
    protected LibrarySession( final AuthenticatedWebApplication application )
    {
        super( application );
        m_passwords = new HashMap<String, String>();
        m_passwords.put( "user1", "1234" );
        m_passwords.put( "user2", "1234" );
        m_passwords.put( "user3", "1234" );
        m_passwords.put( "user4", "1234" );
        m_passwords.put( "a", "a" );
        m_passwords.put( "b", "b" );
        m_passwords.put( "c", "c" );
        m_passwords.put( "d", "d" );
    }

    /**
     * Checks the given username and password, returning a User object if
     * if the username and password identify a valid user.
     *
     * @param username The username
     * @param password The password
     *
     * @return The signed in user
     */
    @Override
    public final boolean authenticate( final String username, final String password )
    {
        boolean success = password.equals( m_passwords.get( username ) );
        return success;
    }

    /**
     * @return Get the roles that this session can play
     */
    @Override
    public Roles getRoles()
    {
        if( isSignedIn() )
        {
            return new Roles( Roles.ADMIN );
        }
        return getRoles();
    }
}


