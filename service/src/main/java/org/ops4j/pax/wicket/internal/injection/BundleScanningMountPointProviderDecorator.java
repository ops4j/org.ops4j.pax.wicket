package org.ops4j.pax.wicket.internal.injection;

import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;
import org.ops4j.pax.wicket.util.DefaultPageMounter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class BundleScanningMountPointProviderDecorator implements InjectionAwareDecorator {

    private BundleContext bundleContext;
    private String applicationName;
    private List<DefaultPageMounter> mountPointRegistrations = new ArrayList<DefaultPageMounter>();

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void start() throws Exception {
        Bundle bundleToScan = bundleContext.getBundle();
        Enumeration<?> findEntries = bundleToScan.findEntries("", "*.class", true);
        while (findEntries.hasMoreElements()) {
            URL object = (URL) findEntries.nextElement();
            String className = object.getFile().substring(1, object.getFile().length() - 6).replaceAll("/", ".");
            Class<?> candidateClass = bundleToScan.loadClass(className);
            if (!Page.class.isAssignableFrom(candidateClass)) {
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<? extends Page> pageClass = (Class<? extends Page>) candidateClass;
            PaxWicketMountPoint mountPoint = pageClass.getAnnotation(PaxWicketMountPoint.class);
            if (mountPoint != null) {
                DefaultPageMounter mountPointRegistration = new DefaultPageMounter(applicationName, bundleContext);
                mountPointRegistration.addMountPoint(mountPoint.mountPoint(), pageClass);
                mountPointRegistration.register();
                mountPointRegistrations.add(mountPointRegistration);
            }
        }
    }

    public void stop() throws Exception {
        for (DefaultPageMounter pageMounter : mountPointRegistrations) {
            pageMounter.dispose();
        }
        mountPointRegistrations.clear();
    }

}
