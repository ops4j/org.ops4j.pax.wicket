package org.ops4j.pax.wicket.internal;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import org.apache.wicket.Component;
import org.junit.Test;
import org.ops4j.pax.wicket.api.PaxWicketInjector;

public class ComponentInstantiationListenerFacadeTest {

    @Test
    public void testCallToFacade_shouldBeForwardedToRealClass() {
        Component component = mock(Component.class);
        PaxWicketInjector injector = mock(PaxWicketInjector.class);
        new ComponentInstantiationListenerFacade(injector).onInstantiation(component);
        verify(injector).inject(component);
    }

}
