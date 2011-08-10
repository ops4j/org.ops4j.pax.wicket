package org.ops4j.pax.wicket.samples.edge.inheritinjection.inherit.internal;

import org.ops4j.pax.wicket.samples.edge.inheritinjection.parent.LinkProvider;

public class DefaultLinkProvider implements LinkProvider {

    private static final long serialVersionUID = 1L;

    public Class<?> getLinkClass() {
        return InheritedPage.class;
    }

}
