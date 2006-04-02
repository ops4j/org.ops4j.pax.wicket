/*
 * $Id: MyAuthenticatedWebSession.java 4790 2006-03-06 23:59:16 +0100 (Mon, 06 Mar 2006) eelco12 $
 * $Revision: 4790 $
 * $Date: 2006-03-06 23:59:16 +0100 (Mon, 06 Mar 2006) $
 *
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ops4j.pax.wicket.test;

import wicket.authentication.AuthenticatedWebApplication;
import wicket.authentication.AuthenticatedWebSession;
import wicket.authorization.strategies.role.Roles;

public class TestWebSession extends AuthenticatedWebSession
{
	public TestWebSession( final AuthenticatedWebApplication application )
	{
		super( application );
    }

	/**
	 * @see wicket.authentication.AuthenticatedWebSession#authenticate(java.lang.String,
	 *      java.lang.String)
	 */
	@Override
	public boolean authenticate( final String username, final String password )
	{
		// Check username and password
		return "wicket".equals( username ) && "wicket".equals( password );
	}

	@Override
	public Roles getRoles()
	{
		if (isSignedIn())
		{
			// If the user is signed in, they have these roles
			return new Roles( Roles.ADMIN );
		}
		return null;
	}
}
