package org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping;

import org.ops4j.pax.wicket.internal.spring.SpringBeanHelper;
import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.osgi.framework.BundleContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextAwareContentAggregator extends RootContentAggregator {

    private final String beanId;
    private final ConfigurableApplicationContext applicationContext;

    public SpringContextAwareContentAggregator(BundleContext bundleContext, String applicationName,
            String aggregationPointName, String beanId, ConfigurableApplicationContext applicationContext) {
        super(bundleContext, applicationName, aggregationPointName);
        this.beanId = beanId;
        this.applicationContext = applicationContext;
    }

    public void start() {
        super.register();
        SpringBeanHelper.registerBean(applicationContext, beanId, this);
    }

    public void stop() {
        super.dispose();
    }

}
