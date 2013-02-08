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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.internal.extender.ExtendedBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class represents an extended class loader automatically trying to load from all bundles added to it.
 */
public class BundleDelegatingClassResolver implements IClassResolver, InternalBundleDelegationProvider {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingClassResolver.class);

    private final String applicationName;
    private final BundleContext paxWicketBundleContext;
    private final Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    private ServiceRegistration<IClassResolver> classResolverRegistration;

    public BundleDelegatingClassResolver(BundleContext paxWicketBundleContext, String applicationName) {
        this.paxWicketBundleContext = paxWicketBundleContext;
        this.applicationName = applicationName;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void start() {
        if (classResolverRegistration != null) {
            throw new IllegalStateException("Service is already registered");
        }
        Dictionary<String, String> properties = new Hashtable<String, String>();
        properties.put(Constants.APPLICATION_NAME, applicationName);
        classResolverRegistration =
            paxWicketBundleContext.registerService(IClassResolver.class, this, properties);
    }

    public void stop() {
        if (classResolverRegistration == null) {
            LOGGER.warn("Trying to unregister service although not registered");
            return;
        }
        classResolverRegistration.unregister();
    }

    public void addBundle(ExtendedBundle bundle) {
        if (classResolverRegistration == null) {
            throw new IllegalStateException("The service is stoped and no more bundles could be added");
        }
        synchronized (bundles) {
            bundles.put(bundle.getBundle().getSymbolicName(), bundle.getBundle());
        }
    }

    public void removeBundle(ExtendedBundle bundle) {
        if (classResolverRegistration == null) {
            throw new IllegalStateException("The service is stoped and no more bundles could be removed");
        }
        synchronized (bundles) {
            bundles.remove(bundle.getBundle().getSymbolicName());
        }
    }

    public Class<?> resolveClass(String classname) throws ClassNotFoundException {
        LOGGER.trace("Trying to resolve class {} from BundleDelegatingClassResolver", classname);
        synchronized (bundles) {
            Collection<Bundle> values = bundles.values();
            for (Bundle bundle : values) {
                try {
                    LOGGER.trace("Trying to load class {} from bundle {}", classname, bundle.getSymbolicName());
                    Class<?> loadedClass = bundle.loadClass(classname);
                    LOGGER.debug("Loaded class {} from bundle {}", classname, bundle.getSymbolicName());
                    return loadedClass;
                } catch (ClassNotFoundException e) {
                    LOGGER.trace("Could not load class {} from bundle {} because bundle does not contain the class",
                        classname, bundle.getSymbolicName());
                } catch (IllegalStateException e) {
                    LOGGER.trace("Could not load class {} from bundle {} because bundle had been uninstalled",
                        classname,
                        bundle.getSymbolicName());
                }
            }
        }
        throw new ClassNotFoundException("Class [" + classname + "] can't be resolved.");
    }

    public Iterator<URL> getResources(String name) {
        ArrayList<URL> collectedResources = new ArrayList<URL>();
        try {
            synchronized (bundles) {
                Collection<Bundle> values = bundles.values();
                for (Bundle bundle : values) {
                    final Enumeration<URL> enumeration = bundle.getResources(name);
                    if (enumeration == null) {
                        continue;
                    }
                    while (enumeration.hasMoreElements()) {
                        collectedResources.add(enumeration.nextElement());
                    }
                }
            }
        } catch (IOException e) {
            LOGGER.warn("IO exception during reading resources from bundle; returning current state.");
            collectedResources.iterator();
        }
        return collectedResources.iterator();
    }

    /**
     * This method is uses only for some internal wicket stuff if the IClassResolver is NOT replaced and in some IOC
     * stuff also not used by pax wicket. Therefore this method should never ever be called. If it is though we want to
     * be informed about the problem as soon as possible.
     */
    public ClassLoader getClassLoader() {
        throw new UnsupportedOperationException("This method should NOT BE CALLED!");
    }

}
