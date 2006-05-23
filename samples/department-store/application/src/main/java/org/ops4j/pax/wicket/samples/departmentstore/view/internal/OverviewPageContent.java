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
package org.ops4j.pax.wicket.samples.departmentstore.view.internal;

import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewPage;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import org.ops4j.pax.wicket.service.PageContent;
import org.ops4j.pax.wicket.service.PageFinder;
import org.ops4j.pax.wicket.service.AbstractPageContent;
import org.osgi.framework.BundleContext;
import wicket.Page;
import wicket.PageParameters;

public class OverviewPageContent extends AbstractPageContent
    implements PageContent
{
    private BundleContext m_context;
    private DefaultPageContainer m_pageContainer;

    public OverviewPageContent( BundleContext context, DefaultPageContainer  pageContainer, String applicationName, String pageName )
    {
        super(context, applicationName, pageName );
        m_context = context;
        m_pageContainer = pageContainer;
    }

    public Class getPageClass()
    {
        return OverviewPage.class;
    }

    public Page createPage( PageParameters params )
    {
        PageContent[] pages = PageFinder.findPages( m_context, "departmentstore", "about" );
        Class pageClass;
        if( pages.length == 0 )
        {
            pageClass = null;
        }
        else
        {
            pageClass = pages[ 0 ].getPageClass();
        }
        OverviewPage overviewPage = new OverviewPage( m_pageContainer, "Sungei Wang Plaza", pageClass );
        return overviewPage;
    }
}
