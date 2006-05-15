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

import wicket.markup.html.image.Image;
import wicket.markup.html.panel.Panel;
import wicket.markup.html.basic.Label;
import wicket.Resource;

public class TitlePanel extends Panel
{
    private Resource m_mainlogo;
    private Resource m_sublogo;
    private String m_title;

    public TitlePanel( String id )
    {
        super( id );
        add( new Image( "logo" ) );
        add( new Label( "title" ) );
        add( new Image( "sublogo" ) );
    }

    public void setSubLogo( Resource resource )
    {
        m_sublogo = resource;
        replace( new Image( "sublogo", m_sublogo ) );
    }

    public Resource getSubLogo()
    {
        return m_sublogo;
    }

    public void setMainLogo( Resource resource )
    {
        m_mainlogo = resource;
        replace( new Image( "mainlogo", m_mainlogo ) );
    }

    public Resource getMainLogo()
    {
        return m_mainlogo;
    }

    public void setTitle( String title )
    {
        m_title = title;
        replace( new Label( "title", m_title ) );
    }

    public String getTitle()
    {
        return m_title;
    }
}

