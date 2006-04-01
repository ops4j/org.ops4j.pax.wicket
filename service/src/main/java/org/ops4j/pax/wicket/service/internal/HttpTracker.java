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

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.http.HttpContext;
import org.osgi.service.http.HttpService;
import org.osgi.service.http.NamespaceException;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.ops4j.pax.servicemanager.ServiceManager;

public class HttpTracker
    implements ServiceTrackerCustomizer
{
    private BundleContext m_BundleContext;
    private HttpService m_Service;
    private HttpContext m_HttpContext;
    private Servlet m_Servlet;
    private ServiceManager m_serviceManager;
    private String m_rootUrl;

    public HttpTracker( BundleContext bundleContext, Servlet servlet, ServiceManager serviceManager )
    {
        m_serviceManager = serviceManager;
        Log logger = LogFactory.getLog( HttpTracker.class );
        logger.debug( "HttpTracker( " + bundleContext + ", " + servlet + " )" );
        m_BundleContext = bundleContext;
        m_Servlet = servlet;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        m_Service = (HttpService) m_BundleContext.getService( serviceReference );
        try
        {
            registerAll();
        }
        catch( NamespaceException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
        catch( ServletException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
        return m_Service;
    }

    public void modifiedService( ServiceReference serviceReference, Object value )
    {
        unregisterAll();
        try
        {
            registerAll();
        }
        catch( NamespaceException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
        catch( ServletException e )
        {
            e.printStackTrace();  //TODO: Auto-generated, need attention.
        }
    }

    public void removedService( ServiceReference serviceReference, Object value )
    {
        unregisterAll();
    }

    private void registerAll()
        throws NamespaceException, ServletException
    {
        m_HttpContext = new GenericContext( m_BundleContext, m_rootUrl, m_serviceManager );
        m_Service.registerServlet( m_rootUrl + "/app", m_Servlet, null, m_HttpContext );
        m_Service.registerResources( m_rootUrl , m_rootUrl, m_HttpContext );

    }

    private void unregisterAll()
    {
        m_Service.unregister( m_rootUrl + "/app" );
        m_Service.unregister( m_rootUrl );
    }
}
