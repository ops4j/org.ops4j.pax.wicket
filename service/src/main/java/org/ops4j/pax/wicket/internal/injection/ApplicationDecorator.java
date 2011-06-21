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

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.ApplicationFactory;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApplicationDecorator implements InjectionAwareDecorator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationFactory.class);

    private BundleContext bundleContext;
    private Class<? extends Page> homepageClass;
    private String mountPoint;
    private String applicationName;
    private ApplicationFactory applicationFactory;
    private Map<String, String> contextParams;
    private PaxWicketApplicationFactory paxWicketApplicationService;
    private ServiceRegistration serviceRegistration;

    public ApplicationDecorator() {
    }

    public void setHomepageClass(Class<? extends Page> homepageClass) {
        this.homepageClass = homepageClass;
    }

    public void setMountPoint(String mountPoint) {
        this.mountPoint = mountPoint;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setApplicationFactory(ApplicationFactory applicationFactory) {
        this.applicationFactory = applicationFactory;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
    }

    public void start() throws Exception {
        LOGGER.info("Trying to register pax-wicket application with the following settings: bundleContext: {}, "
                + "homepageClass: {}, mountPoint: {}, applicationName: {}, applicationFactory: {}",
            new Object[]{ bundleContext.toString(), homepageClass.toString(), mountPoint, applicationName,
                applicationFactory.toString() });
        LOGGER.trace("Check if applicationFactory exists");
        if (applicationFactory != null) {
            LOGGER.debug("ApplicationFactory is provided; creating paxwicket applicaiton");
            paxWicketApplicationService =
                new PaxWicketApplicationFactory(bundleContext, homepageClass, mountPoint, applicationName,
                    applicationFactory, contextParams);
        } else {
            LOGGER.debug("No own application factory found; falling back to old method");
            paxWicketApplicationService =
                new PaxWicketApplicationFactory(bundleContext, homepageClass, mountPoint, applicationName);
        }
        serviceRegistration = paxWicketApplicationService.register();
        LOGGER.info("Successfully registered application factory");
    }

    public void stop() throws Exception {
        LOGGER
            .info("Removing pax-wicket application with the following settings: bundleContext: {}, homepageClass: {}, "
                    + "mountPoint: {}, applicationName: {}, applicationFactory: {}",
                new Object[]{ bundleContext.toString(), homepageClass.toString(), mountPoint, applicationName,
                    applicationFactory.toString() });
        serviceRegistration.unregister();
        paxWicketApplicationService.dispose();
    }

}
