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
package org.ops4j.pax.wicket.internal.spring;

import java.util.Map;

import org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.context.ApplicationContext;

public class SpringBeanProxyTargetLocator implements IProxyTargetLocator {

    private static final long serialVersionUID = 1L;

    private PaxWicketBean annotation;
    private Class<?> beanType;
    private Class<?> parent;
    private BundleContext bundleContext;
    private String applicationName;
    private Map<String, String> overwrites;

    public SpringBeanProxyTargetLocator(String applicationName, BundleContext bundleContext, PaxWicketBean annotation,
            Class<?> beanType, Class<?> parent, Map<String, String> overwrites) {
        this.applicationName = applicationName;
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
        String filter = SpringBeanHelper.getApplicationContextFilter(bundleContext.getBundle().getSymbolicName());
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
        try {
            Thread.currentThread().setContextClassLoader(parent.getClassLoader());
            BeanReactor strategy = createStrategy();
            for (ServiceReference serviceReference : references) {
                ApplicationContext service = (ApplicationContext) bundleContext.getService(serviceReference);
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
            return new BeanReactor() {
                public boolean containsBean(ApplicationContext applicationContext) {
                    try {
                        applicationContext.getBean(beanType);
                    } catch (NoSuchBeanDefinitionException e) {
                        return false;
                    }
                    return true;
                }

                public Object createBean(ApplicationContext applicationContext) {
                    return applicationContext.getBean(beanType);
                }
            };
        }
        if (overwrites == null || overwrites.size() == 0 || !overwrites.containsKey(annotation.name())) {
            return new BeanReactor() {
                public boolean containsBean(ApplicationContext applicationContext) {
                    return applicationContext.containsBean(annotation.name());
                }

                public Object createBean(ApplicationContext applicationContext) {
                    return applicationContext.getBean(annotation.name(), beanType);
                }
            };
        }
        return new BeanReactor() {
            public boolean containsBean(ApplicationContext applicationContext) {
                return applicationContext.containsBean(overwrites.get(annotation.name()));
            }

            public Object createBean(ApplicationContext applicationContext) {
                return applicationContext.getBean(overwrites.get(annotation.name()), beanType);
            }
        };
    }

    private static interface BeanReactor {
        boolean containsBean(ApplicationContext applicationContext);

        Object createBean(ApplicationContext applicationContext);
    }

}
