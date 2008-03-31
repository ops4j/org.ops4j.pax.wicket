/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
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
package org.ops4j.pax.wicket.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Page;
import org.apache.wicket.application.IClassResolver;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebRequest;
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
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.ops4j.pax.wicket.api.ContentSource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class PaxWicketApplication extends WebApplication
{

    private final BundleContext m_bundleContext;
    private final String m_applicationName;
    private final String m_mountPoint;
    private final PageMounter m_pageMounter;
    protected Class<? extends Page> m_homepageClass;
    private PaxWicketPageFactory m_factory;
    private DelegatingClassResolver m_delegatingClassResolver;
    private final List<ServiceRegistration> m_serviceRegistrations;

    public PaxWicketApplication(
        BundleContext bundleContext,
        String applicationName,
        String mountPoint,
        PageMounter pageMounter,
        Class<? extends Page> homepageClass,
        PaxWicketPageFactory factory,
        DelegatingClassResolver delegatingClassResolver )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( mountPoint, "mountPoint" );
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );
        NullArgumentException.validateNotNull( factory, "factory" );
        NullArgumentException.validateNotNull( delegatingClassResolver, "delegatingClassResolver" );

        m_bundleContext = bundleContext;
        m_applicationName = applicationName;
        m_mountPoint = mountPoint;
        m_pageMounter = pageMounter;
        m_factory = factory;
        m_homepageClass = homepageClass;
        m_delegatingClassResolver = delegatingClassResolver;
        m_serviceRegistrations = new ArrayList<ServiceRegistration>();
    }

    /**
     * Application subclasses must specify a home page class by implementing this abstract method.
     *
     * @return Home page class for this application
     */
    @Override
    public final Class<? extends Page> getHomePage()
    {
        return m_homepageClass;
    }

    /**
     * Initialize; if you need the wicket servlet for initialization, e.g. because you want to read an initParameter
     * from web.xml or you want to read a resource from the servlet's context path, you can override this method and
     * provide custom initialization. This method is called right after this application class is constructed, and the
     * wicket servlet is set. <strong>Use this method for any application setup instead of the constructor.</strong>
     */
    protected final void init()
    {
        super.init();
        IApplicationSettings applicationSettings = getApplicationSettings();
        applicationSettings.setClassResolver( m_delegatingClassResolver );
        addWicketService( IApplicationSettings.class, applicationSettings );

        ISessionSettings sessionSettings = getSessionSettings();
        sessionSettings.setPageFactory( m_factory );
        addWicketService( ISessionSettings.class, sessionSettings );

//        addWicketService( IAjaxSettings.class, getAjaxSettings() );
        addWicketService( IDebugSettings.class, getDebugSettings() );
        addWicketService( IExceptionSettings.class, getExceptionSettings() );
        addWicketService( IFrameworkSettings.class, getFrameworkSettings() );
        addWicketService( IMarkupSettings.class, getMarkupSettings() );
        addWicketService( IPageSettings.class, getPageSettings() );
        addWicketService( IRequestCycleSettings.class, getRequestCycleSettings() );
        addWicketService( IResourceSettings.class, getResourceSettings() );
        addWicketService( ISecuritySettings.class, getSecuritySettings() );

        if( null != m_pageMounter )
        {
            for( MountPointInfo bookmark : m_pageMounter.getMountPoints() )
            {
                mount( bookmark.getCodingStrategy() );
            }
        }
    }

    /**
     * Create a new WebRequest. Subclasses of WebRequest could e.g. decode and obfuscated URL which has been encoded by
     * an appropriate WebResponse.
     *
     * @param servletRequest The servlet request
     *
     * @return a WebRequest object.
     */
    @Override
    protected final WebRequest newWebRequest( final HttpServletRequest servletRequest )
    {
        return new PaxWicketRequest( m_mountPoint, servletRequest );
    }

    private <T> void addWicketService( final Class<T> service, final T implementation )
    {
        Properties props = new Properties();
        props.setProperty( "applicationId", m_applicationName );

        String serviceName = service.getName();
        ServiceRegistration registration = m_bundleContext.registerService( serviceName, implementation, props );
        m_serviceRegistrations.add( registration );
    }

    /** Called by Wicket when the Application is being destroyed and taken down.
     *
     */
    @Override protected void onDestroy()
    {
        for( ServiceRegistration reg : m_serviceRegistrations )
        {
            reg.unregister();
        }
    }
}
