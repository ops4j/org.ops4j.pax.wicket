package org.ops4j.pax.wicket.api;

import javax.servlet.Filter;

public interface FilterFactory extends Comparable<FilterFactory> {

    /**
     * Service property name for the configuration of the priority of a <i>Filter</i>
     */
    String FILTER_PRIORITY = "pax.wicket.filter.priority";

    Integer getPriority();

    String getApplicationName();

    Filter createFilter();

}
