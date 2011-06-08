/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * $Id: UserRolesAuthorizer.java 459160 2006-02-11 06:14:33Z jonl $ $Revision: 459160 $ $Date: 2006-02-11 15:14:33 +0900 (Sat, 11 Feb 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ops4j.pax.wicket.samples.wicketauth.example;

import wicket.Session;
import wicket.authorization.strategies.role.IRoleCheckingStrategy;
import wicket.authorization.strategies.role.Roles;

/**
 * The authorizer we need to provide to the authorization strategy
 * implementation
 * {@link wicket.authorization.strategies.role.annotations.AnnotationsRoleAuthorizationStrategy}.
 * 
 * @author Eelco Hillenius
 */
public class UserRolesAuthorizer implements IRoleCheckingStrategy
{

	/**
	 * Construct.
	 */
	public UserRolesAuthorizer()
	{
	}

	/**
	 * @see wicket.authorization.strategies.role.IRoleCheckingStrategy#hasAnyRole(Roles)
	 */
	public boolean hasAnyRole(Roles roles)
	{
		RolesSession authSession = (RolesSession)Session.get();
		return authSession.getUser().hasAnyRole(roles);
	}

}
