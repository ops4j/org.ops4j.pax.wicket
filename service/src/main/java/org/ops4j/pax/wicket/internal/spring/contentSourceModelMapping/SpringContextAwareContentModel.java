package org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping;

import org.ops4j.pax.wicket.internal.spring.util.SpringBeanHelper;
import org.osgi.framework.BundleContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextAwareContentModel {

    private String beanId;
    private Object model;
    private final ConfigurableApplicationContext applicationContext;

    public SpringContextAwareContentModel(String beanId, Object model, BundleContext bundleContext,
            ConfigurableApplicationContext applicationContext) {
        this.beanId = beanId;
        this.model = model;
        this.applicationContext = applicationContext;
    }

    public void start() {
        SpringBeanHelper.registerBean(applicationContext, beanId, model);
    }

    public void stop() {
        // nothing to do here
    }

}
