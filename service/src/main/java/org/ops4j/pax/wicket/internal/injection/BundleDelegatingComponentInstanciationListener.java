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

import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;

import java.util.HashSet;

import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.api.NoBeanAvailableForInjectionException;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.ops4j.pax.wicket.api.PaxWicketInjector;
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
 * able to be searched for beans: {@link ContentSource}, {@link ContentAggregator}, {@link PageFactory} and
 * {@link PaxWicketApplicationFactory}.
 */
public class BundleDelegatingComponentInstanciationListener extends ServiceTracker implements
        PaxWicketInjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleDelegatingComponentInstanciationListener.class);

    private static final String FILTER = "(|" +
            "(objectClass=" + ContentSource.class.getName() + ")" +
            "(objectClass=" + ContentAggregator.class.getName() + ")" +
            "(objectClass=" + PageFactory.class.getName() + ")" +
            "(objectClass=" + PaxWicketApplicationFactory.class.getName() + ")" +
            ")";

    private HashSet<BundleAnalysingComponentInstantiationListener> listeners;
    private final String applicationName;
    private final Bundle paxWicketBundle;

    public BundleDelegatingComponentInstanciationListener(BundleContext context, String applicationName,
            Bundle paxWicketBundle) {
        super(context, createFilter(context), null);

        this.applicationName = applicationName;
        this.paxWicketBundle = paxWicketBundle;
        listeners = new HashSet<BundleAnalysingComponentInstantiationListener>();
        listeners.add(new BundleAnalysingComponentInstantiationListener(paxWicketBundle.getBundleContext()));

        open(true);
    }

    public void inject(Object toInject) {
        for (BundleAnalysingComponentInstantiationListener analyser : listeners) {
            if (analyser.injectionPossible(toInject.getClass())) {
                analyser.inject(toInject);
                return;
            }
        }
        throw new NoBeanAvailableForInjectionException();
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
            HashSet<BundleAnalysingComponentInstantiationListener> clone =
                (HashSet<BundleAnalysingComponentInstantiationListener>) listeners.clone();
            clone.add(new BundleAnalysingComponentInstantiationListener(bundle.getBundleContext()));
            listeners = clone;
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
        HashSet<BundleAnalysingComponentInstantiationListener> revisedSet =
            new HashSet<BundleAnalysingComponentInstantiationListener>();
        revisedSet.add(new BundleAnalysingComponentInstantiationListener(paxWicketBundle.getBundleContext()));
        try {
            LOGGER.info("Removing bundle {} to DelegatingClassLoader", serviceReference.getBundle().getSymbolicName());
            synchronized (this) {
                ServiceReference[] serviceReferences = context.getAllServiceReferences(null, FILTER);
                if (serviceReferences != null) {
                    for (ServiceReference ref : serviceReferences) {
                        revisedSet.add(new BundleAnalysingComponentInstantiationListener(ref.getBundle()
                            .getBundleContext()));
                    }
                }
                listeners = revisedSet;
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
