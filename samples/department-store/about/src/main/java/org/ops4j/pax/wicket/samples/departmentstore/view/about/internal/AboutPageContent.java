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

import org.ops4j.pax.wicket.service.PageContent;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStoreModelTracker;
import wicket.Page;
import wicket.PageParameters;

public class AboutPageContent
    implements PageContent
{
    private DepartmentStoreModelTracker m_tracker;

    public AboutPageContent( DepartmentStoreModelTracker tracker )
    {
        m_tracker = tracker;
    }

    public Class getPageClass()
    {
        return AboutPage.class;
    }

    public Page createPage( PageParameters params )
    {
        return new AboutPage( m_tracker.getDepartmentStore() );
    }
}
