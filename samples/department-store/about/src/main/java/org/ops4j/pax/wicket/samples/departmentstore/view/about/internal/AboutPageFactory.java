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
package org.ops4j.pax.wicket.samples.departmentstore.view.about.internal;

import org.apache.wicket.PageParameters;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStoreModelTracker;
import org.osgi.framework.BundleContext;

public class AboutPageFactory
    implements PageFactory<AboutPage>
{

    private final DepartmentStoreModelTracker tracker;
    private final BundleContext context;

    public AboutPageFactory( DepartmentStoreModelTracker aTracker, BundleContext aContext )
    {
        tracker = aTracker;
        context = aContext;
    }

    public Class<AboutPage> getPageClass()
    {
        return AboutPage.class;
    }

    public AboutPage createPage( PageParameters params )
    {
        DepartmentStore store = tracker.getDepartmentStore();
        return new AboutPage( store, context );
    }
}
