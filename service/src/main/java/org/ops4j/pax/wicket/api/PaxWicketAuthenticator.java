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
package org.ops4j.pax.wicket.api;

import org.apache.wicket.authorization.strategies.role.Roles;

/**
 * {@code PaxWicketAuthenticator} provides an interface to authenticate users with its password.
 * 
 * @see org.ops4j.pax.wicket.util.UserAdminAuthenticator
 * @since 1.0.0
 */
public interface PaxWicketAuthenticator {

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
    Roles authenticate(String username, String password);
}
