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

import javax.servlet.ServletException;
import org.ops4j.pax.wicket.service.PaxWicketApplication;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public class WicketApplicationTracker
    implements ServiceTrackerCustomizer
{

    private BundleContext m_bundleContext;
    private HttpTracker m_httpTracker;

    public WicketApplicationTracker( BundleContext bundleContext, HttpTracker httpTracker )
    {
        m_bundleContext = bundleContext;
        m_httpTracker = httpTracker;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        final PaxWicketApplication app = (PaxWicketApplication) m_bundleContext.getService( serviceReference );
        Servlet servlet = new Servlet( new IWebApplicationFactory()
        {
            public WebApplication createApplication( WicketServlet servlet )
            {
                return app;
            }
        }
        );
        String mountPoint = getMountPoint( serviceReference, app );
        return addServlet( mountPoint, servlet, app );
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        String oldMountPoint = (String) object;
        String newMountPoint = (String) serviceReference.getProperty( PaxWicketApplication.MOUNTPOINT );
        if( oldMountPoint.equals( newMountPoint ) )
        {
            return;
        }
        Servlet servlet = m_httpTracker.getServlet( oldMountPoint );
        removedService( serviceReference, object );
        final PaxWicketApplication app = (PaxWicketApplication) m_bundleContext.getService( serviceReference );
        String mountPoint = getMountPoint( serviceReference, app );
        addServlet( mountPoint, servlet, app );
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        String mountPoint = (String) object;
        m_httpTracker.removeServlet( mountPoint );
    }

    private Object addServlet( String mountPoint, Servlet servlet, PaxWicketApplication app )
    {
        try
        {
            m_httpTracker.addServlet( mountPoint, servlet );
            return mountPoint;
        } catch( NamespaceException e )
        {
            throw new IllegalArgumentException( "Unable to mount [" + app + "] on mount point '" + mountPoint + "'." );
        } catch( ServletException e )
        {
            String message = "Wicket Servlet for [" + app + "] is unable to initialize. "
                             + "This servlet was tried to be mounted on '" + mountPoint + "'.";
            throw new IllegalArgumentException( message, e );
        }
    }

    private String getMountPoint( ServiceReference serviceReference, PaxWicketApplication app )
    {
        String mountPoint = (String) serviceReference.getProperty( PaxWicketApplication.MOUNTPOINT );
        if( mountPoint == null || mountPoint.length() == 0 )
        {
            String mp = PaxWicketApplication.MOUNTPOINT;
            String message = "PaxWicketApplication [" + app + "] MUST have a '" + mp + "' configuration property.";
            throw new IllegalArgumentException( message );
        }
        return mountPoint;
    }

}
