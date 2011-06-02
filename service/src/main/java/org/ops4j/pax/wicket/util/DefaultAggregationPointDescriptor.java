package org.ops4j.pax.wicket.util;

import java.io.Serializable;

import org.ops4j.pax.wicket.api.AggregationPointDescriptor;

public class DefaultAggregationPointDescriptor implements AggregationPointDescriptor, Serializable {

    private static final long serialVersionUID = 1L;

    private String aggregationPointName;
    private String aggregationPointId;

    public DefaultAggregationPointDescriptor(String aggregationPointName, String aggregationPointId) {
        this.aggregationPointName = aggregationPointName;
        this.aggregationPointId = aggregationPointId;
    }

    public String getAggregationPointName() {
        return aggregationPointName;
    }

    public String getAggregationPointId() {
        return aggregationPointId;
    }

}
