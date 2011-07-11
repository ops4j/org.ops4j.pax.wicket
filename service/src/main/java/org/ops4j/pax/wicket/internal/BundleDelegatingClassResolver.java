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
import java.util.Collection;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;

import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.api.Constants;
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
    private Map<String, Bundle> bundles = new HashMap<String, Bundle>();
    private ServiceRegistration classResolverRegistration;

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
            paxWicketBundleContext.registerService(IClassResolver.class.getName(), this, properties);
    }

    public void stop() {
        if (classResolverRegistration == null) {
            LOGGER.warn("Trying to unregister service although not registered");
            return;
        }
        classResolverRegistration.unregister();
    }

    public void addBundle(Bundle bundle) {
        if (classResolverRegistration == null) {
            throw new IllegalStateException("The service is stoped and no more bundles could be added");
        }
        bundles.put(bundle.getSymbolicName(), bundle);
    }

    public void removeBundle(Bundle bundle) {
        if (classResolverRegistration == null) {
            throw new IllegalStateException("The service is stoped and no more bundles could be removed");
        }
        bundles.remove(bundle.getSymbolicName());
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

    @SuppressWarnings("unchecked")
    public Iterator<URL> getResources(String name) {
        try {
            synchronized (bundles) {
                Collection<Bundle> values = bundles.values();
                for (Bundle bundle : values) {
                    final Enumeration<URL> enumeration = bundle.getResources(name);
                    if (null != enumeration && enumeration.hasMoreElements()) {
                        return new EnumerationAdapter<URL>(enumeration);
                    }
                }
            }
        } catch (IOException e) {
            return Collections.<URL> emptyList().iterator();
        }
        return Collections.<URL> emptyList().iterator();
    }

}
