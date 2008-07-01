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
package org.ops4j.pax.wicket.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTracker;

final class HttpTracker extends ServiceTracker
{

    private HttpService m_httpService;
    private final HashMap<String, ServletDescriptor> m_servlets;

    HttpTracker( BundleContext context )
    {
        super( context, HttpService.class.getName(), null );
        m_servlets = new HashMap<String, ServletDescriptor>();
    }

    @Override
    public final Object addingService( ServiceReference serviceReference )
    {
        m_httpService = (HttpService) super.addingService( serviceReference );
        for( Map.Entry<String, ServletDescriptor> entry : m_servlets.entrySet() )
        {
            ServletDescriptor descriptor = entry.getValue();
            Servlet servlet = descriptor.servlet;
            HttpContext context = descriptor.httpContext;
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

    @Override
    public final void removedService( ServiceReference serviceReference, Object httpService )
    {
        Set<String> mountPoints;
        synchronized( this )
        {
            mountPoints = new HashSet<String>( m_servlets.keySet() );
        }

        for( String mountpoint : mountPoints )
        {
            this.m_httpService.unregister( mountpoint );
        }

        super.removedService( serviceReference, httpService );
    }

    final void addServlet( String mountPoint, Servlet servlet, Bundle paxWicketBundle )
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

    final void removeServlet( String mountPoint )
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

    final Servlet getServlet( String mountPoint )
    {
        mountPoint = normalizeMountPoint( mountPoint );
        ServletDescriptor descriptor = m_servlets.get( mountPoint );
        return descriptor.servlet;
    }

    private static final class ServletDescriptor
    {

        private Servlet servlet;
        private HttpContext httpContext;

        public ServletDescriptor( Servlet aServlet, HttpContext aContext )
        {
            servlet = aServlet;
            httpContext = aContext;
        }
    }
}
