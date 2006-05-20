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
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import java.util.List;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.service.ContentContainer;
import wicket.Component;
import wicket.model.Model;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;

/**
 * {@code FloorPanel}
 *
 * @author Edward Yakop
 * @since 1.0.0
 */
public class FloorPanel extends Panel
{

    public static final String WICKET_ID_NAME_LABEL = "name";
    private static final String WICKET_ID_FRANCHISEE = "franchisee";
    private static final String WICKET_ID_FRANCHISEES = "franchisees";

    public FloorPanel( String id, ContentContainer container, Floor floor )
    {
        super( id, new Model( floor.getName() ) );
        Label nameLabel = new Label( WICKET_ID_NAME_LABEL, floor.getName() );
        add( nameLabel );
        final List<Component> franchisees = container.createComponents( WICKET_ID_FRANCHISEE );
        if( franchisees.isEmpty() )
        {
            Panel p = new Panel( "franchisees" );
            p.add( new Label( "franchisee", "No Franchisees on this floor." ) );
            add( p );
        }
        else
        {
            ListView listView = new ListView( WICKET_ID_FRANCHISEES, franchisees )
            {
                protected void populateItem( final ListItem item )
                {
                    item.add( (Component) item.getModelObject() );
                }
            };
            add( listView );
        }
    }

}
