package org.ops4j.pax.wicket.internal;

/**
 * A method had been called which is not implemented right now
 */
public class NotImplementedException extends RuntimeException {

    private static final long serialVersionUID = -121171427554149005L;

    public NotImplementedException() {
        super();
    }

    public NotImplementedException(String message, Throwable cause) {
        super(message, cause);
    }

    public NotImplementedException(String message) {
        super(message);
    }

    public NotImplementedException(Throwable cause) {
        super(cause);
    }

}
