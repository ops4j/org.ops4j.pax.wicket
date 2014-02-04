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
package org.ops4j.pax.wicket.internal.injection;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.api.PaxWicketInjector;
import org.ops4j.pax.wicket.internal.InternalBundleDelegationProvider;
import org.ops4j.pax.wicket.internal.extender.ExtendedBundle;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleDelegatingComponentInstanciationListener implements PaxWicketInjector,
        InternalBundleDelegationProvider {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(BundleDelegatingComponentInstanciationListener.class);

    private final String applicationName;
    private final BundleContext paxWicketBundleContext;

    private final Map<String, BundleAnalysingComponentInstantiationListener> listeners =
        new HashMap<String, BundleAnalysingComponentInstantiationListener>();
    private ServiceRegistration<PaxWicketInjector> serviceRegistration;

    private final ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> factoryTracker;

    public BundleDelegatingComponentInstanciationListener(BundleContext paxWicketBundleContext,
            String applicationName,
            ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> factoryTracker) {
        this.paxWicketBundleContext = paxWicketBundleContext;
        this.applicationName = applicationName;
        this.factoryTracker = factoryTracker;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void start() {
        Dictionary<String, String> props = new Hashtable<String, String>();
        props.put(Constants.APPLICATION_NAME, applicationName);
        serviceRegistration = paxWicketBundleContext.registerService(PaxWicketInjector.class, this, props);
    }

    public void stop() {
        if (serviceRegistration == null) {
            LOGGER.warn("Trying to unregister listener although not registered.");
            return;
        }
        serviceRegistration.unregister();
    }

    public void addBundle(ExtendedBundle bundle) {
        if (serviceRegistration == null) {
            throw new IllegalStateException("Cannot add any bundle to listener while not started.");
        }
        synchronized (listeners) {
            listeners.put(bundle.getBundle().getSymbolicName(), new BundleAnalysingComponentInstantiationListener(
                    bundle.getBundle().getBundleContext(), PaxWicketBeanInjectionSource.INJECTION_SOURCE_SCAN,
                    factoryTracker));
        }
    }

    public void removeBundle(ExtendedBundle bundle) {
        if (serviceRegistration == null) {
            throw new IllegalStateException("Cannot add any bundle to listener while not started.");
        }
        synchronized (listeners) {
            listeners.remove(bundle.getBundle().getSymbolicName());
        }
    }

    public void inject(Object toInject, Class<?> toHandle) {
        synchronized (listeners) {
            Collection<BundleAnalysingComponentInstantiationListener> values = listeners.values();
            for (BundleAnalysingComponentInstantiationListener analyser : values) {
                if (analyser.injectionPossible(toHandle)) {
                    analyser.inject(toInject, toHandle);
                    return;
                }
            }
        }
        throw new IllegalStateException("no source for injection found");
    }

}
