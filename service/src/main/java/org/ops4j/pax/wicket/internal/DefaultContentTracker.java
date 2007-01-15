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
package org.ops4j.pax.wicket.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.log4j.Logger;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.ContentSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * {@code DefaultContentTracker} tracks {@link ContentSource} services.
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
public final class DefaultContentTracker extends ServiceTracker
{

    private static final Logger m_logger = Logger.getLogger( DefaultContentTracker.class );

    private final BundleContext m_context;
    private final ContentTrackingCallback m_callback;
    private final String m_containmentId;

    private final List<ServiceReference> m_references;

    /**
     * Construct an instance of {@code DefaultContentTracker} with the specified arguments.
     * 
     * @param context The bundle context. This argument must not be {@code null}.
     * @param callback The callback. This argument must not be {@code null}.
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * @param containmentId The containment id. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    public DefaultContentTracker( BundleContext context, ContentTrackingCallback callback, String applicationName,
        String containmentId )
        throws IllegalArgumentException
    {
        super( context, TrackingUtil.createContentFilter( context, applicationName ), null );

        NullArgumentException.validateNotEmpty( containmentId, "containmentId" );
        NullArgumentException.validateNotNull( callback, "callback" );

        m_context = context;
        m_callback = callback;
        m_containmentId = containmentId;

        m_references = new ArrayList<ServiceReference>();
    }

    /**
     * Close the tracker. This method assumes that all {@code ContentSource} that are stored by invoking
     * {@link ContentTrackingCallback#addContent(String,ContentSource)} has been freed.
     * 
     * @since 1.0.0
     */
    @Override
    public final synchronized void close()
    {
        synchronized ( this )
        {
            int startIndexOfWicketId = m_containmentId.length() + 1;

            for ( ServiceReference reference : m_references )
            {
                String destinationId = (String) reference.getProperty( ContentSource.DESTINATION );
                String wicketId = destinationId.substring( startIndexOfWicketId );
                ContentSource content = (ContentSource) m_context.getService( reference );

                m_callback.removeContent( wicketId, content );

                m_context.ungetService( reference ); // Removal for the first get during add
                m_context.ungetService( reference ); // Removal for the second get in this loop block
            }

            m_references.clear();
        }

        super.close();
    }

    /**
     * Adding service.
     * 
     * @see ServiceTracker#addingService(ServiceReference)
     * @since 1.0.0
     */
    public final Object addingService( ServiceReference serviceReference )
    {
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Service Reference [" + serviceReference + "] has been added." );
        }

        String dest = (String) serviceReference.getProperty( ContentSource.DESTINATION );

        Object service;
        synchronized ( this )
        {
            service = m_context.getService( serviceReference );
        }

        if ( dest == null )
        {
            return service;
        }
        if ( dest.startsWith( "regexp(" ) )
        {
            return matchRegularExpression( dest, service, serviceReference );
        }
        else if ( !dest.startsWith( m_containmentId ) )
        {
            return service;
        }

        return matchDirect( dest, service, serviceReference );
    }

    private Object matchRegularExpression( String dest, Object service, ServiceReference serviceReference )
    {
        int lastParan = dest.lastIndexOf( ")." );
        if ( lastParan < 0 )
        {
            String message = "Regular Expressions must have the format: \"regexp(\"[expression]\").\"[wicketId]";
            throw new IllegalArgumentException( message );
        }
        String expression = dest.substring( 7, lastParan );
        if ( Pattern.matches( expression, m_containmentId ) )
        {
            synchronized ( this )
            {
                String id = dest.substring( lastParan + 2 );
                m_callback.addContent( id, (ContentSource) service );
                m_references.add( serviceReference );
            }
        }
        return service;
    }

    private Object matchDirect( String dest, Object service, ServiceReference serviceReference )
    {
        int contIdLength = m_containmentId.length();
        if ( dest.length() == contIdLength )
        {
            String message = "The '" + ContentSource.DESTINATION + "' property have the form ["
                + ContentSource.AGGREGATION_POINT + "].[wicketId] but was " + dest;

            throw new IllegalArgumentException( message );
        }

        if ( dest.charAt( contIdLength ) != '.' )
        {
            return service;
        }

        String id = dest.substring( contIdLength + 1 );

        if ( m_logger.isInfoEnabled() )
        {
            m_logger.info( "Attaching content with wicket:id [" + id + "] to containment [" + m_containmentId + "]" );
        }

        synchronized ( this )
        {
            m_callback.addContent( id, (ContentSource) service );
            m_references.add( serviceReference );
        }

        return service;
    }

    /**
     * Handle modified service.
     * 
     * @see ServiceTracker#modifiedService(ServiceReference,Object)
     * @since 1.0.0
     */
    public final void modifiedService( ServiceReference serviceReference, Object object )
    {
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Service Reference [" + serviceReference + "] has been modified." );
        }

        removedService( serviceReference, object );
        addingService( serviceReference );
    }

    /**
     * Handle removed service.
     * 
     * @see ServiceTracker#removedService(ServiceReference,Object)
     * @since 1.0.0
     */
    public void removedService( ServiceReference serviceReference, Object object )
    {
        if ( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Service Reference [" + serviceReference + "] has been removed." );
        }

        if ( !(object instanceof ContentSource) )
        {
            String message = "OSGi Framework not passing a ContentSource object as specified in R4 spec.";
            throw new IllegalArgumentException( message );
        }

        ContentSource content = (ContentSource) object;
        String destionationId = content.getDestinationId();
        if ( destionationId == null )
        {
            m_logger.warn( "ContentSource [" + content + "] does not have destionationId defined." );
        }
        else
        {
            int pos = destionationId.lastIndexOf( '.' );
            String id = destionationId.substring( pos + 1 );
            boolean wasContentRemoved = m_callback.removeContent( id, content );

            if ( m_logger.isInfoEnabled() && wasContentRemoved )
            {
                m_logger.info( "Detaching content with wicket:id [" + id + "] from containment [" + m_containmentId
                    + "]" );
            }
        }

        synchronized ( this )
        {
            m_context.ungetService( serviceReference );
            m_references.remove( serviceReference );
        }
    }
}
