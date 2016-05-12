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
import org.osgi.framework.hooks.weaving.WeavingHook;
import org.osgi.framework.hooks.weaving.WovenClass;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This Extender adds dynmic imports to client bundles
 */
//Temporary disable weaving
//@Component(service = {WeavingHook.class})
public class BundleWicketCoreImportExtender implements WeavingHook {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleWicketCoreImportExtender.class);

    private final Set<Long> extendedBundles = new HashSet<Long>();

    private static final Set<String> ADDITIONAL_IMPORTS = new HashSet<String>();

    //TODO THIS SHOULD PULL from POM file
    static {
        ADDITIONAL_IMPORTS.add("net.sf.cglib.proxy;version=\"[2,3)\"");
        ADDITIONAL_IMPORTS.add("net.sf.cglib.core;version=\"[2,3)\"");
        ADDITIONAL_IMPORTS.add("net.sf.cglib.reflect;version=\"[2,3)\"");
    }


    public BundleWicketCoreImportExtender() {
    }

    @Override
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

            if (bundle.getSymbolicName().startsWith("org.apache.wicket.core")) {
                LOGGER.debug("net.sf.cglib Enhance DynamicImports of bundle {}...", bundle.getSymbolicName());

                wovenClass.getDynamicImports().addAll(ADDITIONAL_IMPORTS);
            }
        } catch (RuntimeException e) {
            LOGGER.warn("RuntimeException while trying to extend bundle imports");
        }
    }
}
