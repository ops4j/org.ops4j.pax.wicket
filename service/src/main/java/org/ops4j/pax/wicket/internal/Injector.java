package org.ops4j.pax.wicket.internal;

public interface Injector {

    /**
     * An implementation should look for PaWicketBean-annotated fields and
     * apply a proxy on such a field.
     */
    void inject(Object object);
    
}
