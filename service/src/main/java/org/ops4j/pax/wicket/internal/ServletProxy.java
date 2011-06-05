/* 
 * Copyright 2008 Edward Yakop.
 * Copyright 2010 David Leangen.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal;

import static java.lang.reflect.Proxy.isProxyClass;
import static java.lang.reflect.Proxy.newProxyInstance;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;

/**
 * @author edward.yakop@gmail.com
 */
public class ServletProxy
{

    private static final Class<?>[] SERVLET_INTERFACES = new Class[]{
        Servlet.class
    };
    
    static Servlet newServletProxy(
        IWebApplicationFactory applicationFactory, File tempDir, String mountPoint, FilterDelegator filterDelegator )
    {
        ServletInvocationHandler ih = new ServletInvocationHandler( applicationFactory, tempDir, mountPoint, filterDelegator );
        return newServletProxy( ih );
    }
    
    static Servlet newServletProxy( PaxWicketApplicationFactory applicationFactory, File tempDir, String mountPoint )
    {
        ServletInvocationHandler ih = new ServletInvocationHandler( applicationFactory, tempDir, mountPoint );
        return newServletProxy( ih );
    }
    
    private static Servlet newServletProxy( ServletInvocationHandler ih )
    {
        ClassLoader classLoader = ServletProxy.class.getClassLoader();
        return (Servlet) newProxyInstance( classLoader, SERVLET_INTERFACES, ih );
    }

    private static class ServletInvocationHandler
        implements InvocationHandler
    {

        private static final Class<?>[] REQUEST_INTERFACES = new Class[]{
            HttpServletRequest.class
        };

        private final String m_mountPoint;
        private final ServletDelegator m_delegator;
        private FilterDelegator m_filterDelegator;

        public ServletInvocationHandler(
            IWebApplicationFactory applicationFactory, File tempDir, String mountPoint, FilterDelegator filterDelegator
        )
        {
            m_mountPoint = mountPoint;
            m_delegator = new ServletDelegator( applicationFactory, tempDir );
            m_filterDelegator = filterDelegator;
            m_filterDelegator.setServlet(m_delegator);
        }
        
        public ServletInvocationHandler(
            IWebApplicationFactory applicationFactory, File tempDir, String mountPoint )
        {
            m_mountPoint = mountPoint;
            m_delegator = new ServletDelegator( applicationFactory, tempDir );
        }

        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            replaceHttpRequestArgument( args );
            if ( m_filterDelegator != null)
            {
                if ( method.getName().equals( "service" ) )
                {
                    m_filterDelegator.doFilter( (HttpServletRequest) args[0], (HttpServletResponse) args[1] );
                    return null;
                }
            }
            return method.invoke( m_delegator, args );
        }

        private void replaceHttpRequestArgument( Object[] args )
        {
            if( args == null || args.length == 0 )
            {
                return;
            }

            for( int i = 0; i < args.length; i++ )
            {
                Object arg = args[ i ];
                if( arg == null )
                {
                    continue;
                }

                Class<?> argumentClass = arg.getClass();
                if( HttpServletRequest.class.isAssignableFrom( argumentClass ) &&
                    !isProxyClass( argumentClass ) )
                {
                    HttpServletRequest request = (HttpServletRequest) arg;
                    // Replace the request
                    args[ i ] = newProxyRequest( request );
                }
            }
        }

        private HttpServletRequest newProxyRequest( HttpServletRequest request )
        {
            ClassLoader loader = ServletProxy.class.getClassLoader();
            ServletRequestInvocationHandler ih = new ServletRequestInvocationHandler( request, m_mountPoint );
            return (HttpServletRequest) newProxyInstance( loader, REQUEST_INTERFACES, ih );
        }

        private static final class ServletDelegator extends WicketServlet
        {

            private static final long serialVersionUID = 1L;

            private static final String WICKET_REQUIRED_ATTRIBUTE = "javax.servlet.context.tempdir";

            private final IWebApplicationFactory m_appFactory;
            private final File m_tmpDir;

            ServletDelegator( IWebApplicationFactory applicationFactory, File tempDir )
                throws IllegalArgumentException
            {
                validateNotNull( applicationFactory, "applicationFactory" );
                validateNotNull( tempDir, "tempDir" );

                m_appFactory = applicationFactory;
                m_tmpDir = tempDir;
                m_tmpDir.mkdirs();
            }

            @Override
            protected WicketFilter newWicketFilter()
            {
                ServletContext servletContext = getServletContext();
                if( servletContext.getAttribute( WICKET_REQUIRED_ATTRIBUTE ) == null )
                {
                    servletContext.setAttribute( WICKET_REQUIRED_ATTRIBUTE, m_tmpDir );
                }

                return new PaxWicketFilter( m_appFactory );
            }

            @Override
            public String toString()
            {
                return "Pax Wicket Servlet";
            }
        }

        private static class ServletRequestInvocationHandler
            implements InvocationHandler
        {

            private final HttpServletRequest m_request;
            private final String m_mountPoint;

            private ServletRequestInvocationHandler( HttpServletRequest request, String mountPoint )
                throws IllegalArgumentException
            {
                validateNotNull( request, "request" );
                validateNotNull( mountPoint, "mountPoint" );

                m_request = request;

                if( mountPoint.length() <= 1 )
                {
                    if( mountPoint.startsWith( "/" ) )
                    {
                        mountPoint = mountPoint.substring( 1 );
                    }
                }
                else
                {
                    if( !mountPoint.startsWith( "/" ) )
                    {
                        mountPoint = "/" + mountPoint;
                    }
                }
                m_mountPoint = mountPoint;
            }

            public Object invoke( Object proxy, Method method, Object[] arguments )
                throws Throwable
            {
                String methodName = method.getName();

                Object returnValue;
                if( m_mountPoint.length() == 0 )
                {
                    if( "getContextPath".equals( methodName ) ||
                        "getServletPath".equals( methodName ) )
                    {
                        returnValue = "";
                    }
                    else if( "getPathInfo".equals( methodName ) )
                    {
                        returnValue = m_request.getServletPath();
                    }
                    else
                    {
                        returnValue = method.invoke( m_request, arguments );
                    }
                }
                else
                {
                    returnValue = method.invoke( m_request, arguments );
                }

                return returnValue;
            }
        }
    }

}
