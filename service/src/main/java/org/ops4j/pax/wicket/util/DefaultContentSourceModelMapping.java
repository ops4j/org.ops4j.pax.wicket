package org.ops4j.pax.wicket.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.pax.wicket.api.AggregationPointDescriptor;
import org.ops4j.pax.wicket.api.ContentSourceDescriptor;
import org.ops4j.pax.wicket.api.ContentSourceModelMapping;

public class DefaultContentSourceModelMapping implements ContentSourceModelMapping, Serializable {

    private static final long serialVersionUID = 1L;

    private List<ContentSourceDescriptor> contentSources = new ArrayList<ContentSourceDescriptor>();
    private List<AggregationPointDescriptor> aggregationPoints = new ArrayList<AggregationPointDescriptor>();
    private Map<String, Object> modelObjects = new HashMap<String, Object>();
    private Boolean allowMultibleRegistrations = false;

    public DefaultContentSourceModelMapping() {
    }

    public DefaultContentSourceModelMapping(boolean allowMultibleRegistrations) {
        this.allowMultibleRegistrations = allowMultibleRegistrations;
    }

    public void addContentSource(ContentSourceDescriptor contentSource) {
        contentSources.add(contentSource);
    }

    public void addAggregationPoint(AggregationPointDescriptor aggregationPoint) {
        aggregationPoints.add(aggregationPoint);
    }

    public void addModelObject(String beanId, Object model) {
        modelObjects.put(beanId, model);
    }

    public List<ContentSourceDescriptor> getContenSources() {
        return contentSources;
    }

    public List<AggregationPointDescriptor> getAggregationPoints() {
        return aggregationPoints;
    }

    public Map<String, Object> getModelObjects() {
        return modelObjects;
    }

    public boolean allowMultibleRegistrations() {
        return allowMultibleRegistrations;
    }

}
