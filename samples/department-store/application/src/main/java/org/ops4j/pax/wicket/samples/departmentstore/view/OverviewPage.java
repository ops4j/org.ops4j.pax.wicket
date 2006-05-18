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
package org.ops4j.pax.wicket.samples.departmentstore.view;

import java.util.ArrayList;
import java.util.List;
import org.ops4j.pax.wicket.service.ContentContainer;
import wicket.Component;
import wicket.extensions.ajax.markup.html.tabs.AjaxTabbedPanel;
import wicket.extensions.markup.html.tabs.AbstractTab;
import wicket.markup.html.WebPage;
import wicket.markup.html.basic.Label;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

public class OverviewPage extends WebPage
{

    public static final int FLOOR_PAGE_SIZE = 10;
    public static final String WICKET_ID_LABEL = "storeName";

    public OverviewPage( ContentContainer container, String storeName )
    {
        Label label = new Label( WICKET_ID_LABEL, storeName );
        add( label );
        final List<Component> floors = container.createComponents( "floor" );
        List tabs = new ArrayList();
        for( final Component floor : floors )
        {
            tabs.add( new AbstractTab( new Model() )
            {
                public Panel getPanel( String panelId )
                {
                    System.out.println( "AbstractTab.getPanel(" + panelId + ");" );
                    Panel panel = new Panel( panelId );
                    panel.add( floor );
                    return panel;
                }
            }
            );
        }
        if( tabs.isEmpty() )
        {
            add( new Label( "floors", "No Floors installed yet." ) );
        }
        else
        {
            add( new AjaxTabbedPanel( "floors", tabs ) );
        }

    }
}
