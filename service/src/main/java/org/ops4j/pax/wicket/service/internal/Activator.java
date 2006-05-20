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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;
import org.ops4j.pax.wicket.service.PaxWicketApplicationFactory;

public class Activator
    implements BundleActivator
{
    private ServiceTracker m_httpTracking;
    private ServiceTracker m_wicketTracking;

    public void start( BundleContext bundleContext )
        throws Exception
    {
        LogFactory.setBundleContext( bundleContext );
        Log logger = LogFactory.getFactory().getInstance( Activator.class );
        logger.debug( "Initializing the servlet." );

        HttpTracker httpTracker = new HttpTracker( bundleContext );
        m_httpTracking = new ServiceTracker( bundleContext, HttpService.class.getName(), httpTracker );
        m_httpTracking.open();

        PaxWicketAppFactoryTracker wickTracking = new PaxWicketAppFactoryTracker( bundleContext, httpTracker );
        String serviceName = PaxWicketApplicationFactory.class.getName();
        m_wicketTracking = new ServiceTracker( bundleContext, serviceName, wickTracking );
        m_wicketTracking.open();
    }

    public void stop( BundleContext bundleContext )
        throws Exception
    {
        m_httpTracking.close();
        m_httpTracking = null;
        m_wicketTracking.close();
        m_wicketTracking = null;
    }
}
