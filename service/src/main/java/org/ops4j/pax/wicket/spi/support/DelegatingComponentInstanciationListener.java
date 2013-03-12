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
package org.ops4j.pax.wicket.spi.support;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.osgi.framework.Constants.OBJECTCLASS;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.cglib.proxy.Factory;

import org.ops4j.pax.wicket.api.InjectorHolder;
import org.ops4j.pax.wicket.api.PaxWicketInjector;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DelegatingComponentInstanciationListener extends AbstractPaxWicketInjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingComponentInstanciationListener.class);

    private final BundleContext context;
    private final String applicationName;
    private final List<PaxWicketInjector> resolvers;

    private ComponentInstanciationListenerTracker tracker;

    public DelegatingComponentInstanciationListener(BundleContext context, String applicationName)
        throws IllegalArgumentException {
        validateNotNull(context, "context");
        validateNotEmpty(applicationName, "applicationName");
        this.context = context;
        this.applicationName = applicationName;
        resolvers = new ArrayList<PaxWicketInjector>();

        InjectorHolder.setInjector(applicationName, this);
    }

    public final void intialize() throws IllegalStateException {
        synchronized (this) {
            if (tracker != null) {
                throw new IllegalStateException(
                    "DelegatingComponentInstanciationListener [" + this + "] had been initialized.");
            }
            tracker = new ComponentInstanciationListenerTracker(context, applicationName);
            tracker.open();
        }
    }

    public void dispose() throws IllegalStateException {
        synchronized (this) {
            if (tracker == null) {
                throw new IllegalStateException(
                    "DelegatingComponentInstanciationListener [" + this + "] had not been initialized.");
            }
            tracker.close();
            tracker = null;
        }
    }

    public void inject(Object toInject, Class<?> toHandle) {
        Set<String> foundAnnotation = countComponentContainPaxWicketBeanAnnotatedFieldsHierachical(toHandle);
        if (foundAnnotation.isEmpty()) {
            LOGGER.trace("Component {} doesn't contain any PaxWicketBean fields. Therefore ignore", toInject
                .getClass().getName());
            return;
        }
        Set<String> handledAnnotations = new HashSet<String>();
        synchronized (resolvers) {
            Class<?> currentAnalysingClass = toHandle;
            boolean handledFactory = false;
            if (Factory.class.isInstance(toInject)) {
                handledFactory = true;
            }
            while (!isBoundaryClass(currentAnalysingClass)) {
                for (PaxWicketInjector listener : resolvers) {
                    try {
                        listener.inject(toInject, currentAnalysingClass);
                        // if we reach here the bean had been injected correctly
                        if (handledFactory) {
                            handledAnnotations.addAll(
                                countComponentContainPaxWicketBeanAnnotatedOneLevel(currentAnalysingClass
                                    .getSuperclass()));
                        } else {
                            handledAnnotations.addAll(
                                countComponentContainPaxWicketBeanAnnotatedOneLevel(currentAnalysingClass));
                        }
                        // once we've found it we could take the next level
                        break;
                    } catch (IllegalStateException e) {
                        // well, not found... retry with the next listener
                    }
                }
                currentAnalysingClass = currentAnalysingClass.getSuperclass();
                if (handledFactory) {
                    currentAnalysingClass = currentAnalysingClass.getSuperclass();
                    handledFactory = false;
                }
            }
        }
        if (handledAnnotations.size() != foundAnnotation.size()) {
            throw new IllegalStateException(String.format(
                "For Component %s %d %s fields should be injected but only %d %s had been injected.", toInject
                    .getClass()
                    .getName(),
                    foundAnnotation.size(),
                Arrays.toString(foundAnnotation.toArray(new String[0])),
                handledAnnotations.size(),
                Arrays.toString(handledAnnotations.toArray(new String[0]))));
        }
    }

    private final class ComponentInstanciationListenerTracker extends
            ServiceTracker<PaxWicketInjector, PaxWicketInjector> {

        private final String m_applicationName;

        ComponentInstanciationListenerTracker(BundleContext context, String applicationName) {
            super(context, createFilter(context, applicationName), null);
            m_applicationName = applicationName;
        }

        @Override
        public final PaxWicketInjector addingService(ServiceReference<PaxWicketInjector> reference) {
            PaxWicketInjector resolver = super.addingService(reference);
            synchronized (resolvers) {
                resolvers.add(resolver);
            }
            return resolver;
        }

        @Override
        public final void modifiedService(ServiceReference<PaxWicketInjector> reference, PaxWicketInjector service) {
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
        public final void removedService(ServiceReference<PaxWicketInjector> reference, PaxWicketInjector service) {
            PaxWicketInjector resolver = service;
            synchronized (resolvers) {
                resolvers.remove(resolver);
            }
            super.removedService(reference, service);
        }
    }

    private static Filter createFilter(BundleContext context, String applicationName) {
        String filterStr =
            "(&(" + OBJECTCLASS + "=" + PaxWicketInjector.class.getName() + ")(" + APPLICATION_NAME + "="
                    + applicationName + "))";
        try {
            return context.createFilter(filterStr);
        } catch (InvalidSyntaxException e) {
            String message = APPLICATION_NAME + "[" + applicationName + "] has an invalid format. ";
            throw new IllegalArgumentException(message);
        }
    }

}
