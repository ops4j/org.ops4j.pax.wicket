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

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.Constants;
import org.osgi.util.tracker.BundleTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaxWicketBundleListener implements BundleTrackerCustomizer {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketBundleListener.class);

    private BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker;

    public PaxWicketBundleListener(BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker) {
        this.bundleDelegatingExtensionTracker = bundleDelegatingExtensionTracker;
    }

    private static boolean isBundleRelavantForPaxWicket(Bundle bundle) {
        String importedPackages = (String) bundle.getHeaders().get(Constants.IMPORT_PACKAGE);
        LOGGER.trace("Checking {} for import of org.apache.wicket.*", bundle.getSymbolicName());
        if (importedPackages == null) {
            LOGGER.info("Bundle {} does not contain any imported packages --> ignore", bundle.getSymbolicName());
            return false;
        }
        if (importedPackages.contains("org.apache.wicket")) {
            LOGGER.debug("Bundle {} contains imports of org.apache.wicket", bundle.getSymbolicName());
            return true;
        }
        LOGGER.debug("Bundle {} does NOT contain imports of org.apache.wicket", bundle.getSymbolicName());
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.BundleTrackerCustomizer#addingBundle(org.osgi.framework.Bundle,
     * org.osgi.framework.BundleEvent)
     */
    public Object addingBundle(Bundle bundle, BundleEvent event) {
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
    public void modifiedBundle(Bundle bundle, BundleEvent event, Object object) {
        // we don't care about state changes (for now)
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.osgi.util.tracker.BundleTrackerCustomizer#removedBundle(org.osgi.framework.Bundle,
     * org.osgi.framework.BundleEvent, java.lang.Object)
     */
    public void removedBundle(Bundle bundle, BundleEvent event, Object object) {
        if (isBundleRelavantForPaxWicket(bundle)) {
            bundleDelegatingExtensionTracker.removeRelevantBundle(bundle);
            LOGGER.debug("{} is removed as a relevant bundle for pax wicket", bundle.getSymbolicName());
        }
    }

}
