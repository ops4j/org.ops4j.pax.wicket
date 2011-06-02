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

package org.ops4j.pax.wicket.internal.spring.application;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.ApplicationFactory;
import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.osgi.context.BundleContextAware;
import org.springframework.util.StringUtils;

public class ApplicationBuilder implements ApplicationContextAware, BundleContextAware {

    private BundleContext bundleContext;
    private ApplicationContext applicationContext;
    private Class<? extends Page> homepageClass;
    private String mountPoint;
    private String applicationName;
    private String applicationFactory;

    private PaxWicketApplicationFactory paxWicketApplicationService;
    private ServiceRegistration serviceRegistration;

    public void register() {
        System.out.println("=======STARTUP ApplicationBuilder========");
        System.out.println("=======================");
        System.out.println("=======================");
        System.out
            .println(String
                .format(
                    "bundleContext: %s, applicationContext: %s, homepageClass: %s, mountPoint: %s, applicationName: %s, applicationFactory: %s",
                    bundleContext.toString(), applicationContext.toString(), homepageClass.toString(), mountPoint,
                    applicationName, applicationFactory.toString()));
        System.out.println("Check if applicationFactory exists");
        if (StringUtils.hasText(applicationFactory)) {
            System.out.println("Found applicationFactory; trying to create bean");
            ApplicationFactory realFactory = applicationContext.getBean(applicationFactory, ApplicationFactory.class);
            System.out.println("Created bean; creating paxwicket applicaiton");
            paxWicketApplicationService =
                new PaxWicketApplicationFactory(bundleContext, homepageClass, mountPoint, applicationName, realFactory);
        } else {
            System.out.println("No own application factory found; falling back to old method");
            paxWicketApplicationService =
                new PaxWicketApplicationFactory(bundleContext, homepageClass, mountPoint, applicationName);
        }
        serviceRegistration = paxWicketApplicationService.register();
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println("=======================");
    }

    public void unregister() {
        System.out.println("=======Kill ApplicationBuilder========");
        System.out.println("=======================");
        System.out.println("=======================");
        System.out
            .println(String
                .format(
                    "bundleContext: %s, applicationContext: %s, homepageClass: %s, mountPoint: %s, applicationName: %s, applicationFactory: %s",
                    bundleContext.toString(), applicationContext.toString(), homepageClass.toString(), mountPoint,
                    applicationName, applicationFactory.toString()));
        System.out.println("Unregister service and dispose application factory");
        serviceRegistration.unregister();
        paxWicketApplicationService.dispose();
        System.out.println("=======================");
        System.out.println("=======================");
        System.out.println("=======================");
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

}
