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
package org.ops4j.pax.wicket.internal.injection;

import java.util.Map;

import javax.servlet.Filter;

import org.ops4j.pax.wicket.api.ConfigurableFilterConfig;
import org.ops4j.pax.wicket.util.AbstractFilterFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;

public class DefaultFilterFactory extends AbstractFilterFactory implements ApplicationContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilterFactory.class);

    private Class<? extends Filter> filterClass;
    private String applicationName;
    private ApplicationContext applicationContext;
    private Map<String, String> filterConfiguration;

    public DefaultFilterFactory(BundleContext bundleContext, Class<? extends Filter> filterClass, Integer priority,
            String applicationName, Map<String, String> filterConfiguration) {
        super(bundleContext, applicationName, priority);
        this.applicationName = applicationName;
        this.filterClass = filterClass;
        this.filterConfiguration = filterConfiguration;
    }

    public void start() {
        register();
    }

    public void stop() {
        dispose();
    }

    public Filter createFilter(ConfigurableFilterConfig filterConfig) {
        ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(filterClass.getClassLoader());
            LOGGER.info("Creating new instance of {} for application {}", filterClass.getName(), applicationName);
            Filter filter = filterClass.newInstance();
            initFilter(filter);
            filterConfig.putAllInitParameter(filterConfiguration);
            filter.init(filterConfig);
            return filter;
        } catch (Exception e) {
            throw new IllegalStateException(String.format("Filter %s could not be created for application {}",
                filterClass.getName(), applicationName), e);
        } finally {
            Thread.currentThread().setContextClassLoader(oldClassloader);
        }
    }

    private void initFilter(Filter filter) {
        if (filter instanceof ApplicationContextAware) {
            ((ApplicationContextAware) filter).setApplicationContext(applicationContext);
        }
        if (filter instanceof BundleContextAware) {
            ((BundleContextAware) filter).setBundleContext(getBundleContext());
        }
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

}
