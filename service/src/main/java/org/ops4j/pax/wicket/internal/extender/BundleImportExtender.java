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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ops4j.pax.wicket.internal.extender.ExtendedBundle.ExtendedBundleContext;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.framework.SynchronousBundleListener;
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Extender adds dynmic imports to client bundles
 * 
 */
public class BundleImportExtender implements WeavingHook, SynchronousBundleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleImportExtender.class);

    private final Set<Long> extendedBundles = new HashSet<Long>();

    private final List<String> additionalImports = new ArrayList<String>();

    private final ExtendedBundleContext extendedBundleContext;

    public BundleImportExtender(BundleContext paxBundleContext) {
        extendedBundleContext = new ExtendedBundle.ExtendedBundleContext(paxBundleContext);
        // TODO: Any chance we not must hardcode this?
        additionalImports.add("org.apache.wicket.core.request.mapper");
        additionalImports.add("org.ops4j.pax.wicket.util.proxy");
        additionalImports.add("net.sf.cglib.proxy;version=\"[2,3)\"");
        additionalImports.add("net.sf.cglib.core;version=\"[2,3)\"");
        additionalImports.add("net.sf.cglib.reflect;version=\"[2,3)\"");
        additionalImports.add("javax.servlet;version=\"2.5.0\"");
        additionalImports.add("javax.servlet.http;version=\"2.5.0\"");
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
                wovenClass.getDynamicImports().addAll(additionalImports);
            }
        } catch (RuntimeException e) {
            LOGGER.warn("RuntimeException while trying to extend bundle imports");
        }

    }

    public void bundleChanged(BundleEvent event) {
        // Remove bundle from cache in some circumstances
        switch (event.getType()) {
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
