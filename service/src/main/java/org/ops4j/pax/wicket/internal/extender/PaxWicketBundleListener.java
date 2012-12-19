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
package org.ops4j.pax.wicket.internal.extender;

import org.ops4j.pax.wicket.internal.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaxWicketBundleListener implements BundleTrackerCustomizer<Bundle> {

    /**
     * 
     */
    private static final String APACHE_WICKET_NAMESPACE = "org.apache.wicket";

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketBundleListener.class);

    private final BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker;

    public PaxWicketBundleListener(BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker) {
        this.bundleDelegatingExtensionTracker = bundleDelegatingExtensionTracker;
    }

    private static boolean isBundleRelavantForPaxWicket(Bundle bundle) {
        LOGGER.trace("Checking {} for import of org.apache.wicket.*", bundle.getSymbolicName());
        if (hasImportPackage(bundle) || hasRequireBundle(bundle)) {
            return true;
        } else {
            LOGGER.debug("Bundle {} does NOT contain imports of org.apache.wicket", bundle.getSymbolicName());
            return false;
        }
    }

    private static boolean hasRequireBundle(Bundle bundle) {
        String requireBundle = bundle.getHeaders().get(Constants.REQUIRE_BUNDLE);
        if (requireBundle != null
                && (requireBundle.contains(APACHE_WICKET_NAMESPACE) || requireBundle.contains(Activator.SYMBOLIC_NAME))) {
            // TODO: We better should parse this (see comments in hasImportPackage)
            LOGGER.debug("Bundle {} contains require-bundle of org.apache.wicket", bundle.getSymbolicName());
            LOGGER
                .info(
                    "Bundle {} uses require-bundle to import wicket, this style is discouraged see http://wiki.osgi.org/wiki/Require-Bundle",
                    bundle.getSymbolicName());
            return true;
        }
        return false;
    }

    private static boolean hasImportPackage(Bundle bundle) {
        String importedPackages = bundle.getHeaders().get(Constants.IMPORT_PACKAGE);
        if (importedPackages != null && importedPackages.contains(APACHE_WICKET_NAMESPACE)) {
            // TODO: We better should parse the String, this could be confused by use clause or similar!
            // Is there any OSGi Util for this? Maybe use the PackageAdmin instead!
            LOGGER.debug("Bundle {} contains package-imports of org.apache.wicket", bundle.getSymbolicName());
            return true;
        }
        // we can consider dynamic imports here...
        String dynamicImport = bundle.getHeaders().get(Constants.DYNAMICIMPORT_PACKAGE);
        if (dynamicImport != null && dynamicImport.contains(APACHE_WICKET_NAMESPACE)) {
            // TODO: in fact we have to check for *, org.* and org.apache.* ...
            // TODO: We better should parse the String, this could be confused by use clause or similar!
            // Is there any OSGi Util for this? Maybe use the PackageAdmin instead!
            LOGGER.debug("Bundle {} contains package-imports of org.apache.wicket", bundle.getSymbolicName());
            return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.BundleTrackerCustomizer#addingBundle(org.osgi.framework.Bundle,
     * org.osgi.framework.BundleEvent)
     */
    public Bundle addingBundle(Bundle bundle, BundleEvent event) {
        if (isBundleRelavantForPaxWicket(bundle)) {
            LOGGER.info("{} is added as a relevant bundle for pax wicket", bundle.getSymbolicName());
            bundleDelegatingExtensionTracker.addRelevantBundle(bundle);
        }
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.BundleTrackerCustomizer#modifiedBundle(org.osgi.framework.Bundle,
     * org.osgi.framework.BundleEvent, java.lang.Object)
     */
    public void modifiedBundle(Bundle bundle, BundleEvent event, Bundle object) {
        // we don't care about state changes (for now)
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.BundleTrackerCustomizer#removedBundle(org.osgi.framework.Bundle,
     * org.osgi.framework.BundleEvent, java.lang.Object)
     */
    public void removedBundle(Bundle bundle, BundleEvent event, Bundle object) {
        if (isBundleRelavantForPaxWicket(bundle)) {
            bundleDelegatingExtensionTracker.removeRelevantBundle(bundle);
            LOGGER.debug("{} is removed as a relevant bundle for pax wicket", bundle.getSymbolicName());
        }
    }

}
