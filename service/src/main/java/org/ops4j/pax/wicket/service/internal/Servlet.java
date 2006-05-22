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

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WicketServlet;

public final class Servlet extends WicketServlet
{
    private static final Log m_logger = LogFactory.getLog( Servlet.class );

    private IWebApplicationFactory m_appFactory;
    private ClassLoader m_classloader;
    private Class<?>[] m_interfaces;

    public Servlet( IWebApplicationFactory appFactory )
    {
        m_appFactory = appFactory;
        m_classloader = getClass().getClassLoader();
        m_interfaces = new Class[] { HttpServletRequest.class };
    }

    protected IWebApplicationFactory getApplicationFactory()
    {
        return m_appFactory;
    }

    public void service( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Servlet.service( " + req + ", " + resp + " )" );
        }
        InvocationHandler handler = new RequestProxyHandler( req );
        HttpServletRequest hackedRequest = (HttpServletRequest) Proxy.newProxyInstance( m_classloader, m_interfaces, handler );
        super.service( hackedRequest, resp );
    }

    private static class RequestProxyHandler
        implements InvocationHandler
    {

        private HttpServletRequest m_original;

        private RequestProxyHandler( HttpServletRequest original )
        {
            m_original = original;
        }

        public Object invoke( Object proxy, Method method, Object[] args )
            throws Throwable
        {
            if( "getServletPath".equals( method.getName() ) )
            {
                return m_original.getContextPath();
            }
            else if( "getContextPath".equals( method.getName() ) )
            {
                return m_original.getServletPath();
            }
            else
            {
                return method.invoke( m_original, args );
            }
        }
    }
}
