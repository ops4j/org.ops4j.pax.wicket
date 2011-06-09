package org.ops4j.pax.wicket.api;

import org.ops4j.pax.wicket.internal.InjectorHolder;

public final class Injection {

    public static void makeOn(Object object) {
        InjectorHolder.getInjector().inject(object);
    }
    
}
