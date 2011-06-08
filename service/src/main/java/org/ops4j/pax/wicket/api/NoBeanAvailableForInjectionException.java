package org.ops4j.pax.wicket.api;

import org.apache.wicket.application.IComponentInstantiationListener;

/**
 * Since the {@link IComponentInstantiationListener#onInstantiation(org.apache.wicket.Component)} method does not have a
 * return value we couldn't ask a service simply if it could find a bean to inject for a component to inject at all.
 * Therefore we add the notatiion to pax-wicket that every
 * {@link IComponentInstantiationListener#onInstantiation(org.apache.wicket.Component)} method call can throw this
 * {@link NoBeanAvailableForInjectionException}. Using this exception we can iterate over multible provider checking if
 * a bean is available.
 */
public class NoBeanAvailableForInjectionException extends RuntimeException {

    private static final long serialVersionUID = -8910110478566112761L;

    public NoBeanAvailableForInjectionException() {
        super();
    }

    public NoBeanAvailableForInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoBeanAvailableForInjectionException(String message) {
        super(message);
    }

    public NoBeanAvailableForInjectionException(Throwable cause) {
        super(cause);
    }

}
