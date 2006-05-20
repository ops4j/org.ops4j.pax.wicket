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

import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.wicket.service.PaxWicketApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import wicket.protocol.http.WicketServlet;

public class PaxWicketAppFactoryTracker
    implements ServiceTrackerCustomizer
{

    private static final Log m_logger = LogFactory.getLog( PaxWicketAppFactoryTracker.class );
    private BundleContext m_bundleContext;
    private HttpTracker m_httpTracker;

    public PaxWicketAppFactoryTracker( BundleContext bundleContext, HttpTracker httpTracker )
    {
        m_bundleContext = bundleContext;
        m_httpTracker = httpTracker;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        final PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) m_bundleContext.getService( serviceReference );
        if( m_logger.isDebugEnabled() )
        {
            String message = "Service Added [" + serviceReference + "], Factory hash [" + System.identityHashCode(
                factory
            ) + "]";
            m_logger.debug( message );
        }

        WicketServlet servlet = new Servlet( factory );
        String mountPoint = getMountPoint( serviceReference );
        return addServlet( mountPoint, servlet, serviceReference );
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        String oldMountPoint = (String) object;
        String newMountPoint = (String) serviceReference.getProperty( PaxWicketApplicationFactory.MOUNTPOINT );
        if( oldMountPoint.equals( newMountPoint ) )
        {
            return;
        }
        WicketServlet servlet = m_httpTracker.getServlet( oldMountPoint );
        removedService( serviceReference, object );
        String mountPoint = getMountPoint( serviceReference );
        addServlet( mountPoint, servlet, serviceReference );
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        if( m_logger.isDebugEnabled() )
        {
            Object app = m_bundleContext.getService( serviceReference );
            String message = "Service removed [" + serviceReference + "], Application hash [" +
                             System.identityHashCode( app ) + "]";
            m_logger.debug( message );
        }

        String mountPoint = (String) object;
        m_httpTracker.removeServlet( mountPoint );
    }

    private String addServlet( String mountPoint, WicketServlet servlet, ServiceReference appFactory )
    {
        try
        {
            m_httpTracker.addServlet( mountPoint, servlet );
            return mountPoint;
        } catch( NamespaceException e )
        {
            throw new IllegalArgumentException( "Unable to mount [" + appFactory + "] on mount point '" + mountPoint + "'." );
        } catch( ServletException e )
        {
            String message = "Wicket Servlet for [" + appFactory + "] is unable to initialize. "
                             + "This servlet was tried to be mounted on '" + mountPoint + "'.";
            throw new IllegalArgumentException( message, e );
        }
    }

    private String getMountPoint( ServiceReference serviceReference )
    {
        String mp = PaxWicketApplicationFactory.MOUNTPOINT;
        String mountPoint = (String) serviceReference.getProperty( mp );
        if( mountPoint == null || mountPoint.length() == 0 )
        {
            String message = "PaxWicketApplicationFactory [" + serviceReference + "] MUST have a '" + mp + "' configuration property.";
            throw new IllegalArgumentException( message );
        }
        return mountPoint;
    }

}
