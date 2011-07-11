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
package org.ops4j.pax.wicket.internal;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.wicket.IInitializer;
import org.apache.wicket.Page;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.settings.IApplicationSettings;
import org.apache.wicket.settings.IDebugSettings;
import org.apache.wicket.settings.IExceptionSettings;
import org.apache.wicket.settings.IFrameworkSettings;
import org.apache.wicket.settings.IMarkupSettings;
import org.apache.wicket.settings.IPageSettings;
import org.apache.wicket.settings.IRequestCycleSettings;
import org.apache.wicket.settings.IResourceSettings;
import org.apache.wicket.settings.ISecuritySettings;
import org.apache.wicket.settings.ISessionSettings;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class PaxWicketApplication extends WebApplication {

    private final BundleContext bundleContext;
    private final String applicationName;
    private final PageMounter pageMounter;
    private final PaxWicketPageFactory pageFactory;
    private final DelegatingClassResolver delegatingClassResolver;
    private final List<ServiceRegistration> serviceRegistrations;
    protected final Class<? extends Page> homepageClass;
    private PageMounterTracker mounterTracker;
    private final List<IInitializer> initializers;
    private IComponentInstantiationListener instanciationListener;

    public PaxWicketApplication(
            BundleContext context,
            String applicationName,
            PageMounter pageMounter,
            Class<? extends Page> homepageClass,
            PaxWicketPageFactory pageFactory,
            DelegatingClassResolver delegatingClassResolver,
            List<IInitializer> initializers,
            IComponentInstantiationListener instanciationListener)
        throws IllegalArgumentException {
        validateNotNull(context, "context");
        validateNotEmpty(applicationName, "applicationName");
        validateNotNull(homepageClass, "homepageClass");
        validateNotNull(pageFactory, "pageFactory");
        validateNotNull(delegatingClassResolver, "delegatingClassResolver");
        validateNotNull(initializers, "initializers");
        validateNotNull(instanciationListener, "instanciationListener");
        bundleContext = context;
        this.applicationName = applicationName;
        this.pageMounter = pageMounter;
        this.pageFactory = pageFactory;
        this.homepageClass = homepageClass;
        this.delegatingClassResolver = delegatingClassResolver;
        serviceRegistrations = new ArrayList<ServiceRegistration>();
        this.initializers = new ArrayList<IInitializer>();
        this.initializers.addAll(initializers);
        this.instanciationListener = instanciationListener;
    }

    @Override
    public final Class<? extends Page> getHomePage() {
        return homepageClass;
    }

    @Override
    protected final void init() {
        super.init();

        addComponentInstantiationListener(instanciationListener);

        IApplicationSettings applicationSettings = getApplicationSettings();
        applicationSettings.setClassResolver(delegatingClassResolver);
        addWicketService(IApplicationSettings.class, applicationSettings);

        ISessionSettings sessionSettings = getSessionSettings();
        sessionSettings.setPageFactory(pageFactory);
        addWicketService(ISessionSettings.class, sessionSettings);

        // addWicketService( IAjaxSettings.class, getAjaxSettings() );
        addWicketService(IDebugSettings.class, getDebugSettings());
        addWicketService(IExceptionSettings.class, getExceptionSettings());
        addWicketService(IFrameworkSettings.class, getFrameworkSettings());
        addWicketService(IMarkupSettings.class, getMarkupSettings());
        addWicketService(IPageSettings.class, getPageSettings());
        addWicketService(IRequestCycleSettings.class, getRequestCycleSettings());
        addWicketService(IResourceSettings.class, getResourceSettings());
        addWicketService(ISecuritySettings.class, getSecuritySettings());

        if (pageMounter != null) {
            for (MountPointInfo bookmark : pageMounter.getMountPoints()) {
                mount(bookmark.getCodingStrategy());
            }
        }

        // Now add a tracker so we can still mount pages later
        mounterTracker = new PageMounterTracker(bundleContext, this, applicationName);
        mounterTracker.open();

        for (final IInitializer initializer : initializers) {
            initializer.init(this);
        }
    }

    private <T> void addWicketService(Class<T> service, T serviceImplementation) {
        Properties props = new Properties();

        // Note: This is kept for legacy
        props.setProperty("applicationId", applicationName);
        props.setProperty(APPLICATION_NAME, applicationName);

        String serviceName = service.getName();
        ServiceRegistration registration =
            bundleContext.registerService(serviceName, serviceImplementation, props);
        serviceRegistrations.add(registration);
    }

    @Override
    protected void onDestroy() {
        removeComponentInstantiationListener(instanciationListener);

        if (mounterTracker != null) {
            mounterTracker.close();
            mounterTracker = null;
        }

        for (ServiceRegistration reg : serviceRegistrations) {
            reg.unregister();
        }
        serviceRegistrations.clear();

        super.onDestroy();
    }
}
