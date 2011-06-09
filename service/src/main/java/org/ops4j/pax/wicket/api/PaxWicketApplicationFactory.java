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
package org.ops4j.pax.wicket.api;

import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import static org.ops4j.pax.wicket.api.ContentSource.HOMEPAGE_CLASSNAME;
import static org.ops4j.pax.wicket.api.ContentSource.MOUNTPOINT;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
import java.util.Properties;

import org.apache.wicket.IInitializer;
import org.apache.wicket.Page;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.application.IComponentOnAfterRenderListener;
import org.apache.wicket.application.IComponentOnBeforeRenderListener;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
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
import org.ops4j.pax.wicket.internal.BundleDelegatingClassResolver;
import org.ops4j.pax.wicket.internal.BundleDelegatingComponentInstanciationListener;
import org.ops4j.pax.wicket.internal.ComponentInstantiationListenerFacade;
import org.ops4j.pax.wicket.internal.DelegatingClassResolver;
import org.ops4j.pax.wicket.internal.DelegatingComponentInstanciationListener;
import org.ops4j.pax.wicket.internal.PageMounterTracker;
import org.ops4j.pax.wicket.internal.PaxAuthenticatedWicketApplication;
import org.ops4j.pax.wicket.internal.PaxWicketApplication;
import org.ops4j.pax.wicket.internal.PaxWicketPageFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationException;
import org.osgi.service.cm.ManagedService;

public final class PaxWicketApplicationFactory implements IWebApplicationFactory, ManagedService {

    private static final String[] APPLICATION_FACTORY_SERVICE_NAMES = {
        PaxWicketApplicationFactory.class.getName(), ManagedService.class.getName()
    };

    private final BundleContext m_bundleContext;
    private Class<? extends Page> m_homepageClass;
    private final Properties m_properties;

    private PaxWicketPageFactory m_pageFactory;
    private DelegatingClassResolver m_delegatingClassResolver;
    private DelegatingComponentInstanciationListener m_delegatingComponentInstanciationListener;

    private ServiceRegistration m_registration;

    private PaxWicketAuthenticator m_authenticator;
    private Class<? extends WebPage> m_signinPage;

    private PageMounter m_pageMounter;
    private RequestCycleFactory m_requestCycleFactory;
    private RequestCycleProcessorFactory m_requestCycleProcessorFactory;
    private SessionStoreFactory m_sessionStoreFactory;

    private final List<IComponentOnBeforeRenderListener> m_componentOnBeforeRenderListeners;
    private final List<IComponentOnAfterRenderListener> m_componentOnAfterRenderListeners;
    private final List<IInitializer> m_initializers;

    private final ApplicationFactory m_applicationFactory;

    private ServiceRegistration m_bdcrRegistration;
    private BundleDelegatingClassResolver m_bdcr;
    private ServiceRegistration m_bdciRegistration;
    private BundleDelegatingComponentInstanciationListener m_bdci;

    /**
     * Construct an instance of {@code PaxWicketApplicationFactory} with the specified arguments.
     * 
     * @param context The bundle context. This argument must not be {@code null}.
     * @param homepageClass The homepage class. This argument must not be {@code null}.
     * @param mountPoint The mount point. This argument must not be be {@code null}.
     * @param applicationName The application name. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    public PaxWicketApplicationFactory(
            BundleContext context,
            Class<? extends Page> homepageClass,
            String mountPoint,
            String applicationName)
        throws IllegalArgumentException {
        this(context, homepageClass, mountPoint, applicationName, null);
    }

    /**
     * Construct an instance of {@code PaxWicketApplicationFactory} with the specified arguments.
     * 
     * @param context The bundle context. This argument must not be {@code null}.
     * @param homepageClass The homepage class. This argument must not be {@code null}.
     * @param mountPoint The mount point. This argument must not be be {@code null}.
     * @param applicationName The application name. This argument must not be {@code null}.
     * @param applicationFactory An alternative method to create an application service and maintain almost full control
     *        about the application
     * 
     * @throws IllegalArgumentException Thrown if one or some or all arguments are {@code null}.
     * @since 1.0.0
     */
    public PaxWicketApplicationFactory(
            BundleContext context,
            Class<? extends Page> homepageClass,
            String mountPoint,
            String applicationName, ApplicationFactory applicationFactory)
        throws IllegalArgumentException {
        validateNotNull(context, "context");
        validateNotNull(homepageClass, "homepageClass");
        validateNotNull(mountPoint, "mountPoint");
        validateNotEmpty(applicationName, "applicationName");

        m_properties = new Properties();
        m_applicationFactory = applicationFactory;

        m_homepageClass = homepageClass;
        m_bundleContext = context;

        setMountPoint(mountPoint);
        setApplicationName(applicationName);

        String homepageClassName = homepageClass.getName();
        m_properties.setProperty(HOMEPAGE_CLASSNAME, homepageClassName);

        m_componentOnBeforeRenderListeners = new ArrayList<IComponentOnBeforeRenderListener>();
        m_componentOnAfterRenderListeners = new ArrayList<IComponentOnAfterRenderListener>();
        m_initializers = new ArrayList<IInitializer>();
    }

    /**
     * Sets the authenticator of this pax application factory.
     * <p>
     * Note: Value changed will only affect wicket application created after this method invocation.
     * </p>
     * 
     * @param authenticator The authenticator.
     * @param signInPage The sign in page.
     * 
     * @throws IllegalArgumentException Thrown if one of the arguments are {@code null}.
     * @see #register()
     * @since 1.0.0
     */
    public final void setAuthenticator(PaxWicketAuthenticator authenticator, Class<? extends WebPage> signInPage)
        throws IllegalArgumentException {
        if (authenticator != null && signInPage == null || authenticator == null && signInPage != null) {
            String message = "Both [authenticator] and [signInPage] argument must not be [null].";
            throw new IllegalArgumentException(message);
        }

        m_authenticator = authenticator;
        m_signinPage = signInPage;
    }

    /**
     * Clear resources used by this {@code PaxWicketApplicationFactory} instance.
     * <p>
     * Note: dispose does not unregister this {@code PaxWicketApplicationFactory} instance from the OSGi container.
     * </p>
     * 
     * @since 1.0.0
     */
    public final void dispose() {
        synchronized (this) {
            m_pageFactory.dispose();
            m_delegatingClassResolver.dispose();
            m_delegatingComponentInstanciationListener.dispose();

            if (m_bdcrRegistration != null) {
                m_bdcrRegistration.unregister();
            }
        }
    }

    /**
     * Returns the mount point that the wicket application will be accessible. This method must not return {@code null}
     * string.
     * 
     * @return The mount point that the wicket application will be accessible.
     * 
     * @since 1.0.0
     */
    public final String getMountPoint() {
        synchronized (this) {
            return m_properties.getProperty(MOUNTPOINT);
        }
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public final void updated(Dictionary config)
        throws ConfigurationException {
        if (config == null) {
            synchronized (this) {
                m_registration.setProperties(m_properties);
            }

            return;
        }

        String classname = (String) config.get(HOMEPAGE_CLASSNAME);
        if (classname == null) {
            synchronized (this) {
                String homepageClassName = m_homepageClass.getName();
                config.put(HOMEPAGE_CLASSNAME, homepageClassName);
            }
        } else {
            synchronized (this) {
                try {
                    Bundle bundle = m_bundleContext.getBundle();
                    m_homepageClass = bundle.loadClass(classname);
                } catch (ClassNotFoundException e) {
                    throw new ConfigurationException(HOMEPAGE_CLASSNAME, "Class not found in application bundle.", e);
                }
            }
        }

        String applicationName = (String) config.get(APPLICATION_NAME);
        if (applicationName == null) {
            String currentApplicationName;
            synchronized (this) {
                currentApplicationName = (String) m_properties.get(APPLICATION_NAME);
            }

            config.put(APPLICATION_NAME, currentApplicationName);
        } else {
            setApplicationName(applicationName);
        }

        String mountPoint = (String) config.get(MOUNTPOINT);
        if (mountPoint == null) {
            String currentMountPoint;
            synchronized (this) {
                currentMountPoint = m_properties.getProperty(MOUNTPOINT);
            }
            config.put(MOUNTPOINT, currentMountPoint);
        } else {
            setMountPoint(mountPoint);
        }

        m_registration.setProperties(config);
    }

    /**
     * Register this {@code PaxWicketApplicationFactory} instance to OSGi container.
     * 
     * @return The service registration.
     * 
     * @since 1.0.0
     */
    public final ServiceRegistration register() {
        m_registration = m_bundleContext.registerService(APPLICATION_FACTORY_SERVICE_NAMES, this, m_properties);
        return m_registration;
    }

    private void setApplicationName(String applicationName) {
        synchronized (this) {
            if (m_pageFactory != null) {
                m_pageFactory.dispose();
            }
            if (m_delegatingClassResolver != null) {
                m_delegatingClassResolver.dispose();
            }
            if (m_delegatingComponentInstanciationListener != null) {
                m_delegatingComponentInstanciationListener.dispose();
            }

            m_delegatingClassResolver = new DelegatingClassResolver(m_bundleContext, applicationName);
            m_delegatingClassResolver.intialize();

            m_delegatingComponentInstanciationListener =
                new DelegatingComponentInstanciationListener(m_bundleContext, applicationName);
            m_delegatingComponentInstanciationListener.intialize();

            m_pageFactory = new PaxWicketPageFactory(m_bundleContext, applicationName);
            m_pageFactory.initialize();

            m_properties.setProperty(APPLICATION_NAME, applicationName);
        }
    }

    private void setMountPoint(String mountPoint) {
        synchronized (this) {
            m_properties.put(MOUNTPOINT, mountPoint);
        }
    }

    public final void setPageMounter(PageMounter pageMounter) {
        validateNotNull(pageMounter, "pageMounter");

        m_pageMounter = pageMounter;
    }

    public void setRequestCycleFactory(RequestCycleFactory factory) {
        m_requestCycleFactory = factory;
    }

    public void setRequestCycleProcessorFactory(RequestCycleProcessorFactory factory) {
        m_requestCycleProcessorFactory = factory;
    }

    public void setSessionStoreFactory(SessionStoreFactory sessionStoreFactory) {
        m_sessionStoreFactory = sessionStoreFactory;
    }

    public final void setPaxWicketBundle(Bundle bundle) {
        if (m_bdcrRegistration != null) {
            m_bdcrRegistration.unregister();
            m_bdcrRegistration = null;
            m_bdciRegistration.unregister();
            m_bdciRegistration = null;
        }

        if (m_bdcr != null) {
            m_bdcr.close();
            m_bdci.close();
        }

        if (bundle != null) {
            Properties config = new Properties();
            String applicationName = getApplicationName();
            config.setProperty(APPLICATION_NAME, applicationName);

            m_bdcr = new BundleDelegatingClassResolver(m_bundleContext, applicationName, bundle);
            m_bdcrRegistration = m_bundleContext.registerService(IClassResolver.class.getName(), m_bdcr, config);

            m_bdci = new BundleDelegatingComponentInstanciationListener(m_bundleContext, applicationName, bundle);
            m_bdciRegistration =
                m_bundleContext.registerService(PaxWicketInjector.class.getName(), m_bdci, config);
        }
    }

    /**
     * Returns the application name.
     * 
     * @return The application name.
     * 
     * @since 0.5.4
     */
    private String getApplicationName() {
        return m_properties.getProperty(APPLICATION_NAME);
    }

    public void addComponentOnBeforeRenderListener(IComponentOnBeforeRenderListener listener) {
        m_componentOnBeforeRenderListeners.add(listener);
    }

    public void addComponentOnAfterRenderListener(IComponentOnAfterRenderListener listener) {
        m_componentOnAfterRenderListeners.add(listener);
    }

    public void addInitializer(IInitializer initializer) {
        m_initializers.add(initializer);
    }

    /**
     * Creates a web application.
     * 
     * @param filter The wicket filter.
     * 
     * @return The new web application.
     */
    public final WebApplication createApplication(WicketFilter filter) {
        WebApplication paxWicketApplication;

        synchronized (this) {
            String applicationName = getApplicationName();

            if (m_applicationFactory == null) {
                if (m_authenticator != null && m_signinPage != null) {
                    paxWicketApplication = createPredefinedPaxAuthenticatedWicketApplication(applicationName);
                } else {
                    paxWicketApplication = createPredefinedPaxWicketApplication(applicationName);
                }
            } else {
                paxWicketApplication = createWicketApplicationViaCustomFactory();
            }

            // paxWicketApplication.addComponentInitializationListener(new component)

            for (IComponentOnBeforeRenderListener listener : m_componentOnBeforeRenderListeners) {
                paxWicketApplication.addPostComponentOnBeforeRenderListener(listener);
            }

            for (IComponentOnAfterRenderListener listener : m_componentOnAfterRenderListeners) {
                paxWicketApplication.addComponentOnAfterRenderListener(listener);
            }

            return paxWicketApplication;
        }
    }

    private WebApplication createWicketApplicationViaCustomFactory() {
        WebApplication paxWicketApplication;
        paxWicketApplication = m_applicationFactory.createWebApplication(new ApplicationLifecycleListener() {
            private final List<ServiceRegistration> m_serviceRegistrations =
                new ArrayList<ServiceRegistration>();
            private PageMounterTracker m_mounterTracker;

            public void onInit(WebApplication wicketApplication) {
                wicketApplication.addComponentInstantiationListener(new ComponentInstantiationListenerFacade(
                    m_delegatingComponentInstanciationListener));

                IApplicationSettings applicationSettings = wicketApplication.getApplicationSettings();
                applicationSettings.setClassResolver(m_delegatingClassResolver);
                addWicketService(IApplicationSettings.class, applicationSettings);

                ISessionSettings sessionSettings = wicketApplication.getSessionSettings();
                sessionSettings.setPageFactory(m_pageFactory);
                addWicketService(ISessionSettings.class, sessionSettings);

                addWicketService(IDebugSettings.class, wicketApplication.getDebugSettings());
                addWicketService(IExceptionSettings.class, wicketApplication.getExceptionSettings());
                addWicketService(IFrameworkSettings.class, wicketApplication.getFrameworkSettings());
                addWicketService(IMarkupSettings.class, wicketApplication.getMarkupSettings());
                addWicketService(IPageSettings.class, wicketApplication.getPageSettings());
                addWicketService(IRequestCycleSettings.class, wicketApplication.getRequestCycleSettings());
                addWicketService(IResourceSettings.class, wicketApplication.getResourceSettings());
                addWicketService(ISecuritySettings.class, wicketApplication.getSecuritySettings());

                if (m_pageMounter != null) {
                    for (MountPointInfo bookmark : m_pageMounter.getMountPoints()) {
                        wicketApplication.mount(bookmark.getCodingStrategy());
                    }
                }

                // Now add a tracker so we can still mount pages later
                m_mounterTracker = new PageMounterTracker(m_bundleContext, wicketApplication, getApplicationName());
                m_mounterTracker.open();

                for (final IInitializer initializer : m_initializers) {
                    initializer.init(wicketApplication);
                }
            }

            private <T> void addWicketService(Class<T> service, T serviceImplementation) {
                Properties props = new Properties();

                // Note: This is kept for legacy
                props.setProperty("applicationId", getApplicationName());
                props.setProperty(APPLICATION_NAME, getApplicationName());

                String serviceName = service.getName();
                ServiceRegistration registration =
                    m_bundleContext.registerService(serviceName, serviceImplementation, props);
                m_serviceRegistrations.add(registration);
            }

            public void onDestroy(WebApplication wicketApplication) {
                wicketApplication.removeComponentInstantiationListener(new ComponentInstantiationListenerFacade(
                    m_delegatingComponentInstanciationListener));

                if (m_mounterTracker != null) {
                    m_mounterTracker.close();
                    m_mounterTracker = null;
                }

                for (ServiceRegistration reg : m_serviceRegistrations) {
                    reg.unregister();
                }
                m_serviceRegistrations.clear();
            }
        });
        return paxWicketApplication;
    }

    private WebApplication createPredefinedPaxWicketApplication(String applicationName) {
        WebApplication paxWicketApplication;
        paxWicketApplication =
            new PaxWicketApplication(m_bundleContext, applicationName, m_pageMounter, m_homepageClass, m_pageFactory,
                m_delegatingClassResolver, m_initializers, new ComponentInstantiationListenerFacade(
                    m_delegatingComponentInstanciationListener));
        return paxWicketApplication;
    }

    private WebApplication createPredefinedPaxAuthenticatedWicketApplication(String applicationName) {
        WebApplication paxWicketApplication;
        paxWicketApplication =
            new PaxAuthenticatedWicketApplication(m_bundleContext, applicationName, m_pageMounter, m_homepageClass,
                m_pageFactory, m_requestCycleFactory, m_requestCycleProcessorFactory, m_sessionStoreFactory,
                m_delegatingClassResolver, m_authenticator, m_signinPage, m_initializers,
                new ComponentInstantiationListenerFacade(m_delegatingComponentInstanciationListener));
        return paxWicketApplication;
    }

}
