package org.ops4j.pax.wicket.api;

import org.apache.wicket.Component;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;

/**
 * General PaxWicket injection abstraction. This interface takes any object and tries to inject all
 * {@link PaxWicketBean} annotations. In that way there is no difference if its a Wicket {@link Component} or not.
 */
public interface PaxWicketInjector {

    public void inject(Object toInject);

}
