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
package org.ops4j.pax.wicket.api.support;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This page is a little bit more complex (compared to the {@link org.ops4j.pax.wicket.api.support.SimpleWebApplicationFactory}). But it is not required
 * to register the OSGi service yourself.
 *
 * In the easiest version
 *
 * @author nmw
 * @version $Id: $Id
 */
public class DefaultWebApplicationFactory<T extends WebApplication> extends SimpleWebApplicationFactory<T> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultWebApplicationFactory.class);

    private BundleContext bundleContext;
    private String applicationName;
    private String mountPoint;
    @SuppressWarnings("rawtypes")
    private ServiceRegistration<WebApplicationFactory> registration;
    private Map<String, String> contextParam = new HashMap<String, String>();

    /**
     * <p>Constructor for DefaultWebApplicationFactory.</p>
     */
    public DefaultWebApplicationFactory() {
        super();
    }

    /**
     * <p>Constructor for DefaultWebApplicationFactory.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param wicketApplication a {@link java.lang.Class} object.
     * @param applicationName a {@link java.lang.String} object.
     */
    public DefaultWebApplicationFactory(BundleContext bundleContext, Class<T> wicketApplication,
            String applicationName) {
        this(bundleContext, wicketApplication, applicationName, applicationName, null);
    }

    /**
     * <p>Constructor for DefaultWebApplicationFactory.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param wicketApplication a {@link java.lang.Class} object.
     * @param applicationName a {@link java.lang.String} object.
     * @param mountPoint a {@link java.lang.String} object.
     */
    public DefaultWebApplicationFactory(BundleContext bundleContext, Class<T> wicketApplication,
            String applicationName, String mountPoint) {
        this(bundleContext, wicketApplication, applicationName, mountPoint, null);
    }

    /**
     * <p>Constructor for DefaultWebApplicationFactory.</p>
     *
     * @param bundleContext a {@link org.osgi.framework.BundleContext} object.
     * @param wicketApplication a {@link java.lang.Class} object.
     * @param applicationName a {@link java.lang.String} object.
     * @param mountPoint a {@link java.lang.String} object.
     * @param contextParam a {@link java.util.Map} object.
     */
    public DefaultWebApplicationFactory(BundleContext bundleContext, Class<T> wicketApplication,
            String applicationName, String mountPoint, Map<String, String> contextParam) {
        super(wicketApplication);
        this.bundleContext = bundleContext;
        this.applicationName = applicationName;
        this.mountPoint = mountPoint;
        this.contextParam = contextParam == null ? new HashMap<String, String>() : contextParam;
    }

    /**
     * <p>register.</p>
     */
    public void register() {
        if (registration != null) {
            throw new IllegalStateException("Webapplication is already registered.");
        }
        validateNotNull(applicationName, "applicationName");
        validateNotNull(mountPoint, "mountPoint");
        Hashtable<String, Object> props = new Hashtable<String, Object>();
        props.put(Constants.APPLICATION_NAME, applicationName);
        props.put(Constants.MOUNTPOINT, mountPoint);
        props.put(Constants.CONTEXT_PARAMS, contextParam);
        registration = bundleContext.registerService(WebApplicationFactory.class, this, props);
    }

    /**
     * <p>dispose.</p>
     */
    public void dispose() {
        if (registration == null) {
            LOGGER.warn("Trying to unregister application {} which is not registered", applicationName);
            return;
        }
        registration.unregister();
        registration = null;
    }

    /**
     * <p>Getter for the field <code>applicationName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getApplicationName() {
        return applicationName;
    }

    /**
     * <p>Setter for the field <code>applicationName</code>.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     */
    public void setApplicationName(String applicationName) {
        if (registration != null) {
            throw new IllegalStateException(
                "It's not allowed to change the application name if the service is already started");
        }
        this.applicationName = applicationName;
    }

    /**
     * <p>Getter for the field <code>mountPoint</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getMountPoint() {
        return mountPoint;
    }

    /**
     * <p>Setter for the field <code>mountPoint</code>.</p>
     *
     * @param mountPoint a {@link java.lang.String} object.
     */
    public void setMountPoint(String mountPoint) {
        if (registration != null) {
            throw new IllegalStateException(
                "It's not allowed to change the mount point if the service is already started");
        }
        this.mountPoint = mountPoint;
    }

}
