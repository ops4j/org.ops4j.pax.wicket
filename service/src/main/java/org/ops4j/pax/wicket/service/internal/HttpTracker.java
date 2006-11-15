/*
 * Copyright 2005 Niclas Hedhman.
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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;
import wicket.protocol.http.WicketServlet;

public class HttpTracker extends ServiceTracker
{

    private BundleContext m_bundleContext;
    private HttpService m_httpService;
    private HashMap<String, ServletDescriptor> m_servlets;

    public HttpTracker( BundleContext bundleContext )
    {
        super( bundleContext, HttpService.class.getName(), null );
        m_bundleContext = bundleContext;
        m_servlets = new HashMap<String, ServletDescriptor>();
    }

    public Object addingService( ServiceReference serviceReference )
    {
        m_httpService = (HttpService) m_bundleContext.getService( serviceReference );
        for( Map.Entry<String, ServletDescriptor> entry : m_servlets.entrySet() )
        {
            ServletDescriptor descriptor = entry.getValue();
            WicketServlet servlet = descriptor.m_servlet;
            HttpContext context = descriptor.m_httpContext;
            String mountpoint = entry.getKey();
            try
            {
                m_httpService.registerServlet( mountpoint, servlet, null, context );
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
        Set<String> mountPoints;
        synchronized( this )
        {
            mountPoints = new HashSet<String>( m_servlets.keySet() );   
        }
        
        for( String mountpoint : mountPoints )
        {
            m_httpService.unregister( mountpoint );
        }
    }

    void addServlet( String mountPoint, WicketServlet servlet, Bundle paxWicketBundle )
        throws NamespaceException, ServletException
    {
        mountPoint = normalizeMountPoint( mountPoint );
        HttpContext httpContext = new GenericContext( paxWicketBundle, mountPoint );
        ServletDescriptor descriptor = new ServletDescriptor( servlet, httpContext );
        m_servlets.put( mountPoint, descriptor );
        if( m_httpService != null )
        {
            m_httpService.registerServlet( mountPoint, servlet, null, httpContext );
        }
    }

    void removeServlet( String mountPoint )
    {
        mountPoint = normalizeMountPoint( mountPoint );
        if( m_servlets.remove( mountPoint ) != null )
        {
            if( m_httpService != null )
            {
                m_httpService.unregister( mountPoint );
            }
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

    WicketServlet getServlet( String mountPoint )
    {
        mountPoint = normalizeMountPoint( mountPoint );
        ServletDescriptor descriptor = m_servlets.get( mountPoint );
        return descriptor.m_servlet;
    }

    private static class ServletDescriptor
    {
        private WicketServlet m_servlet;
        private HttpContext m_httpContext;

        public ServletDescriptor( WicketServlet servlet, HttpContext httpContext )
        {
            m_servlet = servlet;
            m_httpContext = httpContext;
        }
    }
}
