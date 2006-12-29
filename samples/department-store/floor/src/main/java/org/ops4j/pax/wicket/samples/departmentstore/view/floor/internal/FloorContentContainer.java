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

import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.view.OverviewTabContent;
import org.ops4j.pax.wicket.service.DefaultContentContainer;
import org.osgi.framework.BundleContext;

import wicket.Component;
import wicket.extensions.markup.html.tabs.AbstractTab;
import wicket.markup.html.panel.Panel;
import wicket.model.Model;

public class FloorContentContainer extends DefaultContentContainer<FloorPanel>
    implements OverviewTabContent
{
    private final Floor m_floor;
    private final String m_tabId;

    public FloorContentContainer( Floor floor, String containmentId, String destinationId, BundleContext bundleContext,
        String applicationname )
    {
        super( bundleContext, applicationname, containmentId, destinationId );
        m_tabId = containmentId;
        m_floor = floor;
    }

    protected FloorPanel createComponent( String id, Component parent )
    {
        return new FloorPanel( id, this, m_floor );
    }

    public AbstractTab createTab( Locale locale )
    {
        final String floorName = m_floor.getName();
        Model title = new Model( floorName );
        return new AbstractTab( title )
        {
            private static final long serialVersionUID = 1L;

            @Override
            public Panel getPanel( String panelId )
            {
                Activator instance = Activator.getInstance();
                FloorContentContainer floorContentContainer = instance.getFloorContentContainer( floorName );
                return floorContentContainer.createComponent( panelId, null );
            }
        };
    }

    public String getTabId()
    {
        return m_tabId;
    }
}
