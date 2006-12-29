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

import org.ops4j.pax.wicket.toolkit.actions.PaxWicketAction;
import org.ops4j.pax.wicket.toolkit.actions.PaxWicketBookmarkableLink;
import wicket.PageParameters;
import wicket.ResourceReference;
import wicket.markup.html.basic.Label;
import wicket.markup.html.image.Image;
import wicket.markup.html.link.BookmarkablePageLink;
import wicket.markup.html.link.Link;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

public class PaxWicketMenuItemPanel extends Panel
{

    private PaxWicketMenuItem m_menuItem;

    public PaxWicketMenuItemPanel( PaxWicketMenuItem menuItem, String wicketId )
    {
        super( wicketId );
        m_menuItem = menuItem;
        PaxWicketBookmarkableLink pageLink = menuItem.getLink();
        if( pageLink != null )
        {
            Class pageClass = pageLink.getPageClass();
            PageParameters params = pageLink.getParameters();
            BookmarkablePageLink bookmarkableLink = new BookmarkablePageLink( "link", pageClass, params );
            add( bookmarkableLink );
        }
        else
        {
            Link actionLink = new Link("link", new Model( menuItem.getAction() ) )
            {
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
        if( menuItem.getAlignment() == Alignment.before )
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
