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
package org.ops4j.pax.wicket.service;

import java.util.Dictionary;
import org.osgi.service.cm.ManagedService;
import wicket.Component;

public abstract class DefaultContent
    implements Content, ManagedService
{
    private String m_destinationId;

    protected DefaultContent( String destinationId )
    {
        m_destinationId = destinationId;
    }

    public final String getDestinationID()
    {
        return m_destinationId;
    }

    public final Component createComponent()
    {
        int pos = m_destinationId.lastIndexOf( '.' );
        String id = m_destinationId.substring( pos + 1 );
        return createComponent( id );
    }

    protected abstract Component createComponent( String id );

    public final void updated( Dictionary config )
    {
        m_destinationId = (String) config.get( CONFIG_DESTINATIONID );
    }
}
