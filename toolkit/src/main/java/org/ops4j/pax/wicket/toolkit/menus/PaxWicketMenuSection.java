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

import org.ops4j.pax.wicket.util.AbstractAggregatedSource;
import org.ops4j.pax.wicket.toolkit.actions.ActionGroup;
import org.osgi.framework.BundleContext;
import wicket.Component;
import wicket.markup.html.panel.Panel;

public class PaxWicketMenuSection extends AbstractAggregatedSource<Panel>
    implements ActionGroup
{

    public static final String MENUSECTION_PREFIX = "menusection:";

    private String m_sectionName;

    public PaxWicketMenuSection( BundleContext context, String application, String sectionName,
                                 String defaultLocation )
        throws IllegalArgumentException
    {
        super( context, application, sectionName, defaultLocation );
        m_sectionName = sectionName;
    }

    protected <T extends Component> Panel createComponent( String contentId, T parent )
        throws IllegalArgumentException
    {
        return new PaxWicketSectionPanel( this, contentId );
    }

    public String getSectionName()
    {
        return m_sectionName;
    }

    public String getIdentifier()
    {
        return MENUSECTION_PREFIX + m_sectionName;
    }
}
