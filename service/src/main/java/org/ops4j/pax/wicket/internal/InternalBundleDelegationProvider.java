package org.ops4j.pax.wicket.internal;

import org.osgi.framework.Bundle;

/**
 * This is quite a simple interface marking all BundleDelegating loaders used by the BundleDelegatingExtensionTracker
 * helping to provide an internal standard of methods for such classes.
 * 
 * An additional important remark: Non of those classes implementing this interface have to handle synchronized
 * themselves, but have to be rather synchronized by the caller!
 */
public interface InternalBundleDelegationProvider {

    /**
     * Each {@link InternalBundleDelegationProvider} has to know the applicationName it is registered for. This method
     * returns the application name.
     */
    String getApplicationName();

    /**
     * In this method the component has to start itself. It can either register a service or do any other operations.
     * Please keep in mind that {@link #addBundle(Bundle)} and {@link #removeBundle(Bundle)} couldn't be called before
     * this method is called and are likely to throw an {@link IllegalStateException} otherwise.
     */
    void start();

    /**
     * In this method the component has to stop itself. It can unregister services or do any other operations for tear
     * down. Please keep in mind that neither the {@link #addBundle(Bundle)} nor the {@link #removeBundle(Bundle)} are
     * likely to work after this method is called and will throw an {@link IllegalStateException}.
     */
    void stop();

    /**
     * Adds a bundle which should be used for delegation. This will thrown an {@link IllegalStateException} in case the
     * {@link #start()} method had not been called.
     */
    void addBundle(Bundle bundle);

    /**
     * Removes a bundle which shouldn't be used any longer for delegation. This will throw an
     * {@link IllegalStateException} in case the {@link #start()} method had not been called already. If you try to
     * remove a bundle not added by now nothing happens.
     */
    void removeBundle(Bundle bundle);

}
