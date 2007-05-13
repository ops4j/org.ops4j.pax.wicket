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

import java.util.Collections;
import java.util.List;
import wicket.Component;
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
final class FloorPanel extends Panel
{

    private static final long serialVersionUID = 1L;

    public static final String WICKET_ID_NAME_LABEL = "name";
    public static final String WICKET_ID_FRANCHISEE = "franchisee";
    public static final String WICKET_ID_FRANCHISEES = "franchisees";

    FloorPanel( String wicketId, List<String> sources, String floorName )
    {
        super( wicketId );

        ListView view;
        if( sources.isEmpty() )
        {
            String message = "No Franchisees are renting on this floor.";
            view = new LabelListView( WICKET_ID_FRANCHISEES, Collections.singletonList( message ) );
        }
        else
        {
            view = new FloorListView( WICKET_ID_FRANCHISEES, sources, floorName );
        }

        add( view );
    }

    private static final class LabelListView extends ListView
    {

        private static final long serialVersionUID = 1L;

        private LabelListView( String wicketId, List<String> list )
        {
            super( wicketId, list );
        }

        @Override
        protected final void populateItem( ListItem item )
        {
            String message = (String) item.getModelObject();
            Label label = new Label( WICKET_ID_FRANCHISEE, message );
            item.add( label );
        }
    }

    private static class FloorListView extends ListView
    {

        private static final long serialVersionUID = 1L;
        private final String m_floorName;

        private FloorListView( String wicketId, List<String> sources, String floorName )
        {
            super( wicketId, sources );
            m_floorName = floorName;
        }

        @Override
        protected void populateItem( ListItem item )
        {
            String sourceId = (String) item.getModelObject();
            FloorAggregatedSource instance = FloorAggregatedSource.getInstance( m_floorName );
            Component component = instance.createWiredComponent( sourceId, WICKET_ID_FRANCHISEE );

            item.add( component );
        }
    }
}
