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

import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStoreModelTracker;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.api.PageFactory;

import org.apache.wicket.PageParameters;

public class AboutPageFactory
    implements PageFactory<AboutPage>
{
    private DepartmentStoreModelTracker m_tracker;

    public AboutPageFactory( DepartmentStoreModelTracker tracker )
    {
        m_tracker = tracker;
    }

    public Class<AboutPage> getPageClass()
    {
        return AboutPage.class;
    }

    public AboutPage createPage( PageParameters params )
    {
        DepartmentStore store = m_tracker.getDepartmentStore();
        return new AboutPage( store );
    }
}
