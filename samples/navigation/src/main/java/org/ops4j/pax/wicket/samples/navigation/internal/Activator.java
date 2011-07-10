package org.ops4j.pax.wicket.samples.navigation.internal;

import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private PaxWicketApplicationFactory paxWicketApplicationFactory;

    public void start(BundleContext context) throws Exception {
        paxWicketApplicationFactory =
            new PaxWicketApplicationFactory(context, NavigationPage.class, "navigation", "navigation",
                new NavigationApplicationFactory());
        paxWicketApplicationFactory.register();
    }

    public void stop(BundleContext context) throws Exception {
        paxWicketApplicationFactory.dispose();
    }

}
