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
import wicket.markup.html.WebPage;
import wicket.markup.html.link.Link;
import wicket.markup.html.list.ListItem;
import wicket.markup.html.list.ListView;
import wicket.markup.html.panel.Panel;

public class SectionsPanel extends Panel
{
    private List<Item> m_items;
    private ListView m_itemView;

    public SectionsPanel( String id )
    {
        super( id );
        m_items = new ArrayList<Item>();
        m_itemView = new ListView( "items", m_items )
        {
            protected void populateItem( final ListItem item )
            {
                repopulate( item );
            }
        };
        add( m_itemView );
    }

    public void addSectionItem( String id, WebPage page )
    {
        Item item = new Item( id, page );
        m_items.add( item );
    }

    public void removeSectionItem( String id )
    {
        Iterator it = m_items.iterator();
        while( it.hasNext() )
        {
            Item item = (Item) it.next();
            if( item.m_id.equals( id ) )
            {
                it.remove();
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
                setResponsePage( item.m_page );
            }
        };
        listitem.add( link );
    }

    private class Item
    {
        private String m_id;
        private WebPage m_page;

        public Item( String id, WebPage page )
        {
            m_page = page;
            m_id = id;
        }
    }
}
