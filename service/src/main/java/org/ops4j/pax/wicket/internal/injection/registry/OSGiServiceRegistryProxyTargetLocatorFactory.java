/**
 * 
 */
package org.ops4j.pax.wicket.internal.injection.registry;

import java.lang.reflect.Field;
import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;

public class OSGiServiceRegistryProxyTargetLocatorFactory implements ProxyTargetLocatorFactory {

    private final BundleContext paxBundleContext;

    public OSGiServiceRegistryProxyTargetLocatorFactory(BundleContext paxBundleContext) {
        this.paxBundleContext = paxBundleContext;
    }

    public String getName() {
        return PaxWicketBean.INJECTION_SOURCE_SERVICE_REGISTRY;
    }

    public ProxyTargetLocator createProxyTargetLocator(BundleContext context, Field field, Class<?> page,
            Map<String, String> overwrites) {
        OSGiServiceRegistryProxyTargetLocator locator =
            new OSGiServiceRegistryProxyTargetLocator(context, field.getAnnotation(PaxWicketBean.class),
                field.getType(), page);
        if (locator.fetchReferences() != null) {
            return locator;
        } else {
            return null;
        }
    }

}
