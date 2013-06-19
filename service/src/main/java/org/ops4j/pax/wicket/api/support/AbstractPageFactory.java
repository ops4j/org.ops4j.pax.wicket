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

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.ops4j.pax.wicket.api.Constants.PAGE_ID;
import static org.ops4j.pax.wicket.api.Constants.PAGE_NAME;

import java.util.Dictionary;
import java.util.Hashtable;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.WebPage;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.PaxWicketMountPoint;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public abstract class AbstractPageFactory<T extends Page> implements PageFactory<T>, ManagedService {

    private BundleContext bundleContext;
    private final Hashtable<String, String> properties = new Hashtable<String, String>();
    private Class<? extends WebPage> pageClass;

    private static final String[] SERVICE_CLASSES = { PageFactory.class.getName(), ManagedService.class.getName() };

    private ServiceRegistration<?> pageServiceRegistration;
    private DefaultPageMounter mountPointRegistration;

    /**
     * While this constructor does not require a pageClass it will NOT analyse the loaded classes automatically for the
     * {@link PaxWicketMountPoint} annotation.
     */
    protected AbstractPageFactory(BundleContext bundleContext, String pageId, String applicationName, String pageName)
        throws IllegalArgumentException {
        this(bundleContext, pageId, applicationName, pageName, null);
    }

    /**
     * This constructor expects an {@link #pageClass}. This class is expected to be the exported page and is
     * automatically scanned for the {@link PaxWicketMountPoint} annotation. The found page is automatically exported as
     * service mounting the page on the defined place.
     */
    protected AbstractPageFactory(BundleContext bundleContext, String pageId, String applicationName, String pageName,
            Class<? extends WebPage> pageClass) throws IllegalArgumentException {
        // bundle context could be temporary null; some situations only allow to retrieve it lazy
        validateNotEmpty(pageId, "pageId");
        validateNotEmpty(applicationName, "applicationName");
        validateNotEmpty(pageName, "pageName");

        this.pageClass = pageClass;
        setInternalBundleContext(bundleContext);
        setPageId(pageId);
        setApplicationName(applicationName);
        setPageName(pageName);
    }

    public final void register() throws IllegalStateException {
        validateNotNull(bundleContext, "bundleContext");

        synchronized (this) {
            if (pageServiceRegistration != null) {
                throw new IllegalStateException(String.format("%s [%s] has been registered.", getClass()
                    .getSimpleName(), this));
            }
            if (pageClass != null) {
                PaxWicketMountPoint mountPoint = pageClass.getAnnotation(PaxWicketMountPoint.class);
                if (mountPoint != null) {
                    mountPointRegistration = new DefaultPageMounter(getApplicationName(), bundleContext);
                    mountPointRegistration.addMountPoint(mountPoint.mountPoint(), pageClass);
                    mountPointRegistration.register();
                }
            }
            pageServiceRegistration = bundleContext.registerService(SERVICE_CLASSES, this, properties);
        }
    }

    public final void dispose() throws IllegalStateException {
        synchronized (this) {
            if (pageServiceRegistration == null) {
                throw new IllegalStateException(String.format("%s [%s] has not been registered.", getClass()
                    .getSimpleName(), this));
            }
            pageServiceRegistration.unregister();
            pageServiceRegistration = null;
            if (mountPointRegistration != null) {
                mountPointRegistration.dispose();
                mountPointRegistration = null;
            }
        }
    }

    /**
     * Returns the application name.
     * 
     * @return The application name.
     * 
     * @since 1.0.0
     */
    public final String getApplicationName() {
        synchronized (this) {
            return properties.get(APPLICATION_NAME);
        }
    }

    /**
     * Returns the page name.
     * 
     * @return The page name.
     * 
     * @since 1.0.0
     */
    public final String getPageName() {
        synchronized (this) {
            return properties.get(PAGE_NAME);
        }
    }

    public void updated(Dictionary<String, ?> config)
        throws ConfigurationException {
        if (config == null) {
            synchronized (this) {
                pageServiceRegistration.setProperties(properties);
            }

            return;
        }

        String pagename = (String) config.get(PAGE_NAME);
        String appname = (String) config.get(APPLICATION_NAME);
        setPageName(pagename);
        setApplicationName(appname);
        synchronized (this) {
            pageServiceRegistration.setProperties(config);
        }
    }

    /**
     * Sets the application name.
     * 
     * @param applicationName The application name. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code applicationName} is {@code null}.
     * @since 1.0.0
     */
    protected final void setApplicationName(String applicationName) throws IllegalArgumentException {
        validateNotEmpty(applicationName, "applicationName");
        synchronized (this) {
            properties.put(APPLICATION_NAME, applicationName);
        }
    }

    protected final void setPageId(String pageId) throws IllegalArgumentException {
        validateNotEmpty(pageId, "pageId");
        synchronized (this) {
            properties.put(Constants.SERVICE_PID, PAGE_ID + "/" + pageId);
        }
    }

    /**
     * Set the page name.
     * 
     * @param pageName The page name. This argument must not be {@code null} or empty.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code pageName} arguments are {@code null}.
     * @since 1.0.0
     */
    protected final void setPageName(String pageName) throws IllegalArgumentException {
        validateNotEmpty(pageName, "pageName");
        synchronized (this) {
            properties.put(PAGE_NAME, pageName);
        }
    }

    protected final void setInternalBundleContext(BundleContext bundleContext) throws IllegalArgumentException {
        this.bundleContext = bundleContext;
    }

}
