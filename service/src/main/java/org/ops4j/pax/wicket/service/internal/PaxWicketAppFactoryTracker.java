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
import org.ops4j.pax.wicket.service.PaxWicketApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import org.apache.log4j.Logger;
import wicket.protocol.http.WicketServlet;

public class PaxWicketAppFactoryTracker extends ServiceTracker
{

    private static final Logger m_logger = Logger.getLogger( PaxWicketAppFactoryTracker.class );
    private BundleContext m_bundleContext;
    private HttpTracker m_httpTracker;

    public PaxWicketAppFactoryTracker( BundleContext bundleContext, HttpTracker httpTracker )
    {
        super( bundleContext, PaxWicketApplicationFactory.class.getName(), null );
        m_bundleContext = bundleContext;
        m_httpTracker = httpTracker;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        final PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) m_bundleContext.getService( serviceReference );
        String mountPoint = factory.getMountPoint();
        if( m_logger.isDebugEnabled() )
        {
            String message = "Service Added [" + serviceReference + "], Factory hash [" + System.identityHashCode(
                factory
            ) + "]";
            m_logger.debug( message );
        }
        WicketServlet servlet = new Servlet( factory );
        return addServlet( mountPoint, servlet, serviceReference );
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        String oldMountPoint = (String) object;
        final PaxWicketApplicationFactory factory = (PaxWicketApplicationFactory) m_bundleContext.getService( serviceReference );
        String newMountPoint = factory.getMountPoint();
        if( oldMountPoint.equals( newMountPoint ) )
        {
            return;
        }
        WicketServlet servlet = m_httpTracker.getServlet( oldMountPoint );
        removedService( serviceReference, object );
        addServlet( newMountPoint, servlet, serviceReference );
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
            m_httpTracker.addServlet( mountPoint, servlet,  appFactory.getBundle() );
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

}
