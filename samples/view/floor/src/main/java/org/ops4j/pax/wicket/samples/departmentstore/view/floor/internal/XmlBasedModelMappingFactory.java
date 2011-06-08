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

import java.util.UUID;

import org.ops4j.pax.wicket.api.ContentSourceFactory;
import org.ops4j.pax.wicket.api.ContentSourceModelMapping;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.util.DefaultAggregationPointDescriptor;
import org.ops4j.pax.wicket.util.DefaultContentSourceDescriptor;
import org.ops4j.pax.wicket.util.DefaultContentSourceModelMapping;

public class XmlBasedModelMappingFactory implements ContentSourceFactory<DepartmentStore> {

    private static final String DESTINATION_ID = "swp.floor";
    private static final String AGGREGATION_POINT_PREFIX = "agg.";
    private static final String PANEL_PREFIX = "panel.";
    private static final String TAB_PREFIX = "tab.";

    public static final String REFERENCE_MODEL = "floorBean";
    public static final String REFERENCE_AGGREGATOR = "sources";
    public static final String REFERENCE_SOURCE = "floorPanel";

    public XmlBasedModelMappingFactory() {
    }

    public ContentSourceModelMapping createContentSourceMappings(DepartmentStore departmentStore) {
        DefaultContentSourceModelMapping mapping = new DefaultContentSourceModelMapping(true);
        for (Floor floor : departmentStore.getFloors()) {
            String aggregationPointBeanId = AGGREGATION_POINT_PREFIX + floor.getName() + UUID.randomUUID();
            String floorPanelBeanId = PANEL_PREFIX + floor.getName() + UUID.randomUUID();
            String floorTabBeanId = TAB_PREFIX + floor.getName();
            String modelId = floor.getName() + UUID.randomUUID();

            mapping.addModelObject(modelId, floor);

            mapping.addAggregationPoint(new DefaultAggregationPointDescriptor(floor.getName(),
                    aggregationPointBeanId));

            DefaultContentSourceDescriptor floorPanel =
                new DefaultContentSourceDescriptor(floorPanelBeanId, floorPanelBeanId, XmlBasedFloorPanel.class);
            floorPanel.addOverwrite(REFERENCE_MODEL, modelId);
            floorPanel.addOverwrite(REFERENCE_AGGREGATOR, aggregationPointBeanId);
            mapping.addContentSource(floorPanel);

            DefaultContentSourceDescriptor floorTab =
                new DefaultContentSourceDescriptor(floorTabBeanId, floorTabBeanId, XmlBasedFloorTab.class);
            floorTab.addDestination(DESTINATION_ID);
            floorTab.addOverwrite(REFERENCE_MODEL, modelId);
            floorTab.addOverwrite(REFERENCE_SOURCE, floorPanelBeanId);
            mapping.addContentSource(floorTab);
        }
        return mapping;
    }
}
