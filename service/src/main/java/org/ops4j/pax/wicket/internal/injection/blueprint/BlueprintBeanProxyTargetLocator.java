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
package org.ops4j.pax.wicket.internal.injection.blueprint;

import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.internal.NotImplementedException;
import org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.blueprint.container.BlueprintContainer;
import org.osgi.service.blueprint.container.NoSuchComponentException;
import org.springframework.context.ApplicationContext;

public class BlueprintBeanProxyTargetLocator implements IProxyTargetLocator {

    private static final long serialVersionUID = 1L;

    private PaxWicketBean annotation;
    private Class<?> beanType;
    private Class<?> parent;
    private BundleContext bundleContext;
    private Map<String, String> overwrites;

    public BlueprintBeanProxyTargetLocator(BundleContext bundleContext, PaxWicketBean annotation, Class<?> beanType,
            Class<?> parent, Map<String, String> overwrites) {
        this.bundleContext = bundleContext;
        this.annotation = annotation;
        this.beanType = beanType;
        this.parent = parent;
        this.overwrites = overwrites;
    }

    public Object locateProxyTarget() {
        if (bundleContext == null) {
            throw new IllegalStateException("Bundle context is not allowed to be null");
        }
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        String filter = getApplicationContextFilter(bundleContext.getBundle().getSymbolicName());
        ServiceReference[] references = null;
        try {
            references = bundleContext.getServiceReferences(ApplicationContext.class.getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("not possible", e);
        }
        if (references == null || references.length == 0) {
            throw new IllegalStateException(String.format("Found %s service references for %s; this is not OK...",
                references.length, bundleContext.getBundle().getSymbolicName()));
        }
        BeanReactor strategy = createStrategy();
        try {
            Thread.currentThread().setContextClassLoader(parent.getClassLoader());
            for (ServiceReference serviceReference : references) {
                BlueprintContainer service = (BlueprintContainer) bundleContext.getService(serviceReference);
                try {
                    if (!strategy.containsBean(service)) {
                        continue;
                    }
                    return strategy.createBean(service);
                } finally {
                    bundleContext.ungetService(serviceReference);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
        throw new IllegalStateException(String.format(
            "Bundle %s can no longer attach bean %s with ID %s, class %s and type %s to page %s", bundleContext
                .getBundle().getSymbolicName(), beanType.getName(), annotation.name(), beanType.getName(),
            annotation.beanResolverType(), parent.getName()));
    }

    private BeanReactor createStrategy() {
        if (annotation.name().equals("")) {
            throw new NotImplementedException("For blueprint the name of the bean to retrieve have to be defined.");
        }
        if (overwrites == null || overwrites.size() == 0 || !overwrites.containsKey(annotation.name())) {
            return new BeanReactor() {
                public boolean containsBean(BlueprintContainer applicationContext) {
                    try {
                        applicationContext.getComponentInstance(annotation.name());
                    } catch (NoSuchComponentException e) {
                        return false;
                    }
                    return true;
                }

                public Object createBean(BlueprintContainer applicationContext) {
                    return applicationContext.getComponentInstance(annotation.name());
                }
            };
        }
        return new BeanReactor() {
            public boolean containsBean(BlueprintContainer applicationContext) {
                try {
                    applicationContext.getComponentInstance(overwrites.get(annotation.name()));
                } catch (NoSuchComponentException e) {
                    return false;
                }
                return true;
            }

            public Object createBean(BlueprintContainer applicationContext) {
                return applicationContext.getComponentInstance(overwrites.get(annotation.name()));
            }
        };
    }

    private static interface BeanReactor {
        boolean containsBean(BlueprintContainer applicationContext);

        Object createBean(BlueprintContainer applicationContext);
    }

    private String getApplicationContextFilter(String symbolicBundleName) {
        return String.format("(&(%s=%s)(%s=%s))", "osgi.blueprint.container.symbolicname", symbolicBundleName,
            Constants.OBJECTCLASS, BlueprintContainer.class.getName());
    }

}
