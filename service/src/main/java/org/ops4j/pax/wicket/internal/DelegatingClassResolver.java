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

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.osgi.framework.Constants.OBJECTCLASS;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.application.IClassResolver;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DelegatingClassResolver implements IClassResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingClassResolver.class);

    private final BundleContext context;
    private final String applicationName;
    private final List<IClassResolver> resolvers;

    private ClassResolverTracker tracker;

    public DelegatingClassResolver(BundleContext context, String applicationName) throws IllegalArgumentException {
        validateNotNull(context, "context");
        validateNotEmpty(applicationName, "applicationName");
        this.context = context;
        this.applicationName = applicationName;
        resolvers = new ArrayList<IClassResolver>();
    }

    public final void intialize() throws IllegalStateException {
        synchronized (this) {
            if (tracker != null) {
                throw new IllegalStateException(
                    "DelegatingClassResolver [" + this + "] had been initialized.");
            }
            tracker = new ClassResolverTracker(context, applicationName);
            tracker.open();
        }
    }

    public void dispose() throws IllegalStateException {
        synchronized (this) {
            if (tracker == null) {
                throw new IllegalStateException(
                    "DelegatingClassResolver [" + this + "] had not been initialized.");
            }
            tracker.close();
            tracker = null;
        }
    }

    public Class<?> resolveClass(final String classname) throws ClassNotFoundException {
        synchronized (resolvers) {
            LOGGER.trace("Try to resolve {} from {} resolvers", classname, resolvers.size());
            for (IClassResolver resolver : resolvers) {
                try {
                    Class<?> candidate = resolver.resolveClass(classname);
                    if (candidate != null) {
                        return candidate;
                    }
                } catch (ClassNotFoundException e) {
                    LOGGER.info("ClassResolver {} could not find class: {}", resolver, classname);
                } catch (RuntimeException e) {
                    LOGGER.warn("ClassResolver {} threw an unexpected exception.", resolver, e);
                }
            }
        }
        throw new ClassNotFoundException(String.format("Class [%s] can't be resolved.", classname));
    }

    public Iterator<URL> getResources(String name) {
        synchronized (resolvers) {
            for (IClassResolver resolver : resolvers) {
                try {
                    Iterator<URL> iterator = resolver.getResources(name);
                    if (iterator != null && iterator.hasNext()) {
                        return iterator;
                    }
                } catch (RuntimeException e) {
                    LOGGER.warn("ClassResolver {} threw an unexpected exception.", resolver, e);
                    return Collections.<URL> emptyList().iterator();
                }
            }
            return Collections.<URL> emptyList().iterator();
        }
    }

    private final class ClassResolverTracker extends ServiceTracker {

        private final String m_applicationName;

        ClassResolverTracker(BundleContext context, String applicationName) {
            super(context, createFilter(context, applicationName), null);
            m_applicationName = applicationName;
        }

        @Override
        public final Object addingService(ServiceReference reference) {
            IClassResolver resolver = (IClassResolver) super.addingService(reference);
            synchronized (resolvers) {
                resolvers.add(resolver);
            }
            return resolver;
        }

        @Override
        public final void modifiedService(ServiceReference reference, Object service) {
            Object objAppName = reference.getProperty(APPLICATION_NAME);
            if (objAppName != null) {
                Class<?> nameClass = objAppName.getClass();
                if (String.class.isAssignableFrom(nameClass)) {
                    if (!nameClass.isArray()) {
                        String appName = (String) objAppName;
                        if (m_applicationName.equals(appName)) {
                            return;
                        }
                    } else {
                        String[] appNames = (String[]) objAppName;
                        for (String appName : appNames) {
                            if (m_applicationName.equals(appName)) {
                                return;
                            }
                        }
                    }
                }
            }
            removedService(reference, service);
        }

        @Override
        public final void removedService(ServiceReference reference, Object service) {
            IClassResolver resolver = (IClassResolver) service;
            synchronized (resolvers) {
                resolvers.remove(resolver);
            }
            super.removedService(reference, service);
        }
    }

    private static Filter createFilter(BundleContext context, String applicationName) {
        String filterStr = "(&(" + OBJECTCLASS + "=" + IClassResolver.class.getName() + ")(" + APPLICATION_NAME + "="
                + applicationName + "))";
        try {
            return context.createFilter(filterStr);
        } catch (InvalidSyntaxException e) {
            String message = APPLICATION_NAME + "[" + applicationName + "] has an invalid format. ";
            throw new IllegalArgumentException(message);
        }
    }
}
