package org.ops4j.pax.wicket.internal.spring.contentSourceModelMapping;

import java.io.Serializable;
import java.util.List;

import org.ops4j.pax.wicket.api.ContentSourceDescriptor;
import org.ops4j.pax.wicket.internal.spring.contentSource.ContentSourceFactory;
import org.ops4j.pax.wicket.internal.spring.util.SpringBeanHelper;
import org.osgi.framework.BundleContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextAwareContentSources extends ContentSourceFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ContentSourceDescriptor descriptor;
    private final ConfigurableApplicationContext applicationContext;

    public SpringContextAwareContentSources(BundleContext bundleContext, ContentSourceDescriptor descriptor,
            String applicationName, ConfigurableApplicationContext applicationContext)
        throws IllegalArgumentException {
        super(bundleContext, descriptor.getWicketId(), applicationName, descriptor.getComponentClass());
        this.descriptor = descriptor;
        this.applicationContext = applicationContext;
        setOverwrite(descriptor.getOverwrites());
    }

    @Override
    public void start() {
        List<String> dest = descriptor.getDestinations();
        if (dest != null && dest.size() != 0) {
            super.setDestination(dest.toArray(new String[0]));
        }
        super.register();
        SpringBeanHelper.registerBean(applicationContext, descriptor.getContentSourceId(), this);
    }

    @Override
    public void stop() {
        super.dispose();
    }

}
