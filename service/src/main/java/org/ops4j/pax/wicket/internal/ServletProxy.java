/*  Copyright 2008 Edward Yakop.
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

import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import static java.lang.reflect.Proxy.isProxyClass;
import static java.lang.reflect.Proxy.newProxyInstance;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

/**
 * @author edward.yakop@gmail.com
 */
public class ServletProxy
{

    private static final Class[] SERVLET_INTERFACES = new Class[]{
        Servlet.class
    };

    static Servlet newServletProxy(
        IWebApplicationFactory anApplicationFactory, File aTempDir, final String aMountPoint )
    {
        ClassLoader classLoader = ServletProxy.class.getClassLoader();
        ServletInvocationHandler ih = new ServletInvocationHandler( anApplicationFactory, aTempDir, aMountPoint );
        return (Servlet) newProxyInstance( classLoader, SERVLET_INTERFACES, ih );
    }

    private static class ServletInvocationHandler
        implements InvocationHandler
    {

        private static final Class[] REQUEST_INTERFACES = new Class[]{
            HttpServletRequest.class
        };

        private final String mountPoint;
        private final ServletDelegator delegator;

        public ServletInvocationHandler(
            IWebApplicationFactory anApplicationFactory, File aTempDir, String aMountPoint
        )
        {
            mountPoint = aMountPoint;
            delegator = new ServletDelegator( anApplicationFactory, aTempDir );
        }

        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            replaceHttpRequestArgument( args );
            return method.invoke( delegator, args );
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

        private HttpServletRequest newProxyRequest( final HttpServletRequest aRequest )
        {
            ClassLoader loader = ServletProxy.class.getClassLoader();
            ServletRequestInvocationHandler ih = new ServletRequestInvocationHandler( aRequest, mountPoint );
            return (HttpServletRequest) newProxyInstance( loader, REQUEST_INTERFACES, ih );
        }

        private static final class ServletDelegator extends WicketServlet
        {

            private static final long serialVersionUID = 1L;

            private static final String WICKET_REQUIRED_ATTRIBUTE = "javax.servlet.context.tempdir";

            private final IWebApplicationFactory appFactory;
            private final File tmpDir;

            ServletDelegator( IWebApplicationFactory anApplicationFactory, File aTempDir )
                throws IllegalArgumentException
            {
                validateNotNull( anApplicationFactory, "anApplicationFactory" );
                validateNotNull( aTempDir, "aTempDir" );

                appFactory = anApplicationFactory;
                tmpDir = aTempDir;
                tmpDir.mkdirs();
            }

            @Override
            protected WicketFilter newWicketFilter()
            {
                ServletContext servletContext = getServletContext();
                if( servletContext.getAttribute( WICKET_REQUIRED_ATTRIBUTE ) == null )
                {
                    servletContext.setAttribute( WICKET_REQUIRED_ATTRIBUTE, tmpDir );
                }

                return new PaxWicketFilter( appFactory );
            }

            @Override
            public String toString()
            {
                return "Pax Wicket Servlet";
            }
        }

        private static class ServletRequestInvocationHandler implements InvocationHandler
        {

            private final HttpServletRequest request;
            private final String mountPoint;

            private ServletRequestInvocationHandler(
                HttpServletRequest aRequest, String aMountPoint )
                throws IllegalArgumentException
            {
                validateNotNull( aRequest, "aRequest" );
                validateNotNull( aMountPoint, "aMountPoint" );

                request = aRequest;

                if( aMountPoint.length() <= 1 )
                {
                    if( aMountPoint.startsWith( "/" ) )
                    {
                        aMountPoint = aMountPoint.substring( 1 );
                    }
                }
                else
                {
                    if( !aMountPoint.startsWith( "/" ) )
                    {
                        aMountPoint = "/" + aMountPoint;
                    }
                }
                mountPoint = aMountPoint;
            }

            public Object invoke( Object aProxy, Method aMethod, Object[] arguments )
                throws Throwable
            {
                String methodName = aMethod.getName();

                Object returnValue;
                if( mountPoint.length() == 0 )
                {
                    if( "getContextPath".equals( methodName ) ||
                        "getServletPath".equals( methodName ) )
                    {
                        returnValue = "";
                    }
                    else if( "getPathInfo".equals( methodName ) )
                    {
                        returnValue = request.getServletPath();
                    }
                    else
                    {
                        returnValue = aMethod.invoke( request, arguments );
                    }
                }
                else
                {
                    returnValue = aMethod.invoke( request, arguments );
                }

                return returnValue;
            }
        }
    }
}
