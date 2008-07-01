/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
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

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import org.apache.wicket.Component;
import org.apache.wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import org.apache.wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewTabContent;
import org.ops4j.pax.wicket.util.RootContentAggregator;

@AuthorizeInstantiation( "user" )
final class OverviewPage extends WebPage
{

    private static final long serialVersionUID = 1L;

    private static final String WICKET_ID_LABEL = "storeName";

    @SuppressWarnings( "unchecked" )
    OverviewPage( RootContentAggregator container, String storeName, Class aboutPageClass )
    {
        Label label = new Label( WICKET_ID_LABEL, storeName );
        add( label );
        Component link;
        if( aboutPageClass == null )
        {
            link = new Label( "aboutlink", "" );
        }
        else
        {
            link = new BookmarkablePageLink( "aboutlink", aboutPageClass );
        }
        add( link );

        Locale locale = getLocale();
        List<ContentSource<Component>> contents = container.getContents( "floor" );
        int numberOfContents = contents.size();
        List<AbstractTab> tabs = new ArrayList<AbstractTab>( numberOfContents );
        for( ContentSource content : contents )
        {
            if( content instanceof OverviewTabContent )
            {
                OverviewTabContent otc = (OverviewTabContent) content;
                AbstractTab tab = otc.createTab( locale );
                tabs.add( tab );
            }
        }

        if( tabs.isEmpty() )
        {
            Label niceMsg = new Label( "floors", "No Floors installed yet." );
            add( niceMsg );
        }
        else
        {
            AjaxTabbedPanel tabbedPanel = new AjaxTabbedPanel( "floors", tabs );
            add( tabbedPanel );
        }
    }
}
