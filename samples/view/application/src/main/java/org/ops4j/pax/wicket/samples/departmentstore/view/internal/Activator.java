/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 * Copyright 2010 Andreas Pieber
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
package org.ops4j.pax.wicket.samples.departmentstore.view.internal;

import static org.apache.wicket.util.lang.Objects.setObjectStreamFactory;

import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.ops4j.pax.wicket.util.serialization.PaxWicketObjectStreamFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private RootContentAggregator m_store;
    private ServiceRegistration m_serviceRegistration;
    private ServiceRegistration m_pageRegistration;
    private PaxWicketApplicationFactory m_applicationFactory;
    private OverviewPageFactory m_overviewPageFactory;

    public void start(BundleContext bundleContext) throws Exception {
        // to mount in root context use "/" instead. Please keep in mind that if you change this value you also have to
        // update the stylesheet link in the OverviewPage.html. E.g. if you change the mount point here to / you have to
        // change deptStore/stylesheets/style.css in OverviewPage.html to stylesheets/style.css
        String mountPoint = "deptStore";
        String applicationName = "departmentstore";
        setObjectStreamFactory(new PaxWicketObjectStreamFactory(true));
        m_store = new RootContentAggregator(bundleContext, applicationName, "swp");
        m_pageRegistration = m_store.register();

        m_overviewPageFactory = new OverviewPageFactory(bundleContext, m_store, applicationName, "overview");
        m_overviewPageFactory.register();

        m_applicationFactory =
            new PaxWicketApplicationFactory(bundleContext, OverviewPage.class, mountPoint, applicationName);

        m_serviceRegistration = m_applicationFactory.register();
    }

    public void stop(BundleContext bundleContext) throws Exception {
        m_pageRegistration.unregister();
        m_serviceRegistration.unregister();
        m_overviewPageFactory.dispose();
        m_store.dispose();
        m_applicationFactory.dispose();
    }

}
