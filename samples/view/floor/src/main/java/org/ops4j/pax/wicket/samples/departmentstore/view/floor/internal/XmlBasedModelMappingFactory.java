package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

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
            String aggregationPointBeanId = AGGREGATION_POINT_PREFIX + floor.getName();
            String floorPanelBeanId = PANEL_PREFIX + floor.getName();
            String floorTabBeanId = TAB_PREFIX + floor.getName();
            String modelId = floor.getName();

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
