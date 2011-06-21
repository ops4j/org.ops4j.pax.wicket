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
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.StringUtils;

public class ApplicationBuilder implements ApplicationContextAware, BundleContextAware {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationFactory.class);

    private BundleContext bundleContext;
    private ApplicationContext applicationContext;
    private Class<? extends Page> homepageClass;
    private String mountPoint;
    private String applicationName;
    private String applicationFactory;
    private Map<String, String> contextParams;

    private PaxWicketApplicationFactory paxWicketApplicationService;
    private ServiceRegistration serviceRegistration;

    public void register() {
        LOGGER
            .info(String
                .format(
                    "Trying to register pax-wicket application with the following settings: bundleContext: %s, applicationContext: %s, "
                            + "homepageClass: %s, mountPoint: %s, applicationName: %s, applicationFactory: %s",
                    bundleContext.toString(), applicationContext.toString(), homepageClass.toString(), mountPoint,
                    applicationName, applicationFactory.toString()));
        LOGGER.trace("Check if applicationFactory exists");
        if (StringUtils.hasText(applicationFactory)) {
            LOGGER.debug("Found applicationFactory; trying to create bean");
            ApplicationFactory realFactory = applicationContext.getBean(applicationFactory, ApplicationFactory.class);
            LOGGER.debug("Created bean; creating paxwicket applicaiton");
            paxWicketApplicationService =
                new PaxWicketApplicationFactory(bundleContext, homepageClass, mountPoint, applicationName, realFactory,
                    contextParams);
        } else {
            LOGGER.debug("No own application factory found; falling back to old method");
            paxWicketApplicationService =
                new PaxWicketApplicationFactory(bundleContext, homepageClass, mountPoint, applicationName);
        }
        serviceRegistration = paxWicketApplicationService.register();
        LOGGER.info("Successfully registered application factory");
    }

    public void unregister() {
        LOGGER
            .info(String
                .format(
                    "Removing pax-wicket application with the following settings: bundleContext: %s, applicationContext: %s, "
                            +
                            "homepageClass: %s, mountPoint: %s, applicationName: %s, applicationFactory: %s",
                    bundleContext.toString(), applicationContext.toString(), homepageClass.toString(), mountPoint,
                    applicationName, applicationFactory.toString()));
        serviceRegistration.unregister();
        paxWicketApplicationService.dispose();
    }

    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
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

    public void setApplicationFactory(String applicationFactory) {
        this.applicationFactory = applicationFactory;
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setContextParams(Map<String, String> contextParams) {
        this.contextParams = contextParams;
    }

}
