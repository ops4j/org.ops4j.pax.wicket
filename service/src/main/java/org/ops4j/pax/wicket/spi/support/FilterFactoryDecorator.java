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
package org.ops4j.pax.wicket.spi.support;

import java.util.Map;

import javax.servlet.Filter;

import org.ops4j.pax.wicket.api.ConfigurableFilterConfig;
import org.ops4j.pax.wicket.api.FilterFactory;
import org.ops4j.pax.wicket.api.support.AbstractFilterFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FilterFactoryDecorator implements FilterFactory, InjectionAwareDecorator {

    private Class<? extends Filter> filterClass;
    private Integer priority;
    private String applicationName;
    private Map<String, String> initParams;
    private BundleContext bundleContext;
    private DefaultFilterFactory baseFilterFactory;

    public FilterFactoryDecorator() {
    }

    public Integer getPriority() {
        return baseFilterFactory.getPriority();
    }

    public String getApplicationName() {
        return baseFilterFactory.getApplicationName();
    }

    public Filter createFilter(ConfigurableFilterConfig filterConfig) {
        return baseFilterFactory.createFilter(filterConfig);
    }

    public void start() throws Exception {
        baseFilterFactory = new DefaultFilterFactory(bundleContext, filterClass, priority, applicationName, initParams);
        baseFilterFactory.register();
    }

    public void stop() throws Exception {
        baseFilterFactory.dispose();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setFilterClass(Class<? extends Filter> filterClass) {
        this.filterClass = filterClass;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setInitParams(Map<String, String> initParams) {
        this.initParams = initParams;
    }

    private static class DefaultFilterFactory extends AbstractFilterFactory {

        private static final Logger LOGGER = LoggerFactory.getLogger(DefaultFilterFactory.class);

        private final Class<? extends Filter> filterClass;
        private final String applicationName;
        private final Map<String, String> filterConfiguration;

        public DefaultFilterFactory(BundleContext bundleContext, Class<? extends Filter> filterClass, Integer priority,
                String applicationName, Map<String, String> filterConfiguration) {
            super(bundleContext, applicationName, priority);
            this.applicationName = applicationName;
            this.filterClass = filterClass;
            this.filterConfiguration = filterConfiguration;
        }

        public Filter createFilter(ConfigurableFilterConfig filterConfig) {
            ClassLoader oldClassloader = Thread.currentThread().getContextClassLoader();
            try {
                Thread.currentThread().setContextClassLoader(filterClass.getClassLoader());
                LOGGER.info("Creating new instance of {} for application {}", filterClass.getName(), applicationName);
                Filter filter = filterClass.newInstance();
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

    }

}
