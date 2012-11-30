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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    /**
     * 
     */
    private static final String APACHE_WICKET_NAMESPACE = "org.apache.wicket.";

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
        String symbolicName = bundleToScan.getSymbolicName();
        if (symbolicName.equals(Activator.SYMBOLIC_NAME)
                || symbolicName.startsWith(APACHE_WICKET_NAMESPACE)) {
            LOGGER.debug("Ignore the pax-wicket service package for PageMounting.");
            return;
        }
        if (symbolicName.startsWith(APACHE_WICKET_NAMESPACE)) {
            LOGGER.debug("Ignore apache-wicket bundle " + symbolicName + " for PageMounting.");
            return;
        }
        LOGGER.trace("Scanning bundle {} for PaxWicketMountPoint annotations", symbolicName);
        if (mountPointRegistrations.containsKey(symbolicName)) {
            removeBundle(bundleToScan);
        }
        mountPointRegistrations.put(symbolicName, new ArrayList<DefaultPageMounter>());
        Enumeration<?> findEntries = bundleToScan.findEntries("", "*.class", true);
        while (findEntries.hasMoreElements()) {
            URL object = (URL) findEntries.nextElement();
            String className = object.getFile().substring(1, object.getFile().length() - 6).replaceAll("/", ".");
            Class<?> candidateClass = null;
            try {
                candidateClass = loadCandidate(className, bundleToScan);
            } catch (NoClassDefFoundError e) {
                // Its not nice to catch errors, but otherwhise we can't give a nice feedback!
                String message = e.getMessage();
                if (message != null) {
                    // In eclipse, the entry for a class is prepend by the "bin-output-folder" (e.g.
                    // bin/my/package/MyClass.class
                    // If we detect this, try to load the real classname that is mentiened in the message
                    Pattern pattern = Pattern.compile("\\(wrong name: (.+)\\)");
                    Matcher matcher = pattern.matcher(message);
                    if (matcher.find()) {
                        String realname = matcher.group(1);
                        LOGGER.debug("It seems the entry has a misleading name for class {}, retry with name {}",
                            className, realname);
                        candidateClass = loadCandidate(realname.replace('/', '.'), bundleToScan);
                    }
                }
                if (candidateClass == null) {
                    // If still null our fallback does not work...
                    throw new IllegalStateException(
                        "Class '"
                                + className
                                + "' found via bundle but classloader complains about NoClassDefFoundError although existing in bundle "
                                + symbolicName
                                + " (is the jar file corrupted or a dependant optional dependencies not resolved?)",
                        e);
                }
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
                mountPointRegistrations.get(symbolicName).add(mountPointRegistration);
                LOGGER.debug("Mounting page {} at {}", pageClass.getName(), mountPoint.mountPoint());
            }
        }
    }

    /**
     * @param className
     * @param bundleToScan
     * @return
     */
    private Class<?> loadCandidate(String className, Bundle bundleToScan) {
        try {
            return bundleToScan.loadClass(className);
        } catch (ClassNotFoundException e) {
            throw new IllegalStateException("Class '" + className
                    + "' not found via bundle " + bundleToScan.getSymbolicName() + "although existing in bundle",
                e);
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
