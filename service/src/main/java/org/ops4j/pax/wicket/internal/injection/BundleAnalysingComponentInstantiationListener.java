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
import java.util.Enumeration;
import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Factory;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.internal.OverwriteProxy;
import org.ops4j.pax.wicket.internal.injection.blueprint.BlueprintBeanProxyTargetLocator;
import org.ops4j.pax.wicket.internal.injection.registry.OSGiServiceRegistryProxyTargetLocator;
import org.ops4j.pax.wicket.internal.injection.spring.SpringBeanProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BundleAnalysingComponentInstantiationListener extends AbstractPaxWicketInjector {

    private static final Logger LOGGER = LoggerFactory.getLogger(BundleAnalysingComponentInstantiationListener.class);

    private final BundleContext bundleContext;
    private String bundleResources = "";
    private final String defaultInjectionSource;

    public BundleAnalysingComponentInstantiationListener(BundleContext bundleContext, String defaultInjectionSource) {
        this.bundleContext = bundleContext;
        this.defaultInjectionSource = defaultInjectionSource;
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
            String injectionSource = null;
            // TODO: [PAXWICKET-265] This have to look differently
            if (Factory.class.isInstance(component)) {
                overwrites = ((OverwriteProxy) ((Factory) component).getCallback(0)).getOverwrites();
                injectionSource = ((OverwriteProxy) ((Factory) component).getCallback(0)).getInjectionSource();
                realClass = realClass.getSuperclass();
            }
            if (injectionSource == null || injectionSource.equals("")) {
                injectionSource = defaultInjectionSource;
            }
            Thread.currentThread().setContextClassLoader(realClass.getClassLoader());

            List<Field> fields = getSingleLevelOfFields(realClass);
            for (Field field : fields) {
                if (!field.isAnnotationPresent(PaxWicketBean.class)) {
                    continue;
                }
                PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
                if (!annotation.injectionSource().equals(PaxWicketBean.INJECTION_SOURCE_UNDEFINED)) {
                    injectionSource = annotation.injectionSource();
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
                    Object proxy = createProxy(field, realClass, overwrites, injectionSource);
                    setField(component, field, proxy);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(currentClassLoader);
        }
    }

    private Object createProxy(Field field, Class<?> page, Map<String, String> overwrites, String injectionSource) {
        return LazyInitProxyFactory.createProxy(getBeanType(field),
            createProxyTargetLocator(field, page, overwrites, injectionSource));
    }

    private IProxyTargetLocator createProxyTargetLocator(Field field, Class<?> page, Map<String, String> overwrites,
            String injectionSource) {
        if (PaxWicketBean.INJECTION_SOURCE_NULL.equals(injectionSource)
                || PaxWicketBean.INJECTION_SOURCE_UNDEFINED.equals(injectionSource)) {
            return null;
        }
        PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
        AbstractProxyTargetLocator<?> springBeanTargetLocator =
            new SpringBeanProxyTargetLocator(bundleContext, annotation, getBeanType(field), page, overwrites);
        if (PaxWicketBean.INJECTION_SOURCE_SPRING.equals(injectionSource)) {
            return springBeanTargetLocator;
        }
        AbstractProxyTargetLocator<?> blueprintBeanTargetLocator =
            new BlueprintBeanProxyTargetLocator(bundleContext, annotation, getBeanType(field), page, overwrites);
        if (PaxWicketBean.INJECTION_SOURCE_BLUEPRINT.equals(injectionSource)) {
            return blueprintBeanTargetLocator;
        }
        if (PaxWicketBean.INJECTION_SOURCE_SERVICE_REGISTRY.equals(injectionSource)) {
            return new OSGiServiceRegistryProxyTargetLocator(bundleContext, annotation, getBeanType(field), page);
        }
        if (PaxWicketBean.INJECTION_SOURCE_SCAN.equals(injectionSource)) {
            boolean springBeanTargetLocatorHasApplicationContext =
                hasApplicationContextDelegation(springBeanTargetLocator);
            boolean blueprintBeanTargetLocatorHasApplicationContext =
                hasApplicationContextDelegation(blueprintBeanTargetLocator);
            if (springBeanTargetLocatorHasApplicationContext && blueprintBeanTargetLocatorHasApplicationContext) {
                throw new IllegalStateException(
                    "INJECTION_SOURCE_SCAN cannot be used if spring & blueprint context exist.");
            }
            if (!springBeanTargetLocatorHasApplicationContext && !blueprintBeanTargetLocatorHasApplicationContext) {
                throw new IllegalStateException(
                    "INJECTION_SOURCE_SCAN cannot be used with neither blueprint nor spring context");
            }
            if (springBeanTargetLocatorHasApplicationContext) {
                return springBeanTargetLocator;
            }
            if (blueprintBeanTargetLocatorHasApplicationContext) {
                return blueprintBeanTargetLocator;
            }
        }
        throw new IllegalStateException(String.format("No injection source found for field [%s] in class [%s]",
            field.getName(), page.getName()));
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
