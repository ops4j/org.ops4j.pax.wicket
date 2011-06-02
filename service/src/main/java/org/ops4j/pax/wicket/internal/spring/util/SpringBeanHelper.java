package org.ops4j.pax.wicket.internal.spring.util;

import org.springframework.context.ConfigurableApplicationContext;

public final class SpringBeanHelper {

    public static void registerBean(ConfigurableApplicationContext applicationContext, final String beanId,
            final Object beanInstance) {
        applicationContext.getBeanFactory().registerSingleton(beanId, beanInstance);
    }

}
