package org.ops4j.pax.wicket.samples.mixed.api;

import org.apache.wicket.Component;

public interface ComponentProvider {

    Component getComponent(String id);

}
