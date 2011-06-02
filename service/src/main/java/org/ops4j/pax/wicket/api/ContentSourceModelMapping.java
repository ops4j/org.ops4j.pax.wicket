package org.ops4j.pax.wicket.api;

import java.util.List;
import java.util.Map;

public interface ContentSourceModelMapping {

    List<ContentSourceDescriptor> getContenSources();

    List<AggregationPointDescriptor> getAggregationPoints();

    Map<String, Object> getModelObjects();

    boolean allowMultibleRegistrations();

}
