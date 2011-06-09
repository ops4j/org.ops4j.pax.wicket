package org.ops4j.pax.wicket.api;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A simple singleton class storing and retrieving {@link PaxWicketInjector}s. The {@link #getInjector()} method only
 * works in an PaxWicket-Enabled integration context.
 */
public final class InjectorHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(InjectorHolder.class);
    private static final InjectorHolder instance = new InjectorHolder();

    private Map<String, PaxWicketInjector> injectorMap = new HashMap<String, PaxWicketInjector>();

    private InjectorHolder() {
    }

    /**
     * Retrieves the InjectionHolder responsible for the active {@link Application}. Therefore this will only work in an
     * active Wicket context!
     */
    public static PaxWicketInjector getInjector() {
        String applicationName = Application.get().getApplicationKey();
        PaxWicketInjector injector = null;
        synchronized (instance.injectorMap) {
            injector = instance.injectorMap.get(applicationName);
        }
        if (injector == null) {
            throw new IllegalStateException(String.format("No Injector is set for application %s",
                Application.get().getApplicationKey()));
        }
        return injector;
    }

    public static void setInjector(String applicationName, PaxWicketInjector newInjector) {
        synchronized (instance.injectorMap) {
            instance.injectorMap.put(applicationName, newInjector);
        }
        LOGGER.debug("registered Injector for application {}", applicationName);
    }

}
