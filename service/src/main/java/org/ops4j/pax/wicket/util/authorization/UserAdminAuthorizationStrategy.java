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
import org.apache.wicket.authorization.*;
import org.osgi.service.useradmin.UserAdmin;

public class UserAdminAuthorizationStrategy
    implements IAuthorizationStrategy
{
    private final UserAdmin m_userAdmin;

    public UserAdminAuthorizationStrategy( UserAdmin userAdmin )
    {
        m_userAdmin = userAdmin;
    }

    public boolean isActionAuthorized( Component component, Action action )
    {
        final Class< ? extends Component> componentClass = component.getClass();
        final Role roleAnnotation = componentClass.getAnnotation( Role.class );
        if ( !isAuthorized( action, roleAnnotation ) )
            return false;

        return true;
    }

    public boolean isInstantiationAuthorized( Class componentClass )
    {
        final Role roleAnnotation = (Role)componentClass.getAnnotation( Role.class );
        if ( !isAuthorized( roleAnnotation ) )
            return false;

        return true;
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
        return false;
    }
}
