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

import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.ContentContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DefaultContentTracking
    implements ServiceTrackerCustomizer
{

    private String m_containmentId;
    private BundleContext m_context;
    private ContentTrackingCallback m_callback;

    public DefaultContentTracking( BundleContext context, ContentTrackingCallback callback )
    {
        m_context = context;
        m_callback = callback;
    }

    public void setContainmentId( String containmentId )
    {
        m_containmentId = containmentId;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        String dest = (String) serviceReference.getProperty( "destinationId" );
        Object service = m_context.getService( serviceReference );
        if( dest == null )
        {
            return service;
        }
        if( !dest.startsWith( m_containmentId ) )
        {
            return service;
        }
        int contIdLength = m_containmentId.length();
        if( dest.length() == contIdLength )
        {
            String message = "The '" + Content.CONFIG_DESTINATIONID + "' property have the form ["
                             + ContentContainer.CONFIG_CONTAINMENTID + "].[wicketId] but was " + dest;
            throw new IllegalArgumentException( message );
        }
        if( dest.charAt( contIdLength ) != '.' )
        {
            return service;
        }
        if( !( service instanceof Content ) )
        {
            throw new IllegalArgumentException( "OSGi Framework not passing a Content object as specified in R4 spec."
            );
        }
        String id = dest.substring( contIdLength );
        m_callback.addContent( id, (Content) service );
        return service;
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        removedService( serviceReference, object );
        addingService( serviceReference );
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        if( !( object instanceof Content ) )
        {
            throw new IllegalArgumentException( "OSGi Framework not passing a Content object as specified in R4 spec."
            );
        }
        Content content = (Content) object;
        String destionationId = content.getDestinationID();
        int pos = destionationId.lastIndexOf( '.' );
        String id = destionationId.substring( pos + 1 );
        m_callback.removeContent( id, content );
    }
}
