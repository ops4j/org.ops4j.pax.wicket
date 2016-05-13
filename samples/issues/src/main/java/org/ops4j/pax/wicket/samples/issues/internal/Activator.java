/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.issues.internal;

import java.util.Dictionary;
import java.util.Hashtable;
import org.ops4j.pax.wicket.api.PageMounter;
import org.ops4j.pax.wicket.api.support.DefaultWebApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private DefaultWebApplicationFactory<IssuesWicketApplication> applicationFactory;
    private ServiceRegistration<PageMounter> pageMounter;

    public void start(BundleContext context) throws Exception {
        applicationFactory = new DefaultWebApplicationFactory<IssuesWicketApplication>(context, IssuesWicketApplication.class, "issues", "issues");
        applicationFactory.register();
        
        
        Dictionary<String, String> properties=new Hashtable<String, String>();
        properties.put("pax.wicket.applicationname", "issues");
        pageMounter = context.registerService(PageMounter.class, new IssuePageMounter(), properties);
        

    }

    public void stop(BundleContext context) throws Exception {
        applicationFactory.dispose();
        pageMounter.unregister();
    }

}
