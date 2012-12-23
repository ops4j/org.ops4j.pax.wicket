/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal.injection.registry;

import java.util.Arrays;

import org.ops4j.pax.wicket.api.NoBeanAvailableForInjectionException;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.spi.ProxyTarget;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ReleasableProxyTarget;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class locates the ProxyTarget through the OSGi service registry. It will locate an arbitary service by the
 * service class if the bean name is not specified. If a bean name is specified, it tries to locate a Declarative
 * Component with the given name and service interface
 * 
 * @author Christoph Läubrich
 * 
 */
public class OSGiServiceRegistryProxyTargetLocator implements ProxyTargetLocator {

    private static final Logger LOGGER = LoggerFactory.getLogger(OSGiServiceRegistryProxyTargetLocator.class);

    private static final long serialVersionUID = -5726156325232163363L;
    private final BundleContext bundleContext;
    private final String componentName;

    private final String serviceInterface;

    private final Class<?> parent;

    /**
     * @param pageClass
     * @param serviceClass
     * 
     */
    public OSGiServiceRegistryProxyTargetLocator(BundleContext paxBundleContext, PaxWicketBean annotation,
            Class<?> serviceClass, Class<?> pageClass) {
        this.parent = pageClass;
        if (pageClass.getClassLoader() instanceof BundleReference) {
            // Fetch the Bundlecontext of the page class to locate the service
            BundleReference reference = (BundleReference) pageClass.getClassLoader();
            bundleContext = reference.getBundle().getBundleContext();
            LOGGER.debug("Using the Bundlereference of class {} for locating services", pageClass);
        } else {
            bundleContext = paxBundleContext;
            LOGGER.debug("Using the PAX Wicket BundlereContext for locating services");
        }
        componentName = annotation.name();
        serviceInterface = serviceClass.getName();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator#locateProxyTarget()
     */
    public ReleasableProxyTarget locateProxyTarget() {
        boolean hasComponentName = componentName != null && !componentName.trim().equals("");
        String filter;
        if (hasComponentName) {
            filter = String.format("(%s=%s)", "component.name", componentName);
        } else {
            filter = null;
        }
        try {
            LOGGER.debug("Try to locate a suitable service for objectClass = "
                    + serviceInterface + " and filter = " + filter);
            ServiceReference<?>[] references = bundleContext.getAllServiceReferences(serviceInterface, filter);
            if (references != null) {
                // Sort the references...
                Arrays.sort(references);
                // Fetch the first (if any)...
                for (final ServiceReference<?> reference : references) {
                    final Object service = bundleContext.getService(reference);
                    if (service == null) {
                        // The service is gone while we where iterating over the service references...
                        continue;
                    }
                    // And return a releasable proxy target...
                    return new ReleasableProxyTargetImplementation(service, reference);
                }
            }
        } catch (InvalidSyntaxException e) {
            LOGGER.error("Creation of filter failed: {}", e.getMessage(), e);
            throw new RuntimeException("Creation of filter failed", e);
        }
        throw new NoBeanAvailableForInjectionException("can't find any service matching objectClass = "
                + serviceInterface + " and filter = " + filter);
    }

    /**
     * A releasable Proxy Target for a specific Service Reference
     * 
     * @author Christoph Läubrich
     */
    private final class ReleasableProxyTargetImplementation implements ReleasableProxyTarget {
        /**
         * the service object
         */
        private Object service;
        /**
         * the reference which produced it
         */
        private final ServiceReference<?> reference;
        private ReleasableProxyTarget delegatingProxy;

        /**
         * @param service
         * @param reference
         */
        private ReleasableProxyTargetImplementation(Object service, ServiceReference<?> reference) {
            this.service = service;
            this.reference = reference;
        }

        public synchronized Object getTarget() throws NoBeanAvailableForInjectionException {
            if (service == null) {
                // The service was released before, try to reaquire it...
                LOGGER.debug("Try to reaquire service...");
                Object reaquiredService = bundleContext.getService(reference);
                if (reaquiredService != null) {
                    // Successfull reaquired!
                    LOGGER.debug("reaquire service was successfull");
                    service = reaquiredService;
                } else {
                    LOGGER.debug("reaquire service was not successfull, try to relocate to a different service...");
                    // Try to find a new one...
                    ReleasableProxyTarget newProxyTarget = locateProxyTarget();
                    // If we are here a new ProxyTarget was bound
                    delegatingProxy = newProxyTarget;
                    // Fetch the target from the delegate
                    service = newProxyTarget.getTarget();
                }
            }
            return service;
        }

        public synchronized ProxyTarget releaseTarget() {
            // When releasing, we unget the service so we don't keep stale references...
            service = null;
            if (delegatingProxy != null) {
                // return the delegate proxy for further usage...
                return delegatingProxy.releaseTarget();
            } else {
                try {
                    bundleContext.ungetService(reference);
                } catch (RuntimeException e) {
                    // Sometimes a RuntimeException might occur here, we catch it to not prevent any other
                    // cleanup actions
                    LOGGER.trace("RuntimeException while ungetting service", e);
                }
                return this;
            }
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator#getParent()
     */
    public Class<?> getParent() {
        return parent;
    }
}
