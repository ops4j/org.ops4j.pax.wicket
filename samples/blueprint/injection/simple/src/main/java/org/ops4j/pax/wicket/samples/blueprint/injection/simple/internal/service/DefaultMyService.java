package org.ops4j.pax.wicket.samples.blueprint.injection.simple.internal.service;

/**
 * Most trivial implementation of the service. This implementation could also be located in a different bundle and be
 * imported as OSGi service.
 */
public class DefaultMyService implements MyService {

    public String someEchoMethod(String toEcho) {
        return toEcho;
    }

}
