package org.ops4j.pax.wicket.samples.navigation.internal;

import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.ApplicationLifecycleListener;

public class NavigationApplication extends WebApplication {

    private ApplicationLifecycleListener lifecycleListener;

    public NavigationApplication(ApplicationLifecycleListener lifecycleListener) {
        super();
        this.lifecycleListener = lifecycleListener;
    }

    @Override
    public Class<NavigationPage> getHomePage()
    {
        return NavigationPage.class;
    }

    @Override
    protected void init() {
        lifecycleListener.onInit(this);
        super.init();
    }

    @Override
    protected void onDestroy() {
        lifecycleListener.onDestroy(this);
        super.onDestroy();
    }

}
