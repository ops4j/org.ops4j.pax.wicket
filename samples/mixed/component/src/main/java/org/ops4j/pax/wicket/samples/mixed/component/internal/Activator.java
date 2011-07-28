package org.ops4j.pax.wicket.samples.mixed.component.internal;

import org.ops4j.pax.wicket.samples.mixed.api.ComponentProvider;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class Activator implements BundleActivator {

    private ServiceRegistration serviceRegistration;

    public void start(BundleContext context) throws Exception {
        MyPanelProvider panelProvider = new MyPanelProvider();
        serviceRegistration = context.registerService(ComponentProvider.class.getName(), panelProvider, null);
    }

    public void stop(BundleContext context) throws Exception {
        serviceRegistration.unregister();
    }

}
