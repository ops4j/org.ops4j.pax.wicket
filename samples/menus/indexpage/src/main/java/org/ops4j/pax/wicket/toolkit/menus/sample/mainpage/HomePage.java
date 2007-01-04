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
package org.ops4j.pax.wicket.toolkit.menus.sample.mainpage;

import java.util.List;
import java.util.ArrayList;
import org.ops4j.pax.wicket.service.DefaultPageContainer;
import org.ops4j.pax.wicket.toolkit.menus.PaxWicketMenu;
import wicket.Component;
import wicket.authorization.strategies.role.annotations.AuthorizeInstantiation;
import wicket.markup.html.WebPage;
import wicket.markup.html.list.ListView;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.basic.Label;

@AuthorizeInstantiation( "user" )
public class HomePage extends WebPage
{

    private DefaultPageContainer m_pageContainer;


    /**
     * @see wicket.Page#Page(wicket.model.IModel) @param container The Page Container to use to create the components.
     * @param container
     * @param globalMenu
     * @param localMenu
     */
    public HomePage( DefaultPageContainer container, PaxWicketMenu globalMenu, PaxWicketMenu localMenu )
    {
        super();
        m_pageContainer = container;
        populateMenus( globalMenu, "globalmenu" );
        populateMenus( localMenu, "localmenu" );
    }

    private void populateMenus( PaxWicketMenu menu, String wicketId )
    {
        List<Component> globalMenuComponents  = new ArrayList<Component>();
        ListView globalmenuview = new ListView( wicketId, globalMenuComponents )
        {
            protected void populateItem( final ListItem item )
            {

            }
        };
        globalMenuComponents.addAll( menu.createComponents( "menu", globalmenuview ) );
        if( globalMenuComponents.size() == 0 )
        {
            // No menu installed.
            Label dummy = new Label( wicketId, "" );
            dummy.setVisible( false );
            add( dummy );
        }
        else
        {

        }
    }
}
