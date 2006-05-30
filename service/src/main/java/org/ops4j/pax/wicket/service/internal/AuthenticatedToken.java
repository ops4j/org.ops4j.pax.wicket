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
package org.ops4j.pax.wicket.service.internal;

import java.util.Random;
import java.io.Serializable;

public class AuthenticatedToken
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private static long m_IdSequence = 0;

    private String m_Id;

    public AuthenticatedToken()
    {
        Random random = new Random();
        m_IdSequence = m_IdSequence + ( random.nextInt() % 100000 );
        m_Id = String.valueOf( m_IdSequence );
    }

    public boolean equals( Object o )
    {
        if( this == o )
        {
            return true;
        }
        if( o == null || getClass() != o.getClass() )
        {
            return false;
        }

        final AuthenticatedToken that = (AuthenticatedToken) o;

        if( !m_Id.equals( that.m_Id ) )
        {
            return false;
        }

        return true;
    }

    public int hashCode()
    {
        return m_Id.hashCode();
    }
}
