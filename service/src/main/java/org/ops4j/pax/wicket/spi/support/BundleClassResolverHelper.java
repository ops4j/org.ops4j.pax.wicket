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

import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;

import org.apache.wicket.application.IClassResolver;
import org.ops4j.pax.wicket.internal.EnumerationAdapter;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * {@code BundleClassResolverHelper} is a helper to register {@code IClassResolver}.
 */
public final class BundleClassResolverHelper {

    private static final String[] SERVICE_NAMES =
    {
        IClassResolver.class.getName(),
        ManagedService.class.getName()
    };

    private final BundleContext bundleContext;
    private final Hashtable<String, Object> serviceProperties;

    private final Object lock = new Object();
    private ServiceRegistration<?> serviceRegistration;

    /**
     * Construct an instance of {@code BundleClassResolver}.
     */
    public BundleClassResolverHelper(BundleContext bundleContext) throws IllegalArgumentException {
        validateNotNull(bundleContext, "bundle");
        this.bundleContext = bundleContext;
        serviceProperties = new Hashtable<String, Object>();
    }

    /**
     * Sets the service pid of this {@code BundleClassResolverHelper} instance. This is useful if this class resolver
     * needs to be wired to multiple pax-wicket applications.
     */
    public final void setServicePid(String servicePid) {
        synchronized (lock) {
            if (servicePid == null) {
                serviceProperties.remove(SERVICE_PID);
            } else {
                serviceProperties.put(SERVICE_PID, servicePid);
            }

            if (serviceRegistration != null) {
                serviceRegistration.setProperties(serviceProperties);
            }
        }
    }

    /**
     * @return The service pid of this {@code BundleClassResolverHelper}. Returns {@code null} if not set.
     */
    public final String getServicePid() {
        synchronized (lock) {
            return (String) serviceProperties.get(SERVICE_PID);
        }
    }

    /**
     * Sets the application nane.
     */
    public final void setApplicationName(String... applicationNames) {
        synchronized (lock) {
            if (applicationNames == null) {
                serviceProperties.remove(APPLICATION_NAME);
            } else {
                serviceProperties.put(APPLICATION_NAME, applicationNames);
            }

            if (serviceRegistration != null) {
                serviceRegistration.setProperties(serviceProperties);
            }
        }
    }

    /**
     * Register class resolver.
     */
    public final void register() {
        synchronized (lock) {
            if (serviceRegistration == null) {
                BundleClassResolver resolver = new BundleClassResolver();
                serviceRegistration = bundleContext.registerService(SERVICE_NAMES, resolver, serviceProperties);
            }
        }
    }

    /**
     * Unregister class resolver.
     */
    public final void dispose() {
        synchronized (lock) {
            if (serviceRegistration != null) {
                serviceRegistration.unregister();
                serviceRegistration = null;
            }
        }
    }

    private final class BundleClassResolver implements IClassResolver, ManagedService {

        public final Class<?> resolveClass(String classname) throws ClassNotFoundException {
            Bundle bundle = bundleContext.getBundle();
            return bundle.loadClass(classname);
        }

        public Iterator<URL> getResources(String name) {
            try {
                final Bundle bundle = bundleContext.getBundle();
                final Enumeration<URL> enumeration = bundle.getResources(name);
                if (null == enumeration) {
                    return null;
                }
                return new EnumerationAdapter<URL>(enumeration);
            } catch (IOException e) {
                return Collections.<URL> emptyList().iterator();
            }
        }

        @SuppressWarnings("rawtypes")
        public final void updated(Dictionary dictionary) throws ConfigurationException {
            synchronized (lock) {
                if (dictionary == null) {
                    return;
                }
                Object applicationNames = dictionary.get(APPLICATION_NAME);
                if (applicationNames != null) {
                    serviceProperties.put(APPLICATION_NAME, applicationNames);
                } else {
                    serviceProperties.remove(APPLICATION_NAME);
                }
                serviceRegistration.setProperties(serviceProperties);
            }
        }

        /**
         * This method is uses only for some internal wicket stuff if the IClassResolver is NOT replaced and in some IOC
         * stuff also not used by pax wicket. Therefore this method should never ever be called. If it is though we want
         * to be informed about the problem as soon as possible.
         */
        public ClassLoader getClassLoader() {
            throw new UnsupportedOperationException("This method should NOT BE CALLED!");
        }
    }
}
