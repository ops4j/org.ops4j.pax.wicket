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

import java.util.Locale;
import java.util.HashMap;

import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewTabContent;
import org.ops4j.pax.wicket.util.AbstractAggregatedSource;
import org.osgi.framework.BundleContext;

import wicket.Component;
import wicket.extensions.markup.html.tabs.AbstractTab;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

public class FloorAggregatedSource extends AbstractAggregatedSource<FloorPanel>
    implements OverviewTabContent
{
    private static HashMap<String, FloorAggregatedSource> m_instances;

    private final Model m_floor;
    private final String m_tabId;

    static
    {
        m_instances = new HashMap<String, FloorAggregatedSource>();
    }

    public FloorAggregatedSource( Floor floor, String aggregationPoint, String destination,
                                  BundleContext bundleContext, String applicationname )
    {
        super( bundleContext, applicationname, aggregationPoint, destination );
        m_tabId = aggregationPoint;
        m_floor = new Model( floor.getName() );
        m_instances.put( floor.getName(), this );
    }

    protected FloorPanel createComponent( String contentId, Component parent )
    {
        return new FloorPanel( contentId, this, m_floor );
    }

    public AbstractTab createTab( Locale locale )
    {
        return new FloorTab( m_floor );
    }

    public String getTabId()
    {
        return m_tabId;
    }

    private static class FloorTab extends AbstractTab
    {

        private static final long serialVersionUID = 1L;

        public FloorTab( Model title )
        {
            super( title );
        }

        @Override
        public Panel getPanel( String panelId )
        {
            String floorName = (String) getTitle().getObject( null );
            FloorAggregatedSource source = m_instances.get( floorName );
            return source.createComponent( panelId, null );
        }
    }
}
