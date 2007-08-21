/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2007 David Leangen
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

import org.apache.wicket.PageParameters;
import org.apache.wicket.ResourceReference;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.link.BookmarkablePageLink;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.ops4j.pax.wicket.toolkit.actions.PaxWicketAction;
import org.ops4j.pax.wicket.toolkit.actions.PaxWicketBookmarkableLink;

public class PaxWicketMenuItemPanel extends Panel
{

    private static final long serialVersionUID = 1L;
    
    private PaxWicketMenuItem m_menuItem;

    public PaxWicketMenuItemPanel( PaxWicketMenuItem menuItem, String contentId )
    {
        super( contentId );
        m_menuItem = menuItem;
        PaxWicketBookmarkableLink pageLink = menuItem.getLink();
        if ( pageLink != null )
        {
            Class pageClass = pageLink.getPageClass();
            PageParameters params = pageLink.getParameters();
            BookmarkablePageLink bookmarkableLink = new BookmarkablePageLink( "link", pageClass, params );
            add( bookmarkableLink );
        }
        else
        {
            PaxWicketAction tMenuItemAction = menuItem.getAction();
            Model tMenuItemActionModel = new Model( tMenuItemAction );
            Link actionLink = new Link( "link", tMenuItemActionModel )
            {
                private static final long serialVersionUID = 1L;

                public void onClick()
                {
                    PaxWicketAction action = (PaxWicketAction) getModelObject();
                    action.actionPerformed( m_menuItem, this );
                }
            };
            add( actionLink );
        }
        ResourceReference ref = menuItem.getImage();
        Label dummy;
        if ( menuItem.getAlignment() == Alignment.before )
        {
            add( new Image( "image-left", ref ) );
            dummy = new Label( "image-right" );
        }
        else
        {
            add( new Image( "image-right", ref ) );
            dummy = new Label( "image-left" );
        }
        dummy.setVisible( false );
        add( dummy );
        Label caption = new Label( "caption", menuItem.getCaption() );
        add( caption );
    }
}
