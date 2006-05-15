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
package org.ops4j.pax.wicket.samples.library.skins.std;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.list.ListView;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.link.Link;

public class MenuPanel extends Panel
{
    private List<Item> m_menuitems;
    private ListView m_itemView;

    public MenuPanel( String id )
    {
        super( id );
        m_menuitems = new ArrayList();
        m_itemView = new ListView( "items", m_menuitems )
        {
            protected void populateItem( final ListItem item )
            {
                repopulate( item );
            }
        };
        add( m_itemView );

    }

    public void addMenuItem( String id, Panel content )
    {
        Item item = new Item( id, content );
        m_menuitems.add( item );
    }

    public void removeMenuItem( String id )
    {
        Iterator list = m_menuitems.iterator();
        while( list.hasNext() )
        {
            Item item = (Item) list.next();
            if( item.m_id.equals( id ) )
            {
                list.remove();
            }
        }
    }

    private void repopulate( ListItem listitem )
    {
        final Item item = (Item) listitem.getModelObject();
        Link link = new Link( item.m_id )
        {
            public void onClick()
            {
                // Locate the ContentPanel and set the content of this item to that page, and the reload(?).
            }
        };
        listitem.add( link );
    }


    private class Item
    {
        private String m_id;
        private Panel m_content;

        public Item( String id, Panel content )
        {
            m_content = content;
            m_id = id;
        }
    }
}
