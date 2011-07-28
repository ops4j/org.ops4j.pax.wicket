package org.ops4j.pax.wicket.samples.mixed.component.internal;

import org.apache.wicket.Component;
import org.ops4j.pax.wicket.samples.mixed.api.ComponentProvider;

public class MyPanelProvider implements ComponentProvider {

    public Component getComponent(String id) {
        return new MyPanel(id);
    }

}
