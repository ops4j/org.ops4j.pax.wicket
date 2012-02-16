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
package org.ops4j.pax.wicket.util;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.Constants;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This page is a little bit more complex (compared to the {@link SimpleWebApplicationFactory}). But it is not required
 * to register the OSGi service yourself.
 *
 * In the easiest version
 */
public class DefaultWebApplicationFactory extends SimpleWebApplicationFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebApplicationFactory.class);

    private BundleContext bundleContext;
    private String applicationName;
    private String mountPoint;
    private ServiceRegistration registration;
    private Map<String, String> contextParam = new HashMap<String, String>();

    public DefaultWebApplicationFactory() {
        super();
    }

    public DefaultWebApplicationFactory(BundleContext bundleContext, Class<? extends WebApplication> wicketApplication,
            String applicationName) {
        this(bundleContext, wicketApplication, applicationName, applicationName, null);
    }

    public DefaultWebApplicationFactory(BundleContext bundleContext, Class<? extends WebApplication> wicketApplication,
            String applicationName, String mountPoint) {
        this(bundleContext, wicketApplication, applicationName, mountPoint, null);
    }

    public DefaultWebApplicationFactory(BundleContext bundleContext, Class<? extends WebApplication> wicketApplication,
            String applicationName, String mountPoint, Map<String, String> contextParam) {
        super(wicketApplication);
        this.bundleContext = bundleContext;
        this.applicationName = applicationName;
        this.mountPoint = mountPoint;
        this.contextParam = contextParam == null ? new HashMap<String, String>() : contextParam;
    }

    public void register() {
        if (registration != null) {
            throw new IllegalStateException("Webapplication is already registered.");
        }
        validateNotNull(applicationName, "applicationName");
        validateNotNull(mountPoint, "mountPoint");
        Properties props = new Properties();
        props.put(Constants.APPLICATION_NAME, applicationName);
        props.put(Constants.MOUNTPOINT, mountPoint);
        props.put(Constants.CONTEXT_PARAMS, contextParam);
        registration = bundleContext.registerService(IWebApplicationFactory.class.getName(), this, props);
    }

    public void dispose() {
        if (registration == null) {
            LOGGER.warn("Trying to unregister application {} which is not registered", applicationName);
            return;
        }
        registration.unregister();
        registration = null;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public void setApplicationName(String applicationName) {
        if (registration != null) {
            throw new IllegalStateException(
                "It's not allowed to change the application name if the service is already started");
        }
        this.applicationName = applicationName;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public void setMountPoint(String mountPoint) {
        if (registration != null) {
            throw new IllegalStateException(
                "It's not allowed to change the mount point if the service is already started");
        }
        this.mountPoint = mountPoint;
    }

}
