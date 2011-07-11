/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;
import org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener;
import org.ops4j.pax.wicket.util.DefaultPageMounter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The behavior of this Delegating Resolver is different from the {@link BundleDelegatingClassResolver} or
 * {@link BundleDelegatingComponentInstanciationListener}. In this case the bundles are not only scanned during the real
 * operation but rather services are exported directly at the pax-wicket bundle for the application to register the
 * right mount points.
 */
public class BundleDelegatingPageMounter implements InternalBundleDelegationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingPageMounter.class);

    private final String applicationName;
    private final BundleContext paxWicketContext;

    private Map<String, List<DefaultPageMounter>> mountPointRegistrations =
        new HashMap<String, List<DefaultPageMounter>>();

    public BundleDelegatingPageMounter(String applicationName, BundleContext paxWicketContext) {
        this.applicationName = applicationName;
        this.paxWicketContext = paxWicketContext;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void start() {
        // not required for this class
    }

    public void stop() {
        Collection<List<DefaultPageMounter>> values = mountPointRegistrations.values();
        for (List<DefaultPageMounter> bundleMounters : values) {
            for (DefaultPageMounter pageMounter : bundleMounters) {
                pageMounter.dispose();
            }
        }
        mountPointRegistrations = new HashMap<String, List<DefaultPageMounter>>();
    }

    public void addBundle(Bundle bundleToScan) {
        if (bundleToScan.getSymbolicName().equals(Activator.SYMBOLIC_NAME)) {
            LOGGER.debug("Ignore the pax-wicket service package for PageMounting.");
            return;
        }
        LOGGER.trace("Scanning bundle {} for PaxWicketMountPoint annotations", bundleToScan.getSymbolicName());
        if (mountPointRegistrations.containsKey(bundleToScan.getSymbolicName())) {
            removeBundle(bundleToScan);
        }
        mountPointRegistrations.put(bundleToScan.getSymbolicName(), new ArrayList<DefaultPageMounter>());
        Enumeration<?> findEntries = bundleToScan.findEntries("", "*.class", true);
        while (findEntries.hasMoreElements()) {
            URL object = (URL) findEntries.nextElement();
            String className = object.getFile().substring(1, object.getFile().length() - 6).replaceAll("/", ".");
            Class<?> candidateClass;
            try {
                candidateClass = bundleToScan.loadClass(className);
            } catch (ClassNotFoundException e) {
                throw new IllegalStateException("Class not found via bundle although existing in bundle", e);
            }
            if (!Page.class.isAssignableFrom(candidateClass)) {
                LOGGER.debug("Candidate {} not found, this can happen if the class has optional dependencies...");
                continue;
            }
            @SuppressWarnings("unchecked")
            Class<? extends Page> pageClass = (Class<? extends Page>) candidateClass;
            PaxWicketMountPoint mountPoint = pageClass.getAnnotation(PaxWicketMountPoint.class);
            if (mountPoint != null) {
                DefaultPageMounter mountPointRegistration = new DefaultPageMounter(applicationName, paxWicketContext);
                mountPointRegistration.addMountPoint(mountPoint.mountPoint(), pageClass);
                mountPointRegistration.register();
                mountPointRegistrations.get(bundleToScan.getSymbolicName()).add(mountPointRegistration);
                LOGGER.debug("Mounting page {} at {}", pageClass.getName(), mountPoint.mountPoint());
            }
        }
    }

    public void removeBundle(Bundle bundle) {
        List<DefaultPageMounter> registrations = mountPointRegistrations.get(bundle.getSymbolicName());
        if (registrations == null) {
            return;
        }
        for (DefaultPageMounter pageMounter : registrations) {
            pageMounter.dispose();
        }
        mountPointRegistrations.remove(bundle.getSymbolicName());
    }

}
