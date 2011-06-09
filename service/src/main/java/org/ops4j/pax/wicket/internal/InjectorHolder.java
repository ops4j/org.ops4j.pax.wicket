package org.ops4j.pax.wicket.internal;

import java.util.HashMap;
import java.util.Map;

import org.apache.wicket.Application;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InjectorHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(InjectorHolder.class);
    private static final InjectorHolder instance = new InjectorHolder();
    
    private Map<String, Injector> injectorMap = new HashMap<String, Injector>();
    
    private InjectorHolder() {
    }

    public static Injector getInjector()
    {
        String applicationName = Application.get().getApplicationKey();
        Injector injector = null;
        synchronized (instance.injectorMap) {
            injector = instance.injectorMap.get(applicationName);
        }
        if (injector == null)
        {
            throw new IllegalStateException(String.format("No Injector is set for application %s",
                Application.get().getApplicationKey()));
        }
        return injector;
    }

    public static void setInjector(String applicationName, Injector newInjector)
    {
        synchronized (instance.injectorMap) {
            instance.injectorMap.put(applicationName, newInjector);
        }
        LOGGER.debug("registered Injector for application {}", applicationName);
    }

}
