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
package org.ops4j.pax.wicket.internal.injection.spring;

import java.io.Serializable;
import java.util.List;

import org.ops4j.pax.wicket.api.ContentSourceDescriptor;
import org.osgi.framework.BundleContext;
import org.springframework.context.ConfigurableApplicationContext;

public class SpringContextAwareContentSources extends ContentSourceFactory implements Serializable {

    private static final long serialVersionUID = 1L;

    private final ContentSourceDescriptor descriptor;
    private final ConfigurableApplicationContext applicationContext;

    public SpringContextAwareContentSources(BundleContext bundleContext, ContentSourceDescriptor descriptor,
            String applicationName, ConfigurableApplicationContext applicationContext)
        throws IllegalArgumentException {
        super(bundleContext, descriptor.getOverwrites(), descriptor.getWicketId(), applicationName, descriptor
            .getComponentClass(), descriptor.getDestinations());
        this.descriptor = descriptor;
        this.applicationContext = applicationContext;
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
