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
package org.ops4j.pax.wicket.internal.extender;

import java.util.HashSet;
import java.util.Set;

import org.ops4j.pax.wicket.internal.extender.ExtendedBundle.ExtendedBundleContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Extender adds dynmic imports to client bundles
 */
@Component(service = { WeavingHook.class })
public class BundleImportExtender implements WeavingHook, SynchronousBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleImportExtender.class);

    private final Set<Long> extendedBundles = new HashSet<Long>();

    private static final Set<String> ADDITIONAL_IMPORTS = new HashSet<String>();

    static {
        ADDITIONAL_IMPORTS.add("org.apache.wicket.core.request.mapper;version=\"[1.6,2)\"");
        ADDITIONAL_IMPORTS.add("org.ops4j.pax.wicket.util.proxy;version=\"[1.3,2)\"");
        ADDITIONAL_IMPORTS.add("net.sf.cglib.proxy;version=\"[2,3)\"");
        ADDITIONAL_IMPORTS.add("net.sf.cglib.core;version=\"[2,3)\"");
        ADDITIONAL_IMPORTS.add("net.sf.cglib.reflect;version=\"[2,3)\"");
        ADDITIONAL_IMPORTS.add("javax.servlet;version=\"[2.5.0,3]\"");
        ADDITIONAL_IMPORTS.add("javax.servlet.http;version=\"[2.5.0,3]\"");
    }

    private ExtendedBundleContext extendedBundleContext;

    @Activate
    public void startUp(BundleContext bundleContext) {
        extendedBundleContext = new ExtendedBundle.ExtendedBundleContext(bundleContext);
        bundleContext.addBundleListener(this);
    }

    @Deactivate
    public void shutDown(BundleContext bundleContext) {
        bundleContext.removeBundleListener(this);
    }

    public void weave(WovenClass wovenClass) {
        try {
            BundleWiring bundleWiring = wovenClass.getBundleWiring();
            Bundle bundle = bundleWiring.getBundle();
            synchronized (extendedBundles) {
                if (extendedBundles.contains(bundle.getBundleId())) {
                    // Nothing to do
                    return;
                }
                extendedBundles.add(bundle.getBundleId());
            }
            ExtendedBundle extendedBundle = new ExtendedBundle(extendedBundleContext, bundle);
            if (extendedBundle.isRelevantForImportEnhancements()) {
                LOGGER.debug("Enhance DynamicImports of bundle {}...", bundle.getSymbolicName());
                wovenClass.getDynamicImports().addAll(ADDITIONAL_IMPORTS);
            }
        } catch (RuntimeException e) {
            LOGGER.warn("RuntimeException while trying to extend bundle imports");
        }

    }

    public void bundleChanged(BundleEvent event) {
        Bundle eventBundle = event.getBundle();
        ExtendedBundle extendedBundle = new ExtendedBundle(extendedBundleContext, eventBundle);
        if (extendedBundle.isRelevantForImportEnhancements()) {
            // TODO: We need to refresh the bundles here if we detect that they were started before we could enhance
            // them!
        }
        switch (event.getType()) {
            // Remove bundle from cache in some circumstances
            case BundleEvent.UPDATED:
            case BundleEvent.UNINSTALLED:
            case BundleEvent.UNRESOLVED:
            case BundleEvent.STOPPED:
                synchronized (extendedBundles) {
                    extendedBundles.remove(event.getBundle().getBundleId());
                }
        }
    }
}
