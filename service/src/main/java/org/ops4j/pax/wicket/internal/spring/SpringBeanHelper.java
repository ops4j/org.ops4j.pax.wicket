package org.ops4j.pax.wicket.internal.spring;

import org.osgi.framework.Constants;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;

public final class SpringBeanHelper {

    public static void registerBean(ConfigurableApplicationContext applicationContext, final String beanId,
            final Object beanInstance) {
        applicationContext.getBeanFactory().registerSingleton(beanId, beanInstance);
    }

    public static String getApplicationContextFilter(String symbolicBundleName) {
        return String.format("(&(%s=%s)(%s=%s))", Constants.BUNDLE_SYMBOLICNAME, symbolicBundleName,
            Constants.OBJECTCLASS, ApplicationContext.class.getName());
    }

}
