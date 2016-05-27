
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.spi.support;

import java.util.Map;

import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.support.DefaultWebApplicationFactory;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class ApplicationDecorator implements InjectionAwareDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationDecorator.class);

    private BundleContext bundleContext;
    private String mountPoint;
    private String applicationName;
    private Class<? extends WebApplication> applicationClass;
    private Map<String, String> contextParams;
    private DefaultWebApplicationFactory<?> factory;
    private String injectionSource;

    /**
     * <p>Constructor for ApplicationDecorator.</p>
     */
    public ApplicationDecorator() {
    }

    /** {@inheritDoc} */
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * <p>Setter for the field <code>mountPoint</code>.</p>
     *
     * @param mountPoint a {@link java.lang.String} object.
     */
    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    /**
     * <p>Setter for the field <code>applicationName</code>.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * <p>Setter for the field <code>applicationClass</code>.</p>
     *
     * @param applicationClass a {@link java.lang.Class} object.
     */
    public void setApplicationClass(Class<? extends WebApplication> applicationClass) {
        this.applicationClass = applicationClass;
    }

    /**
     * <p>Setter for the field <code>contextParams</code>.</p>
     *
     * @param contextParams a {@link java.util.Map} object.
     */
    public void setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
    }

    /**
     * <p>Setter for the field <code>injectionSource</code>.</p>
     *
     * @param injectionSource a {@link java.lang.String} object.
     */
    public void setInjectionSource(String injectionSource) {
        this.injectionSource = injectionSource;
    }

    /**
     * <p>Getter for the field <code>injectionSource</code>.</p>
     *
     * @return the current value of injectionSource
     */
    public String getInjectionSource() {
        // TODO: This must be used!
        return injectionSource;
    }

    /**
     * <p>start.</p>
     *
     * @throws java.lang.Exception if any.
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void start() throws Exception {
        factory =
            new DefaultWebApplicationFactory(bundleContext, applicationClass, applicationName, mountPoint,
                contextParams);
        factory.register();
        LOGGER.info("Successfully registered application factory");
    }

    /**
     * <p>stop.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void stop() throws Exception {
        factory.dispose();
        LOGGER.info("Successfully unregistered application factory");
    }

}
