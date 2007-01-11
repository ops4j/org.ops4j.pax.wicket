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

import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.ops4j.pax.wicket.api.PageController;
import org.ops4j.pax.wicket.util.PageFinder;
import org.ops4j.pax.wicket.util.AbstractPageController;
import org.osgi.framework.BundleContext;
import wicket.Page;
import wicket.PageParameters;

public class OverviewPageController extends AbstractPageController<OverviewPage>
    implements PageController<OverviewPage>
{
    private BundleContext m_context;
    private RootContentAggregator m_aggregator;

    public OverviewPageController( BundleContext context, RootContentAggregator aggregator, String applicationName, String pageName )
    {
        super(context, "overview", applicationName, pageName );
        m_context = context;
        m_aggregator = aggregator;
    }

    public Class<OverviewPage> getPageClass()
    {
        return OverviewPage.class;
    }

    public OverviewPage createPage( PageParameters params )
    {
        PageController<Page>[] pageSources = PageFinder.findPages( m_context, "departmentstore", "about" );
        Class pageClass;
        if( pageSources.length == 0 )
        {
            pageClass = null;
        }
        else
        {
            pageClass = pageSources[ 0 ].getPageClass();
        }
        OverviewPage overviewPage = new OverviewPage( m_aggregator, "Sungei Wang Plaza", pageClass );
        return overviewPage;
    }
}
