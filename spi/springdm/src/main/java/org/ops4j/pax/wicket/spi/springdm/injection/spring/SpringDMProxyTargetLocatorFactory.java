/**
 * 
 */
package org.ops4j.pax.wicket.spi.springdm.injection.spring;

import java.lang.reflect.Field;
import java.util.Map;

import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.BundleContext;

/**
 * @author Christoph Läubrich
 * 
 */
public class SpringDMProxyTargetLocatorFactory implements ProxyTargetLocatorFactory {

    public String getName() {
        return PaxWicketBean.INJECTION_SOURCE_SPRING;
    }

    public ProxyTargetLocator createProxyTargetLocator(BundleContext context, Field field, Class<?> page,
            Map<String, String> overwrites) {
        SpringBeanProxyTargetLocator locator =
            new SpringBeanProxyTargetLocator(context, field.getAnnotation(PaxWicketBean.class), field.getType(),
                page, overwrites);
        if (locator.hasApplicationContext()) {
            return locator;
        } else {
            return null;
        }
    }

}
