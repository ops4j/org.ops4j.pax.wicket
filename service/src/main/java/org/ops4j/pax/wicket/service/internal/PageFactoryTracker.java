/*
 * Copyright 2006 Niclas Hedhman.
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

import java.util.ArrayList;
import java.util.List;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import wicket.IPageFactory;

public class PageFactoryTracker
    extends ServiceTracker
{
    private BundleContext m_bundleContext;
    private List<IPageFactory> m_factories;

    public PageFactoryTracker( BundleContext bundleContext, String applicationName )
    {
        super(
            bundleContext,
            "&( (application=" + applicationName + "), objClass=" + IPageFactory.class.getName() + ")", 
            null
        );
        m_bundleContext = bundleContext;
        m_factories = new ArrayList<IPageFactory>();
    }

    public Object addingService( ServiceReference serviceReference )
    {
        IPageFactory factory = (IPageFactory) m_bundleContext.getService( serviceReference );
        m_factories.add( factory );
        return factory;
    }

    public void modifiedService( ServiceReference serviceReference, Object object )
    {
        // TODO: Need to investigate if anything should happen here...
    }

    public void removedService( ServiceReference serviceReference, Object object )
    {
        IPageFactory factory = (IPageFactory) object;
        m_factories.remove( factory );
    }

    List<IPageFactory> getPageFactories()
    {
        return m_factories;
    }
}
