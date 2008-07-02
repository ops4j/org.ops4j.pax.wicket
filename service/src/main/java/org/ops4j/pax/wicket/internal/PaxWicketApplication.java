/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 * Copyright 2008 David Leangen
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
import static org.ops4j.lang.NullArgumentException.validateNotEmpty;
import static org.ops4j.lang.NullArgumentException.validateNotNull;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class PaxWicketApplication extends WebApplication
{

    private final BundleContext m_bundleContext;
    private final String m_applicationName;
    private final PageMounter m_pageMounter;
    private final PaxWicketPageFactory m_pageFactory;
    private final DelegatingClassResolver m_delegatingClassResolver;
    private final List<ServiceRegistration> m_serviceRegistrations;
    protected final Class<? extends Page> m_homepageClass;
    private PageMounterTracker m_mounterTracker;

    public PaxWicketApplication(
        BundleContext context,
        String applicationName,
        PageMounter pageMounter,
        Class<? extends Page> homepageClass,
        PaxWicketPageFactory pageFactory,
        DelegatingClassResolver delegatingClassResolver )
        throws IllegalArgumentException
    {
        validateNotNull( context, "context" );
        validateNotEmpty( applicationName, "applicationName" );
        validateNotNull( homepageClass, "homepageClass" );
        validateNotNull( pageFactory, "pageFactory" );
        validateNotNull( delegatingClassResolver, "delegatingClassResolver" );

        m_bundleContext = context;
        m_applicationName = applicationName;
        m_pageMounter = pageMounter;
        m_pageFactory = pageFactory;
        m_homepageClass = homepageClass;
        m_delegatingClassResolver = delegatingClassResolver;
        m_serviceRegistrations = new ArrayList<ServiceRegistration>();
    }

    @Override
    public final Class<? extends Page> getHomePage()
    {
        return m_homepageClass;
    }

    @Override
    protected final void init()
    {
        super.init();

        IApplicationSettings applicationSettings = getApplicationSettings();
        applicationSettings.setClassResolver( m_delegatingClassResolver );
        addWicketService( IApplicationSettings.class, applicationSettings );

        ISessionSettings sessionSettings = getSessionSettings();
        sessionSettings.setPageFactory( m_pageFactory );
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

        if( m_pageMounter != null )
        {
            for( MountPointInfo bookmark : m_pageMounter.getMountPoints() )
            {
                mount( bookmark.getCodingStrategy() );
            }
        }

        // Now add a tracker so we can still mount pages later
        m_mounterTracker = new PageMounterTracker( m_bundleContext, this, m_applicationName );
        m_mounterTracker.open();
    }

    private <T> void addWicketService( Class<T> service, T serviceImplementation )
    {
        Properties props = new Properties();

        // Note: This is kept for legacy
        props.setProperty( "applicationId", m_applicationName );
        props.setProperty( APPLICATION_NAME, m_applicationName );

        String serviceName = service.getName();
        ServiceRegistration registration =
            m_bundleContext.registerService( serviceName, serviceImplementation, props );
        m_serviceRegistrations.add( registration );
    }

    @Override
    protected void onDestroy()
    {
        if( m_mounterTracker != null )
        {
            m_mounterTracker.close();
            m_mounterTracker = null;
        }

        for( ServiceRegistration reg : m_serviceRegistrations )
        {
            reg.unregister();
        }
        m_serviceRegistrations.clear();

        super.onDestroy();
    }

    @Override
    protected final WebRequest newWebRequest( HttpServletRequest request )
    {
        return new PaxWicketRequest( request );
    }
}
