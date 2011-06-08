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
