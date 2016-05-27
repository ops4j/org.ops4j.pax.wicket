
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.spi.support;

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

    private final String beanName;

    /**
     * <p>Constructor for AbstractProxyTargetLocator.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param beanName a {@link java.lang.String} object.
     * @param beanType a {@link java.lang.Class} object.
     * @param parent a {@link java.lang.Class} object.
     * @param overwrites a {@link java.util.Map} object.
     */
    public AbstractProxyTargetLocator(BundleContext bundleContext, String beanName, Class<?> beanType,
            Class<?> parent, Map<String, String> overwrites) {
        this.bundleContext = bundleContext;
        this.beanName = beanName;
        this.beanType = beanType;
        this.parent = parent;
        this.overwrites = overwrites;
    }

    /**
     * <p>hasApplicationContext.</p>
     *
     * @return a boolean.
     */
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

    /**
     * <p>Getter for the field <code>beanName</code>.</p>
     *
     * @return the current value of beanName
     */
    public String getBeanName() {
        return beanName;
    }

    /**
     * <p>locateProxyTarget.</p>
     *
     * @return a {@link org.ops4j.pax.wicket.spi.ProxyTarget} object.
     */
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
            "Bundle %s can no longer attach bean %s with ID %s, class %s to page %s", bundleContext
                .getBundle().getSymbolicName(), beanType.getName(), beanName, beanType.getName(),
            parent.getName()));
    }

    /**
     * <p>Getter for the field <code>parent</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<?> getParent() {
        return parent;
    }

    /**
     * <p>createStrategy.</p>
     *
     * @return a {@link org.ops4j.pax.wicket.spi.support.AbstractProxyTargetLocator.BeanReactor} object.
     */
    protected abstract BeanReactor<Container> createStrategy();

    /**
     * <p>getApplicationContextFilter.</p>
     *
     * @param symbolicBundleName a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    protected abstract String getApplicationContextFilter(String symbolicBundleName);

    /**
     * <p>getContainerClass.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    protected abstract Class<? extends Container> getContainerClass();

    protected static interface BeanReactor<Container> {
        boolean containsBean(Container applicationContext);

        Object createBean(Container applicationContext);
    }

}
