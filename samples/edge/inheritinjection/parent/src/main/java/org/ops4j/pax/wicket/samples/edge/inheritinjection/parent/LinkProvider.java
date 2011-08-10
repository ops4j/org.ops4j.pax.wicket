package org.ops4j.pax.wicket.samples.edge.inheritinjection.parent;

import java.io.Serializable;

public interface LinkProvider extends Serializable {

    Class<?> getLinkClass();

}
