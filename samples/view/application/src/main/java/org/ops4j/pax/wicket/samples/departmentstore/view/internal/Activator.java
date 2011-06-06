/*
 * Copyright OPS4J
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

import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private RootContentAggregator store;
    private ServiceRegistration serviceRegistration;
    private PaxWicketApplicationFactory applicationFactory;
    private OverviewPageFactory overviewPageFactory;

    public void start(BundleContext bundleContext) throws Exception {
        // to mount in root context use "/" instead. Please keep in mind that if you change this value you also have to
        // update the stylesheet link in the OverviewPage.html. E.g. if you change the mount point here to / you have to
        // change deptStore/stylesheets/style.css in OverviewPage.html to stylesheets/style.css
        String mountPoint = "deptStore";
        String applicationName = "departmentstore";
        store = new RootContentAggregator(bundleContext, applicationName, "swp");
        store.register();

        overviewPageFactory = new OverviewPageFactory(bundleContext, store, applicationName, "overview");
        overviewPageFactory.register();

        // Creating a Wicket Application you've two options:
        // a) etiher create the WicketApplicationFactory yourself or...
        // createPaxWicketApplicationFactoryUsingOwnWicketApplication(bundleContext, mountPoint, applicationName);
        // b) ... let pax-wicket do all the work.
        createPaxWicketApplicationFactoryPaxWicketDoingTheWork(bundleContext, mountPoint, applicationName);

        // This registers the pax-wicket service as OSGi Service.
        serviceRegistration = applicationFactory.register();
    }

    private void createPaxWicketApplicationFactoryPaxWicketDoingTheWork(BundleContext bundleContext, String mountPoint,
            String applicationName) {
        applicationFactory =
            new PaxWicketApplicationFactory(bundleContext, OverviewPage.class, mountPoint, applicationName);
    }

    @SuppressWarnings("unused")
    private void createPaxWicketApplicationFactoryUsingOwnWicketApplication(BundleContext bundleContext,
            String mountPoint, String applicationName) {
        applicationFactory =
            new PaxWicketApplicationFactory(bundleContext, OverviewPage.class, mountPoint, applicationName,
                new DeptStoreApplicationFactory());
    }

    public void stop(BundleContext bundleContext) throws Exception {
        serviceRegistration.unregister();
        overviewPageFactory.dispose();
        store.dispose();
        applicationFactory.dispose();
    }

}
