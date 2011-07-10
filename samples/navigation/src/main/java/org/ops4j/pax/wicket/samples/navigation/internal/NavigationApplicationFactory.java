package org.ops4j.pax.wicket.samples.navigation.internal;

import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.ApplicationFactory;
import org.ops4j.pax.wicket.api.ApplicationLifecycleListener;

public class NavigationApplicationFactory implements ApplicationFactory {
    public WebApplication createWebApplication(ApplicationLifecycleListener lifecycleListener) {
        return new NavigationApplication(lifecycleListener);
    }
}
