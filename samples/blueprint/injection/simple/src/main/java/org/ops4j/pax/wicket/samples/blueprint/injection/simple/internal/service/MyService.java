package org.ops4j.pax.wicket.samples.blueprint.injection.simple.internal.service;

/**
 * This class presents a VERY simple interface which can be located in any package or bundle wished. For simplicity it
 * should be kept in one bundle for now.
 */
public interface MyService {

    /**
     * Very simple method returning the toEcho value.
     */
    String someEchoMethod(String toEcho);

}
