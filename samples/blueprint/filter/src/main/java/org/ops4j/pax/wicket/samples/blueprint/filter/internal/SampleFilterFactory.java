package org.ops4j.pax.wicket.samples.blueprint.filter.internal;

import javax.servlet.Filter;

import org.ops4j.pax.wicket.api.ConfigurableFilterConfig;
import org.ops4j.pax.wicket.api.FilterFactory;

public class SampleFilterFactory implements FilterFactory {

    public int compareTo(FilterFactory o) {
        if (o.getPriority() < 1) {
            return 1;
        }
        if (o.getPriority() > 1) {
            return -1;
        }
        return 0;
    }

    public Integer getPriority() {
        return 1;
    }

    public String getApplicationName() {
        return "blueprint.filter.paxwicket";
    }

    public Filter createFilter(ConfigurableFilterConfig filterConfig) {
        return new IAmASysoutFilter();
    }

}
