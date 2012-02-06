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

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.List;

import org.apache.wicket.Page;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultPageMounter implements PageMounter, ManagedService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPageMounter.class);

    private final List<MountPointInfo> mountPoints;
    private final Dictionary<String, String> properties;
    private final BundleContext bundleContext;

    private ServiceRegistration serviceRegistration;

    public DefaultPageMounter(String applicationName, BundleContext bundleContext) {
        validateNotNull(bundleContext, "bundleContext");
        LOGGER.trace("Initializing MountTracker for {}", applicationName);
        mountPoints = new ArrayList<MountPointInfo>();
        properties = new Hashtable<String, String>();
        this.bundleContext = bundleContext;
        setApplicationName(applicationName);
    }

    /**
     * Automatically regsiteres the {@link PageMounter} as OSGi service
     */
    public void register() {
        LOGGER.debug("Register mount tracker as OSGi service");
        String[] classes = { PageMounter.class.getName(), ManagedService.class.getName() };
        synchronized (this) {
            if (serviceRegistration != null) {
                throw new IllegalStateException(String.format("%s [%s] had been already registered.", getClass()
                    .getSimpleName(), this));
            }
            serviceRegistration = bundleContext.registerService(classes, this, properties);
        }
    }

    /**
     * Automatically unregister the {@link PageMounter} from the OSGi registry
     */
    public void dispose() {
        synchronized (this) {
            if (serviceRegistration == null) {
                throw new IllegalStateException(String.format("%s [%s] had not been registered.", getClass()
                    .getSimpleName(), this));
            }
            serviceRegistration.unregister();
            serviceRegistration = null;
        }
    }

    @SuppressWarnings("rawtypes")
    public void updated(Dictionary properties) throws ConfigurationException {
        if (properties != null) {
            setApplicationName((String) properties.get(APPLICATION_NAME));
        }
        synchronized (this) {
            serviceRegistration.setProperties(properties);
        }
    }

    public void setApplicationName(String applicationName) {
        validateNotEmpty(applicationName, "applicationName");
        synchronized (this) {
            properties.put(APPLICATION_NAME, applicationName);
        }
    }

    public String getApplicationName() {
        synchronized (this) {
            return properties.get(APPLICATION_NAME);
        }
    }

    /**
     * A convenience method that uses a default coding strategy.
     * 
     * @param path the path on which the page is to be mounted
     * @param pageClass the class to mount on this mount point using the default strategy
     */
    public void addMountPoint(String path, Class<? extends Page> pageClass) {
        LOGGER.debug("Adding mount point for path {} = {}", path, pageClass.getName());
        mountPoints.add(new DefaultMountPointInfo(path, pageClass));
    }

    public final List<MountPointInfo> getMountPoints() {
        return Collections.unmodifiableList(mountPoints);
    }

    private static class DefaultMountPointInfo implements MountPointInfo {
        private final String path;
        private final Class<? extends Page> pageClass;

        private DefaultMountPointInfo(String path, Class<? extends Page> pageClass) {
            validateNotEmpty(path, "path");
            validateNotNull(pageClass, "pageClass");
            this.path = path;
            this.pageClass = pageClass;
        }

        public final String getPath() {
            return path;
        }

        public final Class<? extends Page> getPage() {
            return pageClass;
        }
    }

}
