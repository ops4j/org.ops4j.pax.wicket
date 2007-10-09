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
package org.ops4j.pax.wicket.toolkit.menus;

import java.util.List;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;

public class PaxWicketSectionPanel extends Panel
{

    private static final long serialVersionUID = 1L;

    public PaxWicketSectionPanel( PaxWicketMenuSection section, final String id )
    {
        super( id );
        final String sectionName = section.getSectionName();
        List<String> sources = section.getWiredSourceIds( id, null );
        ListView listView = new ListView( "items", sources )
        {
            private static final long serialVersionUID = 1L;

            protected void populateItem( final ListItem listitem )
            {
                PaxWicketMenuSection menuSection = PaxWicketMenuSection.getPaxWicketMenuSection( sectionName );
                menuSection.createWiredComponent( id, PaxWicketMenuSection.MENUITEM );
            }
        };

        add( listView );
    }
}
