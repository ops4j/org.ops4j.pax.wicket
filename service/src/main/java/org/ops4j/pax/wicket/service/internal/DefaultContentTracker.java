/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.wicket.service.Content;
import org.ops4j.pax.wicket.service.ContentContainer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class DefaultContentTracker
    implements ServiceTrackerCustomizer
{

    private static final Log m_logger = LogFactory.getLog( DefaultContentTracker.class );

    private String m_containmentId;
    private BundleContext m_context;
    private ContentTrackingCallback m_callback;

    public DefaultContentTracker( BundleContext context, ContentTrackingCallback callback )
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
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Service Reference [" + serviceReference + "] has been added." );
        }

        String dest = (String) serviceReference.getProperty( Content.CONFIG_DESTINATIONID );
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
            String message = "OSGi Framework not passing a Content object as specified in R4 spec.";
            throw new IllegalArgumentException( message );
        }
        String id = dest.substring( contIdLength + 1 );
        m_logger.info( "Attaching content with wicket:id [" + id + "] to containment [" + m_containmentId + "]" );
        m_callback.addContent( id, (Content) service );
        return service;
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Service Reference [" + serviceReference + "] has been modified." );
        }
        removedService( serviceReference, object );
        addingService( serviceReference );
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Service Reference [" + serviceReference + "] has been removed." );
        }

        if( !( object instanceof Content ) )
        {
            String message = "OSGi Framework not passing a Content object as specified in R4 spec.";
            throw new IllegalArgumentException( message );
        }

        Content content = (Content) object;
        String destionationId = content.getDestinationId();
        int pos = destionationId.lastIndexOf( '.' );
        String id = destionationId.substring( pos + 1 );
        boolean wasContentRemoved = m_callback.removeContent( id, content );
        if( m_logger.isInfoEnabled() && wasContentRemoved )
        {
            m_logger.info(
                "Detaching content with wicket:id [" + id + "] from containment [" + m_containmentId + "]"
            );
        }
    }

}
