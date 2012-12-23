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

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import net.sf.cglib.proxy.Factory;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.spi.OverwriteProxy;
import org.ops4j.pax.wicket.spi.ProxyTarget;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleAnalysingComponentInstantiationListener extends AbstractPaxWicketInjector {

    /**
     * 
     */
    private static final ProxyTargetLocatorFactory[] EMPTY_ARRAY = new ProxyTargetLocatorFactory[0];

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleAnalysingComponentInstantiationListener.class);

    private final BundleContext bundleContext;
    private String bundleResources = "";
    private final String defaultInjectionSource;

    private final ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> tracker;

    public BundleAnalysingComponentInstantiationListener(BundleContext bundleContext, String defaultInjectionSource,
            ServiceTracker<ProxyTargetLocatorFactory, ProxyTargetLocatorFactory> tracker) {
        this.bundleContext = bundleContext;
        this.defaultInjectionSource = defaultInjectionSource;
        this.tracker = tracker;
        // TODO use ExtendedBundle instead
        Enumeration<URL> entries = bundleContext.getBundle().findEntries("/", "*.class", true);
        if (entries == null) {
            // bundle with no .class files (see PAXWICKET-305)
            return;
        }
        while (entries.hasMoreElements()) {
            String urlRepresentation =
                entries.nextElement().toExternalForm().replace("bundle://.+?/", "").replace('/', '.');
            LOGGER.trace("Found entry {} in bundle {}", urlRepresentation, bundleContext.getBundle().getSymbolicName());
            bundleResources += urlRepresentation;
        }
    }

    public boolean injectionPossible(Class<?> component) {
        String name = component.getName();
        LOGGER.debug("Try to find class {} in bundle {}", name, bundleContext.getBundle().getSymbolicName());
        String searchString = name.replaceAll("\\$\\$.*", "");
        searchString = searchString.replaceAll("\\$", "\\\\\\$"); // for nested and anonymous classes
        if (bundleResources.matches(".*" + searchString + ".*")) {
            LOGGER.trace("Found class {} in bundle {}", name, bundleContext.getBundle().getSymbolicName());
            return true;
        }
        LOGGER.trace("Class {} not available in bundle {}", name, bundleContext.getBundle().getSymbolicName());
        return false;
    }

    public void inject(Object component, Class<?> toHandle) {
        ClassLoader currentClassLoader = Thread.currentThread().getContextClassLoader();
        try {
            Class<?> realClass = toHandle;
            Map<String, String> overwrites = null;
            String injectionSource = PaxWicketBean.INJECTION_SOURCE_SCAN;
            if (Factory.class.isInstance(component)) {
                overwrites = ((OverwriteProxy) ((Factory) component).getCallback(0)).getOverwrites();
                injectionSource = ((OverwriteProxy) ((Factory) component).getCallback(0)).getInjectionSource();
                realClass = realClass.getSuperclass();
            } else {
                injectionSource = PaxWicketBean.INJECTION_SOURCE_SCAN;
            }
            if (injectionSource == null || injectionSource.length() > 0) {
                injectionSource = defaultInjectionSource;
            }
            Thread.currentThread().setContextClassLoader(realClass.getClassLoader());

            List<Field> fields = getSingleLevelOfFields(realClass);
            for (Field field : fields) {
                if (!field.isAnnotationPresent(PaxWicketBean.class)) {
                    continue;
                }
                PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
                String fieldInjectionSource = annotation.injectionSource();
                if (fieldInjectionSource != null && fieldInjectionSource.length() > 0) {
                    injectionSource = fieldInjectionSource;
                }
                if (field.getType().equals(BundleContext.class)) {
                    // Is this the special BundleContext type?
                    ClassLoader classLoader = realClass.getClassLoader();
                    if (classLoader instanceof BundleReference) {
                        BundleReference bundleReference = (BundleReference) classLoader;
                        Bundle bundle = bundleReference.getBundle();
                        setField(component, field, bundle.getBundleContext());
                    }
                } else {
                    Object proxy = LazyInitProxyFactory.createProxy(getBeanType(field),
                        createProxyTargetLocator(field, realClass, overwrites, injectionSource));
                    setField(component, field, proxy);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private ProxyTargetLocator createProxyTargetLocator(Field field, final Class<?> page,
            Map<String, String> overwrites,
            String injectionSource) {
        ProxyTargetLocatorFactory[] factories = tracker.getServices(EMPTY_ARRAY);
        if (factories.length == 0) {
            // If no factories are present we will wait for 2 seconds for at least one
            try {
                factories = new ProxyTargetLocatorFactory[]{ tracker.waitForService(TimeUnit.SECONDS.toMillis(2)) };
            } catch (InterruptedException e) {
                // We ignore this...
            }
        }
        List<ProxyTargetLocator> locators = new ArrayList<ProxyTargetLocator>(1);
        for (ProxyTargetLocatorFactory factory : factories) {
            if (factory == null) {
                continue;
            }
            if (injectionSource == null || injectionSource.length() == 0 || injectionSource.equals(factory.getName())
                    || PaxWicketBean.INJECTION_SOURCE_SCAN.equals(injectionSource)) {
                try {
                    // We consider this factory...
                    ProxyTargetLocator locator =
                        factory.createProxyTargetLocator(bundleContext, field, page, overwrites);
                    if (locator != null) {
                        locators.add(locator);
                    }
                } catch (RuntimeException e) {
                    LOGGER.warn("ProxyTargetLocatorFactory factory {} because of RuntimeException", factory.getName(),
                        e);
                }
            }
        }
        if (locators.isEmpty()) {
            if (field.getAnnotation(PaxWicketBean.class).allowNull()) {
                return new ProxyTargetLocator() {

                    private static final long serialVersionUID = 1L;

                    public ProxyTarget locateProxyTarget() {
                        return null;
                    }

                    public Class<?> getParent() {
                        return page;
                    }
                };
            } else {
                throw new IllegalStateException(
                    String
                        .format(
                            "No injection source found for field [%s] in class [%s] and field is not marked as null allowed, the following injectors where queried: %s",
                            field.getName(), page.getName(), toInjectNameString(factories)));
            }
        } else {
            if (locators.size() > 1) {
                LOGGER
                    .warn(
                        "More than one injection source could be considered for field [{}] in class [{}] to archive consistent behaviour use an explicit injection source",
                        field.getName(), page.getName());
            }
            return locators.get(0);
        }

    }

    /**
     * @param factories
     * @return
     */
    private Object toInjectNameString(ProxyTargetLocatorFactory[] factories) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;
        for (int i = 0; i < factories.length; i++) {
            ProxyTargetLocatorFactory factory = factories[i];
            if (factory != null) {
                if (!first) {
                    sb.append(", ");
                }
                first = false;
                sb.append(factory);
            }
        }
        sb.append("]");
        return sb;
    }

    /**
     * @param locator
     * @return <code>locator.hasApplicationContext()</code> if locator is not <code>null</code>, otherwhise
     *         <code>false</code>. If the call throws any exception <code>false</code> is returned also
     */
    private boolean hasApplicationContextDelegation(AbstractProxyTargetLocator<?> locator) {
        if (locator != null) {
            try {
                return locator.hasApplicationContext();
            } catch (Exception e) {
                LOGGER
                    .debug(
                        "Can't determine hasApplicationContext for locator {}, an optional import might not resolve, return false",
                        locator.getClass().getName(), e);
            } catch (NoClassDefFoundError e) {
                // This is really nasty, but if wen don't catch this we can not catch java.lang.ClassNotFoundException
                // wich are the root of the cause!
                LOGGER
                    .debug(
                        "Can't determine hasApplicationContext for locator {}, an optional import might not resolve, return false",
                        locator.getClass().getName(), e);
            }
        }
        return false;
    }

}
