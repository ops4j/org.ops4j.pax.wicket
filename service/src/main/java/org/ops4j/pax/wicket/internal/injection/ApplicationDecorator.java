/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.wicket.internal.injection;

import java.util.Map;

import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.util.DefaultWebApplicationFactory;
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
    private DefaultWebApplicationFactory factory;
    private String injectionSource = PaxWicketBean.INJECTION_SOURCE_UNDEFINED;

    public ApplicationDecorator() {
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setApplicationClass(Class<? extends WebApplication> applicationClass) {
        this.applicationClass = applicationClass;
    }

    public void setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
    }

    public void setInjectionSource(String injectionSource) {
        this.injectionSource = injectionSource;
    }

    public void start() throws Exception {
        factory =
            new DefaultWebApplicationFactory(bundleContext, applicationClass, applicationName, mountPoint,
                contextParams);
        factory.register();
        LOGGER.info("Successfully registered application factory");
    }

    public void stop() throws Exception {
        factory.dispose();
        LOGGER.info("Successfully unregistered application factory");
    }

}
