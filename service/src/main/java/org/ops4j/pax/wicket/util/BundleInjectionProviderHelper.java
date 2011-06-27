/* 
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util;

import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.util.Dictionary;
import java.util.Properties;

import org.ops4j.pax.wicket.api.NoBeanAvailableForInjectionException;
import org.ops4j.pax.wicket.api.PaxWicketInjector;
import org.ops4j.pax.wicket.internal.injection.BundleAnalysingComponentInstantiationListener;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

/**
 * {@code BundleClassResolverHelper} is a helper to register {@code IClassResolver}.
 */
public final class BundleInjectionProviderHelper {

    private static final String[] SERVICE_NAMES =
    {
        PaxWicketInjector.class.getName(),
        ManagedService.class.getName()
    };

    private final BundleContext bundleContext;
    private final Properties serviceProperties;
    private BundleAnalysingComponentInstantiationListener bundleAnalysingComponentInstantiationListener;

    private final Object lock = new Object();
    private ServiceRegistration serviceRegistration;

    /**
     * Construct an instance of {@code BundleClassResolver}.
     */
    public BundleInjectionProviderHelper(BundleContext bundleContext, String applicationName)
        throws IllegalArgumentException {
        validateNotNull(bundleContext, "bundle");
        this.bundleContext = bundleContext;
        serviceProperties = new Properties();
        setApplicationName(applicationName);
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
                serviceProperties.setProperty(SERVICE_PID, servicePid);
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
            return serviceProperties.getProperty(SERVICE_PID);
        }
    }

    /**
     * Sets the application nane.
     */
    public final void setApplicationName(String applicationName) {
        synchronized (lock) {
            if (applicationName == null) {
                serviceProperties.remove(APPLICATION_NAME);
                bundleAnalysingComponentInstantiationListener = null;
            } else {
                serviceProperties.put(APPLICATION_NAME, applicationName);
                bundleAnalysingComponentInstantiationListener =
                    new BundleAnalysingComponentInstantiationListener(bundleContext);
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
                BundleInjectionResolver resolver = new BundleInjectionResolver();
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

    private final class BundleInjectionResolver implements PaxWicketInjector, ManagedService {

        public void inject(Object toInject) {
            validateNotNull(bundleAnalysingComponentInstantiationListener,
                "bundleAnalysingComponentInstantiationListener");
            if (bundleAnalysingComponentInstantiationListener.injectionPossible(toInject.getClass())) {
                bundleAnalysingComponentInstantiationListener.inject(toInject);
                return;
            }
            throw new NoBeanAvailableForInjectionException();
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

    }
}
