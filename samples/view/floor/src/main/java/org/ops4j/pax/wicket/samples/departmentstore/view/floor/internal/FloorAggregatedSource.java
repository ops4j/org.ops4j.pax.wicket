/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;

import java.util.HashMap;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.ops4j.pax.wicket.api.TabContentSource;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.util.AbstractAggregatedSource;
import org.osgi.framework.BundleContext;

public class FloorAggregatedSource extends AbstractAggregatedSource<FloorPanel> implements
        TabContentSource<AbstractTab> {

    private static final HashMap<String, FloorAggregatedSource> m_instances;

    private final Model floor;
    private final String tabId;

    static {
        m_instances = new HashMap<String, FloorAggregatedSource>();
    }

    public FloorAggregatedSource(Floor floor, String aggregationPoint, String destination,
                                  BundleContext bundleContext, String applicationname) {
        super(bundleContext, applicationname, aggregationPoint, destination);
        tabId = aggregationPoint;
        String floorName = floor.getName();
        this.floor = new Model<String>(floorName);
        m_instances.put(floorName, this);
    }

    @Override
    protected FloorPanel createComponent(String wicketId) {
        List<String> sources = getRegisteredSourceIds(FloorPanel.WICKET_ID_FRANCHISEE);
        String floorName = (String) floor.getObject();
        return new FloorPanel(wicketId, sources, floorName);
    }

    public AbstractTab createSourceTab() {
        return new FloorTab(floor);
    }

    public AbstractTab createSourceTab(String title) {
        return new FloorTab(new Model<String>(title));
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
    static FloorAggregatedSource getInstance(String floorId)
        throws IllegalArgumentException {
        validateNotEmpty(floorId, "floorId");
        synchronized (m_instances) {
            return m_instances.get(floorId);
        }
    }

    private static class FloorTab extends AbstractTab {

        private static final long serialVersionUID = 1L;

        public FloorTab(Model title) {
            super(title);
        }

        @Override
        public Panel getPanel(String panelId) {
            IModel titleModel = getTitle();

            String floorName = (String) titleModel.getObject();

            FloorAggregatedSource source = m_instances.get(floorName);

            return source.createSourceComponent(panelId);
        }
    }

}
