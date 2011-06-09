package org.ops4j.pax.wicket.api;

import org.ops4j.pax.wicket.internal.InjectorHolder;

public final class Injection {

    /**
     * Looks for PaWicketBean-annotated fields and applies a proxy on them.
     */
    public static void makeOn(Object object) {
        InjectorHolder.getInjector().inject(object);
    }
    
}
