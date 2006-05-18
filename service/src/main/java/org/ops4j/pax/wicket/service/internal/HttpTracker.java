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
import java.util.Map;
import javax.servlet.ServletException;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

public class HttpTracker
    implements ServiceTrackerCustomizer
{


    private BundleContext m_bundleContext;
    private HttpService m_httpService;
    private Map<String, Servlet> m_servlets;

    public HttpTracker( BundleContext bundleContext )
    {
        m_bundleContext = bundleContext;
        m_servlets = new HashMap<String, Servlet>();
    }

    public Object addingService( ServiceReference serviceReference )
    {
        m_httpService = (HttpService) m_bundleContext.getService( serviceReference );
        for( Map.Entry<String, Servlet> entry : m_servlets.entrySet() )
        {
            Servlet servlet = entry.getValue();
            String mountpoint = entry.getKey();
            try
            {
                m_httpService.registerServlet( mountpoint, servlet, null, null );
            } catch( NamespaceException e )
            {
                throw new IllegalArgumentException(
                    "Unable to mount [" + servlet + "] on mount point '" + mountpoint + "'."
                );
            } catch( ServletException e )
            {
                String message = "Wicket Servlet [" + servlet + "] is unable to initialize. "
                                 + "This servlet was tried to be mounted on '" + mountpoint + "'.";
                throw new IllegalArgumentException( message, e );
            }
        }
        return m_httpService;
    }

    public void modifiedService( ServiceReference serviceReference, Object httpService )
    {
    }

    public void removedService( ServiceReference serviceReference, Object httpService )
    {
        for( String mountpoint : m_servlets.keySet() )
        {
            m_httpService.unregister( mountpoint );
        }
    }

    void addServlet( String mountPoint, Servlet servlet )
        throws NamespaceException, ServletException
    {
        mountPoint = normalizeMountPoint( mountPoint );
        m_httpService.registerServlet( mountPoint, servlet, null, null );
        m_servlets.put( mountPoint, servlet );
    }

    void removeServlet( String mountPoint )
    {
        mountPoint = normalizeMountPoint( mountPoint );
        if( m_servlets.remove( mountPoint ) != null )
        {
            m_httpService.unregister( mountPoint );
        }
    }

    private String normalizeMountPoint( String mountPoint )
    {
        if( !mountPoint.startsWith( "/" ) )
        {
            mountPoint = "/" + mountPoint;
        }
        return mountPoint;
    }

    Servlet getServlet( String mountPoint )
    {
        mountPoint = normalizeMountPoint( mountPoint );
        return m_servlets.get( mountPoint );
    }
}
