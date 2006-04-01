/*
 * Copyright 2005 Niclas Hedhman.
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
package org.ops4j.pax.wicket;

import java.net.URL;

public final class Resource
{

    private String m_MimeType;
    private URL m_URL;

    public Resource( URL url, String mimeType )
    {
        Activator.debug( "Resource( " + url + ", " + mimeType + " )" );
        m_MimeType = mimeType;
        m_URL = url;
    }

    public URL getURL()
    {
        return m_URL;
    }

    public String getMimeType()
    {
        return m_MimeType;
    }

    public int hashCode()
    {
        int hash = 1964923741;
        if( m_MimeType != null )
            hash = hash + m_MimeType.hashCode();
        if( m_URL != null )
        {
            hash = hash + m_URL.hashCode();
        }
        return hash;
    }

    public boolean equals( Object obj )
    {
        if( obj instanceof Resource == false )
        {
            return false;
        }
        Resource other = (Resource) obj;
        if( m_MimeType == null )
        {
            if( other.m_MimeType != null )
            {
                return false;
            }
        }
        else
        {
            if( m_MimeType.equals( other.m_MimeType ) == false )
            {
                return false;
            }
        }
        if( m_URL == null )
        {
            if( other.m_URL != null )
            {
                return false;
            }
        }
        else
        {
            if( m_URL.equals( other.m_URL ) == false )
            {
                return false;
            }
        }
        return true;
    }

    public String toString()
    {
        String type = "";
        if( m_MimeType != null )
        {
            type = m_MimeType + ", ";
        }
        return "Resource[" + type + m_URL + "]";
    }
}
