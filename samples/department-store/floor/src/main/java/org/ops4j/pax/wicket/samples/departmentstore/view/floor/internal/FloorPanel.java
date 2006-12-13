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

import java.io.Serializable;
import java.util.List;

import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.service.ContentContainer;

import wicket.Component;
import wicket.markup.html.basic.Label;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

/**
 * {@code FloorPanel}
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
final class FloorPanel extends Panel
{

    private static final long serialVersionUID = 1L;

    public static final String WICKET_ID_NAME_LABEL = "name";
    private static final String WICKET_ID_FRANCHISEE = "franchisee";
    private static final String WICKET_ID_FRANCHISEES = "franchisees";

    FloorPanel( String id, ContentContainer container, Floor floor )
    {
        super( id, new Model( floor.getName() ) );

        ListView listView = new ListView( WICKET_ID_FRANCHISEES )
        {
            private static final long serialVersionUID = 1L;

            protected void populateItem( final ListItem item )
            {
                Component modelObject = (Component) item.getModelObject();
                item.add( modelObject );
            }
        };

        List<Component> franchisees = container.createComponents( WICKET_ID_FRANCHISEE, listView );
        if ( franchisees.isEmpty() )
        {
            Label tLabel = new Label( WICKET_ID_FRANCHISEE, "No Franchisees are renting on this floor." );
            franchisees.add( tLabel );
        }
        Model listViewModel = new Model( (Serializable) franchisees );
        listView.setModel( listViewModel );

        add( listView );
    }

}
