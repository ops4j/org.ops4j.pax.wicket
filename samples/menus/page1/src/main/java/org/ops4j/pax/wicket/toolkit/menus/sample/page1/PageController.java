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

import org.ops4j.pax.wicket.util.AbstractPageController;
import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.osgi.framework.BundleContext;
import wicket.PageParameters;

public class PageController extends AbstractPageController
{

    private RootContentAggregator m_aggregator;

    protected PageController( BundleContext bundleContext, RootContentAggregator aggregator, String applicationName, String pageName )
        throws IllegalArgumentException
    {
        super( bundleContext, "FirstPage", applicationName, pageName );
        m_aggregator = aggregator;
    }

    /**
     * Returns the page class instance represented by this {@code PageController}.
     *
     * @return The page class represented by this {@code PageController}.
     *
     * @since 1.0.0
     */
    public Class getPageClass()
    {
        return Page.class;
    }

    /**
     * Creates a page with the specified {@code params}.
     *
     * @param params The page parameters.
     *
     * @return An instance of page.
     *
     * @since 1.0.0
     */
    public wicket.Page createPage( PageParameters params )
    {
        return new Page( m_aggregator );
    }
}
