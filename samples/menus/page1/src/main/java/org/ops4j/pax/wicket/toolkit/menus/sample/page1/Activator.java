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
package org.ops4j.pax.wicket.toolkit.menus.sample.page1;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.ops4j.pax.wicket.toolkit.menus.sample.application.Application;

public class Activator
    implements BundleActivator
{

    private RootContentAggregator m_aggregator;
    private ServiceRegistration m_pageRegistration;
    private PageFactory m_pageFactory;

    /**
     * Called when this bundle is started so the Framework can perform the
     * bundle-specific activities necessary to start this bundle. This method
     * can be used to register services or to allocate any resources that this
     * bundle needs.
     *
     * <p>
     * This method must complete and return to its caller in a timely manner.
     *
     * @param context The execution context of the bundle being started.
     *
     * @throws Exception If this method throws an exception, this
     *                   bundle is marked as stopped and the Framework will remove this
     *                   bundle's listeners, unregister all services registered by this
     *                   bundle, and release all services used by this bundle.
     * @see org.osgi.framework.Bundle#start
     */
    public void start( BundleContext context )
        throws Exception
    {
        m_aggregator = new RootContentAggregator(context, "page1", Application.NAME );
        m_pageRegistration = m_aggregator.register();

        m_pageFactory = new PageFactory(context, m_aggregator, Application.NAME, "FirstPage" );
        m_pageFactory.register();
    }

    /**
     * Called when this bundle is stopped so the Framework can perform the
     * bundle-specific activities necessary to stop the bundle. In general, this
     * method should undo the work that the <code>BundleActivator.start</code>
     * method started. There should be no active threads that were started by
     * this bundle when this bundle returns. A stopped bundle must not call any
     * Framework objects.
     *
     * <p>
     * This method must complete and return to its caller in a timely manner.
     *
     * @param context The execution context of the bundle being stopped.
     *
     * @throws Exception If this method throws an exception, the
     *                   bundle is still marked as stopped, and the Framework will remove
     *                   the bundle's listeners, unregister all services registered by the
     *                   bundle, and release all services used by the bundle.
     * @see org.osgi.framework.Bundle#stop
     */
    public void stop( BundleContext context )
        throws Exception
    {
        m_pageFactory.dispose();
        m_pageRegistration.unregister();
        m_aggregator.dispose();
    }
}
