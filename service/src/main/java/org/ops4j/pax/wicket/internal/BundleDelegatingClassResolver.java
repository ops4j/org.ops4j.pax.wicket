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

import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Iterator;

import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We assume that all bundles exporting a service implementing at least one of the following interfaces should also be
 * able to be searched for classes: {@link ContentSource}, {@link ContentAggregator}, {@link PageFactory} and
 * {@link PaxWicketApplicationFactory}.
 */
public class BundleDelegatingClassResolver extends ServiceTracker implements IClassResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingClassResolver.class);

    private static final String FILTER = "(|" +
            "(objectClass=" + ContentSource.class.getName() + ")" +
            "(objectClass=" + ContentAggregator.class.getName() + ")" +
            "(objectClass=" + PageFactory.class.getName() + ")" +
            "(objectClass=" + PaxWicketApplicationFactory.class.getName() + ")" +
            ")";

    private HashSet<Bundle> bundles;
    private final String applicationName;
    private final Bundle paxWicketBundle;

    public BundleDelegatingClassResolver(BundleContext context, String applicationName, Bundle paxWicketBundle) {
        super(context, createFilter(context), null);

        this.applicationName = applicationName;
        this.paxWicketBundle = paxWicketBundle;
        bundles = new HashSet<Bundle>();
        bundles.add(paxWicketBundle);

        open(true);
    }

    public Class<?> resolveClass(String classname) throws ClassNotFoundException {
        LOGGER.trace("Trying to resolve class {} from BundleDelegatingClassResolver", classname);
        for (Bundle bundle : bundles) {
            try {
                LOGGER.trace("Trying to load class {} from bundle {}", classname, bundle.getSymbolicName());
                Class<?> loadedClass = bundle.loadClass(classname);
                LOGGER.debug("Loaded class {} from bundle {}", classname, bundle.getSymbolicName());
                return loadedClass;
            } catch (ClassNotFoundException e) {
                LOGGER.trace("Could not load class {} from bundle {} because bundle does not contain the class",
                    classname, bundle.getSymbolicName());
            } catch (IllegalStateException e) {
                LOGGER.trace("Could not load class {} from bundle {} because bundle had been uninstalled", classname,
                    bundle.getSymbolicName());
            }
        }
        throw new ClassNotFoundException("Class [" + classname + "] can't be resolved.");
    }

    /**
     * Untested!! Currently, tests are broken in pax-wicket.
     */
    @SuppressWarnings("unchecked")
    public Iterator<URL> getResources(String name) {
        try {
            for (Bundle bundle : bundles) {
                final Enumeration<URL> enumeration = bundle.getResources(name);
                if (null != enumeration && enumeration.hasMoreElements()) {
                    return new EnumerationAdapter<URL>(enumeration);
                }
            }
        } catch (IOException e) {
            return Collections.<URL> emptyList().iterator();
        }
        return Collections.<URL> emptyList().iterator();
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object addingService(ServiceReference serviceReference) {
        String appName = (String) serviceReference.getProperty(APPLICATION_NAME);
        if (!applicationName.equals(appName)) {
            LOGGER.debug("Applicationname {} does not match service application name {}", appName, applicationName);
            return null;
        }
        LOGGER.info("Adding bundle {} to DelegatingClassLoader", serviceReference.getBundle().getSymbolicName());
        synchronized (this) {
            Bundle bundle = serviceReference.getBundle();
            HashSet<Bundle> clone = (HashSet<Bundle>) bundles.clone();
            clone.add(bundle);
            bundles = clone;
        }
        return super.addingService(serviceReference);
    }

    @Override
    public void removedService(ServiceReference serviceReference, Object o) {
        String appName = (String) serviceReference.getProperty(APPLICATION_NAME);
        if (!applicationName.equals(appName)) {
            LOGGER.debug("Applicationname {} does not match service application name {}", appName, applicationName);
            return;
        }
        HashSet<Bundle> revisedSet = new HashSet<Bundle>();
        revisedSet.add(paxWicketBundle);
        try {
            LOGGER.info("Removing bundle {} to DelegatingClassLoader", serviceReference.getBundle().getSymbolicName());
            synchronized (this) {
                ServiceReference[] serviceReferences = context.getAllServiceReferences(null, FILTER);
                if (serviceReferences != null) {
                    for (ServiceReference ref : serviceReferences) {
                        revisedSet.add(ref.getBundle());
                    }
                }
                bundles = revisedSet;
            }
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException(String.format("Filter %s have to be accepted", FILTER), e);
        }
        super.removedService(serviceReference, o);
    }

    private static Filter createFilter(BundleContext context) {
        try {
            return context.createFilter(FILTER);
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException(
                String.format("Unexpected behavior! The filter %s should not fail", FILTER), e);
        }
    }
}
