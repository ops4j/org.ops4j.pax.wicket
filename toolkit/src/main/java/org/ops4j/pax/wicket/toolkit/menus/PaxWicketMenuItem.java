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

import org.ops4j.pax.wicket.toolkit.actions.ActionItem;
import org.ops4j.pax.wicket.toolkit.actions.PaxWicketAction;
import org.ops4j.pax.wicket.toolkit.actions.PaxWicketBookmarkableLink;
import org.ops4j.pax.wicket.util.AbstractContentSource;
import org.osgi.framework.BundleContext;
import wicket.Component;
import wicket.MarkupContainer;
import wicket.ResourceReference;

public class PaxWicketMenuItem extends AbstractContentSource
    implements ActionItem
{
    private String m_caption;
    private String m_identifier;
    private ResourceReference m_image;
    private PaxWicketBookmarkableLink m_link;
    private PaxWicketAction m_action;
    private String m_classifier;
    private boolean m_visible;
    private Alignment m_alignment;

    public PaxWicketMenuItem( BundleContext bundleContext, String application, String menuId, String caption )
    {
        super( bundleContext, menuId, application );
        m_caption = caption;
        m_alignment = Alignment.before;
        m_visible = true;
    }

    public String getCaption()
    {
        return m_caption;
    }

    public void setCaption( String caption )
    {
        m_caption = caption;
    }

    public PaxWicketBookmarkableLink getLink()
    {
        return m_link;
    }

    public void setBookmarkableLink( PaxWicketBookmarkableLink link )
    {
        m_link = link;
    }

    public PaxWicketAction getAction()
    {
        return m_action;
    }

    public void setAction( PaxWicketAction action )
    {
        m_action = action;
    }

    public String getClassifier()
    {
        return m_classifier;
    }

    public void setClassifier( String classifier )
    {
        m_classifier = classifier;
    }

    public String getIdentifier()
    {
        return m_identifier;
    }

    public void setIdentifier( String identifier )
    {
        m_identifier = identifier;
    }

    public ResourceReference getImage()
    {
        return m_image;
    }

    public void setImage( ResourceReference image )
    {
        m_image = image;
    }

    public Alignment getAlignment()
    {
        return m_alignment;
    }

    public void setAlignment( Alignment alignment )
    {
        m_alignment = alignment;
    }

    public boolean isVisible()
    {
        return m_visible;
    }

    public void setVisible( boolean visible )
    {
        m_visible = visible;
    }

    protected Component createWicketComponent( MarkupContainer parent, String contentId )
        throws IllegalArgumentException
    {
        return new PaxWicketMenuItemPanel( this, contentId );
    }
}
