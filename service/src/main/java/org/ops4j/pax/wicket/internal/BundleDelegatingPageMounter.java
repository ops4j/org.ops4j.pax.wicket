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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;
import org.ops4j.pax.wicket.api.support.DefaultPageMounter;
import org.ops4j.pax.wicket.internal.extender.ExtendedBundle;
import org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener;
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

    private final Map<String, List<DefaultPageMounter>> mountPointRegistrations =
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
        Collection<List<DefaultPageMounter>> values;
        synchronized (mountPointRegistrations) {
            values = new ArrayList<List<DefaultPageMounter>>(mountPointRegistrations.values());
            mountPointRegistrations.clear();
        }
        for (List<DefaultPageMounter> bundleMounters : values) {
            for (DefaultPageMounter pageMounter : bundleMounters) {
                pageMounter.dispose();
            }
        }
    }

    public void addBundle(ExtendedBundle bundle) {
        String symbolicName = bundle.getBundle().getSymbolicName();
        if (bundle.isRelevantForMountPointAnnotations()) {
            LOGGER.trace("Scanning bundle {} for PaxWicketMountPoint annotations", symbolicName);
            ArrayList<DefaultPageMounter> pageMounter = new ArrayList<DefaultPageMounter>();
            Collection<Class<?>> allClasses = bundle.getAllClasses();
            for (Class<?> clazz : allClasses) {
                PaxWicketMountPoint mountPoint = clazz.getAnnotation(PaxWicketMountPoint.class);
                if (mountPoint != null) {
                    if (!Page.class.isAssignableFrom(clazz)) {
                        LOGGER
                            .warn(
                                "ignore PaxWicketMountPoint annotated class {} since it is no page class or has unresolved optional dependencies...",
                                clazz.getName());
                        continue;
                    }
                    DefaultPageMounter mountPointRegistration =
                        new DefaultPageMounter(applicationName, paxWicketContext);
                    // We have checked this before...
                    @SuppressWarnings("unchecked")
                    Class<? extends Page> pageClass = (Class<? extends Page>) clazz;
                    mountPointRegistration.addMountPoint(mountPoint.mountPoint(), pageClass);
                    mountPointRegistration.register();
                    pageMounter.add(mountPointRegistration);
                    LOGGER.info("Mounting page {} at {}", clazz.getName(), mountPoint.mountPoint());
                }
            }
            synchronized (mountPointRegistrations) {
                if (mountPointRegistrations.containsKey(symbolicName)) {
                    removeBundle(bundle);
                }
                mountPointRegistrations.put(bundle.getID(), pageMounter);
            }
        } else {
            LOGGER.debug("Ignore bundle " + symbolicName + " for PageMounting.");
        }

    }

    public void removeBundle(ExtendedBundle bundle) {
        List<DefaultPageMounter> registrations;
        synchronized (mountPointRegistrations) {
            registrations = mountPointRegistrations.remove(bundle.getID());
        }
        if (registrations == null) {
            return;
        }
        for (DefaultPageMounter pageMounter : registrations) {
            pageMounter.dispose();
        }

    }

}
