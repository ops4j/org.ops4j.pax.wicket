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
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.osgi.framework.Constants.OBJECTCLASS;

import java.lang.reflect.Field;
import java.util.concurrent.ConcurrentLinkedQueue;

import net.sf.cglib.proxy.Factory;

import org.apache.wicket.Component;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.ops4j.pax.wicket.api.NoBeanAvailableForInjectionException;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class DelegatingComponentInstanciationListener implements IComponentInstantiationListener, Injector {

    private static final Logger LOGGER = LoggerFactory.getLogger(DelegatingComponentInstanciationListener.class);

    private final BundleContext context;
    private final String applicationName;
    private final ConcurrentLinkedQueue<IComponentInstantiationListener> resolvers;

    private ComponentInstanciationListenerTracker tracker;

    public DelegatingComponentInstanciationListener(BundleContext context, String applicationName)
        throws IllegalArgumentException {
        validateNotNull(context, "context");
        validateNotEmpty(applicationName, "applicationName");
        this.context = context;
        this.applicationName = applicationName;
        resolvers = new ConcurrentLinkedQueue<IComponentInstantiationListener>();
        
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

    public void inject(Object object) {
        boolean foundAnnotation = doesComponentContainPaxWicketBeanAnnotatedFields(object);
        if (!foundAnnotation) {
            LOGGER.trace("Component {} doesn ot contain any PaxWicketBean fields. Therefore ignore", object
                .getClass().getName());
            return;
        }
        for (IComponentInstantiationListener listener : resolvers) {
            try {
                if (object instanceof Component) {
                    listener.onInstantiation((Component) object);
                } else if (listener instanceof Injector) {
                    ((Injector) listener).inject(listener);
                }
                // if we reach here the bean had been injected correctly and we're happy...
                return;
            } catch (NoBeanAvailableForInjectionException e) {
                // well, not found... retry with the next listener
            }
        }
        throw new NoBeanAvailableForInjectionException(String.format(
            "Component %s has fields to inject but noone provides the beans for them", object.getClass().getName()));
    }
    
    public void onInstantiation(Component component) {
        inject(component);
    }
    
    private boolean doesComponentContainPaxWicketBeanAnnotatedFields(Object component) {
        Class<?> realClass = component.getClass();
        if (Factory.class.isInstance(component)) {
            realClass = realClass.getSuperclass();
        }
        Field[] declaredFields = realClass.getDeclaredFields();
        boolean found = false;
        for (Field field : declaredFields) {
            if (field.isAnnotationPresent(PaxWicketBean.class)) {
                found = true;
                break;
            }
        }
        return found;
    }

    private final class ComponentInstanciationListenerTracker extends ServiceTracker {

        private final String m_applicationName;

        ComponentInstanciationListenerTracker(BundleContext context, String applicationName) {
            super(context, createFilter(context, applicationName), null);
            m_applicationName = applicationName;
        }

        @Override
        public final Object addingService(ServiceReference reference) {
            IComponentInstantiationListener resolver = (IComponentInstantiationListener) super.addingService(reference);
            synchronized (DelegatingComponentInstanciationListener.this) {
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
            IComponentInstantiationListener resolver = (IComponentInstantiationListener) service;
            synchronized (DelegatingComponentInstanciationListener.this) {
                resolvers.remove(resolver);
            }
            super.removedService(reference, service);
        }
    }

    private static Filter createFilter(BundleContext context, String applicationName) {
        String filterStr =
            "(&(" + OBJECTCLASS + "=" + IComponentInstantiationListener.class.getName() + ")(" + APPLICATION_NAME + "="
                           + applicationName + "))";
        try {
            return context.createFilter(filterStr);
        } catch (InvalidSyntaxException e) {
            String message = APPLICATION_NAME + "[" + applicationName + "] has an invalid format. ";
            throw new IllegalArgumentException(message);
        }
    }

}
