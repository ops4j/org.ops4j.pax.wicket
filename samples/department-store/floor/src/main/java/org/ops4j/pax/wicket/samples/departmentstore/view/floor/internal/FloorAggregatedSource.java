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

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewTabContent;
import org.ops4j.pax.wicket.util.AbstractAggregatedSource;
import org.osgi.framework.BundleContext;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

public class FloorAggregatedSource extends AbstractAggregatedSource<FloorPanel>
    implements OverviewTabContent
{

    private static final HashMap<String, FloorAggregatedSource> m_instances;

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
        String floorName = floor.getName();
        m_floor = new Model( floorName );
        m_instances.put( floorName, this );
    }

    protected FloorPanel createComponent( String wicketId )
    {
        List<String> sources = getWiredSourceIds( FloorPanel.WICKET_ID_FRANCHISEE, null );
        String floorName = (String) m_floor.getObject();
        return new FloorPanel( wicketId, sources, floorName );
    }

    public AbstractTab createTab( Locale locale )
    {
        return new FloorTab( m_floor );
    }

    public final String getFloorId()
    {
        return (String) m_floor.getObject();
    }

    public final String getTabId()
    {
        return m_tabId;
    }

    /**
     * Returns the floor aggregated source given the specified {@code floorId} argument.
     *
     * @param floorId The floor id. This argument must not be {@code null}.
     *
     * @return The floor aggregated source given the specified {@code floorId} argument.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code floorId} argument is {@code null}.
     * @since 1.0.0
     */
    static FloorAggregatedSource getInstance( String floorId )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( floorId, "floorId" );
        synchronized( m_instances )
        {
            return m_instances.get( floorId );
        }
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
            IModel titleModel = getTitle();

            String floorName = (String) titleModel.getObject();

            FloorAggregatedSource source = m_instances.get( floorName );

            return source.createSourceComponent( panelId );
        }
    }

}
