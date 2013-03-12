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

import java.util.Map;

import org.ops4j.pax.wicket.spi.ProxyTarget;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

public abstract class AbstractProxyTargetLocator<Container> implements ProxyTargetLocator {

    private static final long serialVersionUID = 1L;

    protected Class<?> beanType;
    protected Map<String, String> overwrites;

    private final Class<?> parent;
    private final BundleContext bundleContext;

    public AbstractProxyTargetLocator(BundleContext bundleContext, Class<?> beanType,
            Class<?> parent, Map<String, String> overwrites) {
        this.bundleContext = bundleContext;
        this.beanType = beanType;
        this.parent = parent;
        this.overwrites = overwrites;
    }

    public boolean hasApplicationContext() {
        String filter = getApplicationContextFilter(bundleContext.getBundle().getSymbolicName());
        ServiceReference<?>[] references = null;
        try {
            references = bundleContext.getServiceReferences(getContainerClass().getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("not possible", e);
        }
        return references != null && references.length != 0;
    }

    public ProxyTarget locateProxyTarget() {
        if (bundleContext == null) {
            throw new IllegalStateException("Bundle context is not allowed to be null");
        }
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        String filter = getApplicationContextFilter(bundleContext.getBundle().getSymbolicName());
        ServiceReference<?>[] references = null;
        try {
            references = bundleContext.getServiceReferences(getContainerClass().getName(), filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalStateException("not possible", e);
        }
        if (references == null || references.length == 0) {
            throw new IllegalStateException(String.format("Found zero service references for %s; this is not OK...",
                bundleContext.getBundle().getSymbolicName()));
        }
        try {
            Thread.currentThread().setContextClassLoader(parent.getClassLoader());
            final BeanReactor<Container> strategy = createStrategy();
            for (ServiceReference<?> serviceReference : references) {
                @SuppressWarnings("unchecked")
                final Container service = (Container) bundleContext.getService(serviceReference);
                try {
                    if (!strategy.containsBean(service)) {
                        continue;
                    }
                    return new ProxyTarget() {

                        public Object getTarget() {
                            return strategy.createBean(service);
                        }
                    };
                } finally {
                    bundleContext.ungetService(serviceReference);
                }
            }
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
        throw new IllegalStateException(String.format(
            "Bundle %s can no longer attach bean %s, class %s to page %s", bundleContext
                .getBundle().getSymbolicName(), beanType.getName(), beanType.getName(),
            parent.getName()));
    }

    public Class<?> getParent() {
        return parent;
    }

    protected abstract BeanReactor<Container> createStrategy();

    protected abstract String getApplicationContextFilter(String symbolicBundleName);

    protected abstract Class<? extends Container> getContainerClass();

    protected static interface BeanReactor<Container> {
        boolean containsBean(Container applicationContext);

        Object createBean(Container applicationContext);
    }

}
