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
package org.ops4j.pax.wicket.service.internal;

import java.util.HashMap;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebSession;

public class PaxWicketSession extends WebSession
{

    private HashMap m_Data;

    /**
     * Constructor
     *
     * @param application The application
     */
    public PaxWicketSession( WebApplication application )
    {
        super( application );
        m_Data = new HashMap();
    }

    public Object getData( String key )
    {
        return m_Data.get( key );
    }

    public void setData( String key, Object value )
    {
        m_Data.put( key, value );
    }
}
