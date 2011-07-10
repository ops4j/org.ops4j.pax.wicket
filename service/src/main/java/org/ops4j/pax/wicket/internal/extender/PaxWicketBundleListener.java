package org.ops4j.pax.wicket.internal.extender;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.BundleListener;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaxWicketBundleListener implements BundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketBundleListener.class);

    private BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker;

    public PaxWicketBundleListener(BundleDelegatingExtensionTracker bundleDelegatingExtensionTracker) {
        this.bundleDelegatingExtensionTracker = bundleDelegatingExtensionTracker;
    }

    public void bundleChanged(BundleEvent event) {
        if (!isBundleRelavantForPaxWicket(event.getBundle())) {
            return;
        }
        if (BundleEvent.STARTED == event.getType()) {
            LOGGER.info("{} is STARTED and relevant for pax wicket", event.getBundle().getSymbolicName());
            bundleDelegatingExtensionTracker.addRelevantBundle(event.getBundle());
            return;
        } else if (BundleEvent.STOPPED == event.getType()) {
            LOGGER.debug("{} is STOPPING relevant for pax wicket", event.getBundle().getSymbolicName());
            bundleDelegatingExtensionTracker.removeRelevantBundle(event.getBundle());
            return;
        }
        LOGGER.debug("{} is in no relevant state for pax wicket", event.getBundle().getSymbolicName());
        return;
    }

    private boolean isBundleRelavantForPaxWicket(Bundle bundle) {
        String importedPackages = (String) bundle.getHeaders().get(Constants.IMPORT_PACKAGE);
        LOGGER.trace("Checking {} for import of org.apache.wicket.*", bundle.getSymbolicName());
        if (importedPackages == null) {
            LOGGER.info("Bundle {} does not contain any imported packages --> ignore");
            return false;
        }
        if (importedPackages.contains("org.apache.wicket")) {
            LOGGER.debug("Bundle {} contains imports of org.apache.wicket", bundle.getSymbolicName());
            return true;
        }
        LOGGER.debug("Bundle {} does NOT contain imports of org.apache.wicket", bundle.getSymbolicName());
        return false;
    }

}
