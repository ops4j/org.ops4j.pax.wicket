package org.ops4j.pax.wicket.internal;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.ops4j.pax.wicket.api.PaxWicketInjector;

/**
 * Simple wrapper transforming calls from a
 */
public class ComponentInstantiationListenerFacade implements IComponentInstantiationListener {

    private PaxWicketInjector toWrap;

    public ComponentInstantiationListenerFacade(PaxWicketInjector toWrap) {
        this.toWrap = toWrap;
    }

    public void onInstantiation(Component component) {
        toWrap.inject(component);
    }

}
