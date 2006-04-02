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

import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.BundleContext;
import org.ops4j.pax.wicket.WicketHomePage;

public class HomePageTracker
    implements ServiceTrackerCustomizer
{
    private WicketHomePage m_HomePage;
    private BundleContext m_bundleContext;

    public HomePageTracker( BundleContext bundleContext )
    {
        m_bundleContext = bundleContext;
    }

    public Object addingService( ServiceReference serviceReference )
    {
        Object service = m_bundleContext.getService( serviceReference );
        if( service instanceof WicketHomePage )
        {
            m_HomePage = (WicketHomePage) service;
        }
        return m_HomePage;
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        Object service = m_bundleContext.getService( serviceReference );
        if( service instanceof WicketHomePage )
        {
            m_HomePage = (WicketHomePage) service;
        }
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        Object service = m_bundleContext.getService( serviceReference );
        if( service instanceof WicketHomePage )
        {
            m_HomePage = null;
        }
    }

    public WicketHomePage getHomePage()
    {
        return m_HomePage;
    }
}
