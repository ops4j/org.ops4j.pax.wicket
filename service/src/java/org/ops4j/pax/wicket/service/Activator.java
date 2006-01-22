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
package org.ops4j.pax.wicket.service;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.service.http.HttpService;
import org.ops4j.pax.servicemanager.ServiceManagerImpl;
import org.ops4j.pax.servicemanager.ServiceManager;
import org.ops4j.pax.logging.providers.LogProvider;
import org.ops4j.pax.logging.providers.PaxLoggingProvider;
import org.ops4j.pax.wicket.service.internal.HttpTracker;
import org.ops4j.pax.wicket.service.internal.Servlet;
import org.ops4j.pax.wicket.service.internal.PageTracker;
import org.ops4j.pax.wicket.service.internal.HomePageTracker;
import org.ops4j.pax.wicket.WicketPage;
import org.ops4j.pax.wicket.WicketHomePage;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import javax.servlet.http.HttpServlet;

public class Activator
    implements BundleActivator
{
    private ServiceManager m_serviceManager;
    private ServiceTracker m_HttpTracking;
    private ServiceTracker m_pageTracking;
    private ServiceTracker m_homePageTracking;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        LogProvider provider = new PaxLoggingProvider( bundleContext );
        LogFactory.getFactory().setLogProvider( provider );
        m_serviceManager = new ServiceManagerImpl( bundleContext, 2000L );
        Log logger = LogFactory.getFactory().getInstance( Activator.class );
        logger.debug( "Initializing the servlet." );

        HttpServlet servlet = new Servlet( m_serviceManager );
        HttpTracker tracker = new HttpTracker( bundleContext, servlet, m_serviceManager );
        m_HttpTracking = new ServiceTracker( bundleContext, HttpService.class.getName(), tracker );
        m_HttpTracking.open();

        PageTracker pageTracker = new PageTracker();
        m_pageTracking = new ServiceTracker( bundleContext, WicketPage.class.getName(), pageTracker );
        m_pageTracking.open();

        HomePageTracker homePageTracker = new HomePageTracker();
        m_homePageTracking = new ServiceTracker( bundleContext, WicketHomePage.class.getName(), homePageTracker );
        m_homePageTracking.open();
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        m_homePageTracking.close();
        m_homePageTracking = null;
        m_pageTracking.close();
        m_pageTracking = null;
        m_HttpTracking.close();
        m_HttpTracking = null;
        m_serviceManager.dispose();
        m_serviceManager = null;
    }
}
