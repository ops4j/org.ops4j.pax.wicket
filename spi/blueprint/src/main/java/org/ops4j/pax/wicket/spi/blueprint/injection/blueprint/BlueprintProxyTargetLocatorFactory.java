/**
 * 
 */
package org.ops4j.pax.wicket.spi.blueprint.injection.blueprint;

import java.lang.reflect.Field;
import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;

/**
 * creates the actual instance
 */
public class BlueprintProxyTargetLocatorFactory implements ProxyTargetLocatorFactory {

    public String getName() {
        return PaxWicketBean.INJECTION_SOURCE_BLUEPRINT;
    }

    public ProxyTargetLocator createProxyTargetLocator(BundleContext context, Field field, Class<?> page,
            Map<String, String> overwrites) {
        PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
        if (annotation.name().equals("")) {
            // We require a name!
            return null;
        }
        BlueprintBeanProxyTargetLocator locator = new BlueprintBeanProxyTargetLocator(context, annotation,
            field.getType(), page, overwrites);
        if (locator.hasApplicationContext()) {
            return locator;
        } else {
            return null;
        }
    }

}
