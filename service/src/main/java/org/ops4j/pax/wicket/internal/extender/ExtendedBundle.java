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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;
import org.ops4j.pax.wicket.internal.Activator;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.Version;
import org.osgi.framework.wiring.BundleRequirement;
import org.osgi.framework.wiring.BundleWire;
import org.osgi.framework.wiring.BundleWiring;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class handles all the enhancement stuff for PAX Wicket when working with an underlying bundle
 * 
 */
public class ExtendedBundle {

    private static final String FILTER_DIRECTIVE = "filter";

    private static final String OSGI_WIRING_PACKAGE_NAMESPACE = "osgi.wiring.package";

    private static final String OSGI_WIRING_BUNDLE_NAMESPACE = "osgi.wiring.bundle";

    private static final String APACHE_WICKET_NAMESPACE = "org.apache.wicket";

    private static final Pattern PACKAGE_PATTERN_WICKET = Pattern.compile("\\(" + OSGI_WIRING_PACKAGE_NAMESPACE
            + "=" + Pattern.quote(APACHE_WICKET_NAMESPACE) + "\\..*\\)");

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketBundleListener.class);

    private final Bundle bundle;

    private final ExtendedBundleContext bundleContext;

    /**
     * @param bundle
     */
    public ExtendedBundle(ExtendedBundleContext bundleContext, Bundle bundle) {
        this.bundleContext = bundleContext;
        this.bundle = bundle;
    }

    /**
     * @return the current value of bundleContext
     */
    public ExtendedBundleContext getExtendedBundleContext() {
        return bundleContext;
    }

    /**
     * @return the current value of bundle
     */
    public Bundle getBundle() {
        return bundle;
    }

    public String getID() {
        return getBundle().getSymbolicName();
    }

    /**
     * @return <code>true</code> if this bundle is relevant for {@link PaxWicketMountPoint} annotations
     *         <code>false</code> otherwhise
     */
    public boolean isRelevantForMountPointAnnotations() {
        if (isWicket() || isPAXWicket()) {
            return false;
        }
        return isImportingPAXWicketAPI();
    }

    public boolean isRelevantForImportEnhancements() {
        if (isWicket() || isPAXWicket()) {
            return false;
        }
        return isImportingPAXWicketAPI() || isImportingWicket();
    }

    /**
     * @return <code>true</code> if the underlying bundle is one of the wicket bundles <code>false</code> otherwhise
     */
    public boolean isWicket() {
        String symbolicName = getBundle().getSymbolicName();
        if (symbolicName.startsWith(APACHE_WICKET_NAMESPACE)) {
            return true;
        }
        return false;
    }

    /**
     * @return <code>true</code> if the underlying bundle is the PAXWicket bundle
     */
    public boolean isPAXWicket() {
        String symbolicName = getBundle().getSymbolicName();
        if (symbolicName.equals(Activator.getBundleContext().getBundle().getSymbolicName())) {
            return true;
        }
        return false;
    }

    /**
     * 
     * @return <code>true</code> if this bundle imports anything from the org.apache.wicket Namespace
     */
    public boolean isImportingWicket() {
        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        // First check if there is a wiring to any package of org.apache.wicket
        List<BundleWire> importPackageWires = bundleWiring.getRequiredWires(OSGI_WIRING_PACKAGE_NAMESPACE);
        for (BundleWire bundleWire : importPackageWires) {
            BundleRequirement requirement = bundleWire.getRequirement();
            String filter = requirement.getDirectives().get(FILTER_DIRECTIVE);
            if (filter != null) {
                Matcher matcher = PACKAGE_PATTERN_WICKET.matcher(filter);
                if (matcher.find()) {
                    return true;
                }
            }
        }
        List<BundleWire> requireBundleWires = bundleWiring.getRequiredWires(OSGI_WIRING_BUNDLE_NAMESPACE);
        if (!requireBundleWires.isEmpty()) {
            // find all apache.wicket bundles and check if there are wirings...
            Bundle[] bundles = bundleContext.paxBundleContext.getBundles();
            for (Bundle bundle : bundles) {
                String symbolicName = bundle.getSymbolicName();
                if (symbolicName.startsWith(APACHE_WICKET_NAMESPACE)) {
                    Map<String, Object> map = createMapWithVersion(OSGI_WIRING_BUNDLE_NAMESPACE, symbolicName,
                        bundle.getVersion());
                    if (hasWireMatchingFilter(requireBundleWires, map)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 
     * @return <code>true</code> if this bundle imports anything from the org.ops4j.pax.wicket.api Namespace
     */
    public boolean isImportingPAXWicketAPI() {
        // Check if there is a package wiring (either static or dynamic)
        BundleWiring bundleWiring = bundle.adapt(BundleWiring.class);
        boolean hasPackageImport =
            hasWireMatchingFilter(bundleWiring.getRequiredWires(OSGI_WIRING_PACKAGE_NAMESPACE),
                bundleContext.importPAXWicketAPI);
        // check if there is an require bundle wire...
        return hasPackageImport || hasWireMatchingFilter(
            bundleWiring.getRequiredWires(OSGI_WIRING_BUNDLE_NAMESPACE),
            bundleContext.requirePAXWicketBundle);
    }

    private boolean hasWireMatchingFilter(List<BundleWire> wires, Map<String, ?> map) {
        for (BundleWire bundleWire : wires) {
            BundleRequirement requirement = bundleWire.getRequirement();
            if (bundleContext
                .matchFilter(requirement.getDirectives().get(FILTER_DIRECTIVE), map)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Try to load a Collection of all classes conained in the underlying bundle, please be aware that calling this has
     * the following implications:
     * <ul>
     * <li>it might be costly to call this method and invloves loading all class from the underlying bundle what might
     * trigger other bundles with lazy activation to activate</li>
     * <li>it can't be garantueed that all classes are found</li>
     * <li>even if a class is detected it might not be included if it's optional dependencies are not already bound to
     * the bundle</li>
     * </ul>
     * 
     * @return a Collection of classes conained in this bundle
     */
    public Collection<Class<?>> getAllClasses() {
        Set<Class<?>> classList = new HashSet<Class<?>>();
        Collection<String> resources = bundle.adapt(BundleWiring.class).listResources("/", "*.class",
                BundleWiring.FINDENTRIES_RECURSE | BundleWiring.LISTRESOURCES_LOCAL);
        if (resources != null) {
            for (String resource : resources) {
                if (resource.charAt(0) == '/') {
                    resource = resource.substring(1);
                }
                String className = resource.substring(0, resource.length() - 6).replaceAll("/", ".");
                Class<?> candidateClass = null;
                try {
                    candidateClass = loadCandidate(className);
                } catch (NoClassDefFoundError e) {
                    // Its not nice to catch errors, but otherwhise we can't give a nice feedback!
                    String message = e.getMessage();
                    if (message != null) {
                        // In eclipse, the entry for a class is prepend by the "bin-output-folder" (e.g.
                        // bin/my/package/MyClass.class
                        // If we detect this, try to load the real classname that is mentiened in the message
                        Pattern pattern = Pattern.compile("\\(wrong name: (.+)\\)");
                        Matcher matcher = pattern.matcher(message);
                        if (matcher.find()) {
                            String realname = matcher.group(1);
                            LOGGER.debug("It seems the entry has a misleading name for class {}, retry with name {}",
                                className, realname);
                            candidateClass = loadCandidate(realname.replace('/', '.'));
                        }
                    }
                    if (candidateClass == null) {
                        LOGGER.debug("classloader complains about NoClassDefFoundError while try to load {}",
                            className, e);
                    }
                }
                if (candidateClass != null) {
                    classList.add(candidateClass);
                } else {
                    LOGGER
                        .warn(
                            "Class '{}' was found via bundle {}'s resource path, but classloader can't load it (is the jar file corrupted or a dependant optional dependencies not resolved?)",
                            getBundle().getSymbolicName(),
                            className);
                }
            }
        }
        return classList;
    }

    /**
     * @param className
     * @param bundleToScan
     * @return
     */
    private Class<?> loadCandidate(String className) {
        try {
            return getBundle().loadClass(className);
        } catch (ClassNotFoundException e) {
            LOGGER.debug("ClassNotFoundException while try to load {}", className, e);
            return null;
        } catch (ClassFormatError e) {
            LOGGER.debug("ClassFormatError while try to load {}", className, e);
            return null;
        }
    }

    public static class ExtendedBundleContext {

        private final Map<String, Object> importPAXWicketAPI;
        private final BundleContext paxBundleContext;
        private final Map<String, Object> requirePAXWicketBundle;

        public ExtendedBundleContext(BundleContext paxBundleContext) {
            this.requirePAXWicketBundle =
                createMapWithVersion(OSGI_WIRING_BUNDLE_NAMESPACE, paxBundleContext.getBundle().getSymbolicName(),
                    paxBundleContext.getBundle().getVersion());
            this.importPAXWicketAPI =
                createMapWithVersion(OSGI_WIRING_PACKAGE_NAMESPACE, Constants.class.getPackage().getName(),
                    paxBundleContext.getBundle().getVersion());
            this.paxBundleContext = paxBundleContext;
        }

        /**
         * @param filterString
         * @param map
         * @return
         */
        public boolean matchFilter(String filterString, Map<String, ?> map) {
            if (filterString != null) {
                try {
                    Filter filter = paxBundleContext.createFilter(filterString);
                    if (filter.matches(map)) {
                        LOGGER.trace("filter = {} matches {}", importPAXWicketAPI);
                        return true;
                    } else {
                        LOGGER.trace("filter = {} not matches {}", importPAXWicketAPI);
                    }
                } catch (InvalidSyntaxException e) {
                    LOGGER.warn("can't parse filter expression: {}", filterString);
                }

            }
            return false;
        }

    }

    private static Map<String, Object> createMapWithVersion(String key, Object value, Version version) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(key, value);
        map.put("version", version);
        return Collections.unmodifiableMap(map);
    }
}
