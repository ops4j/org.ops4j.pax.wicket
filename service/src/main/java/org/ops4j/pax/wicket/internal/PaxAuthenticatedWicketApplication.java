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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.protocol.http.WebRequest;
import org.apache.wicket.request.IRequestCycleProcessor;
import org.apache.wicket.session.ISessionStore;
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
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;
import org.ops4j.pax.wicket.api.RequestCycleProcessorFactory;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public final class PaxAuthenticatedWicketApplication extends AuthenticatedWebApplication
{

    private static final Roles EMPTY_ROLES = new Roles();

    private static final AuthenticatedToken TOKEN_NOT_AUTHENTICATED = new AuthenticatedToken();

    private final BundleContext m_bundleContext;
    private final String m_applicationName;
    private final PageMounter m_pageMounter;
    private PageMounterTracker m_mounterTracker;

    protected Class<? extends Page> m_homepageClass;
    private final PaxWicketPageFactory m_pageFactory;
    // Can be null, which means that we want to use the default provided by Wicket
    private final RequestCycleProcessorFactory m_requestCycleProcessorFactory;
    // Can be null, which means that we want to use the default provided by Wicket
    private final ISessionStore m_sessionStore;
    private final DelegatingClassResolver m_delegatingClassResolver;
    private final PaxWicketAuthenticator m_authenticator;
    private final Class<? extends WebPage> m_signInPage;
    private final HashMap<AuthenticatedToken, Roles> m_roles;
    private final List<ServiceRegistration> m_serviceRegistrations;

    public PaxAuthenticatedWicketApplication(
        BundleContext bundleContext,
        String applicationName,
        PageMounter pageMounter,
        Class<? extends Page> homePageClass,
        PaxWicketPageFactory pageFactory,
        RequestCycleProcessorFactory requestCycleProcessorFactory,
        ISessionStore sessionStore,
        DelegatingClassResolver delegatingClassResolver,
        PaxWicketAuthenticator authenticator,
        Class<? extends WebPage> signInPage )
        throws IllegalArgumentException
    {
        validateNotNull( bundleContext, "bundleContext" );
        validateNotEmpty( applicationName, "applicationName" );
        validateNotNull( homePageClass, "homePageClass" );
        validateNotNull( pageFactory, "pageFactory" );
        validateNotNull( delegatingClassResolver, "delegatingClassResolver" );
        validateNotNull( authenticator, "authenticator" );
        validateNotNull( signInPage, "signInPage" );

        m_bundleContext = bundleContext;
        m_applicationName = applicationName;
        m_pageMounter = pageMounter;
        m_pageFactory = pageFactory;
        m_requestCycleProcessorFactory = requestCycleProcessorFactory;
        m_sessionStore = sessionStore;
        m_homepageClass = homePageClass;
        m_delegatingClassResolver = delegatingClassResolver;
        m_authenticator = authenticator;
        m_signInPage = signInPage;
        m_roles = new HashMap<AuthenticatedToken, Roles>();
        m_serviceRegistrations = new ArrayList<ServiceRegistration>();
    }

    /**
     * Application subclasses must specify a home page class by implementing this abstract method.
     *
     * @return Home page class for this application
     */
    @Override
    public Class<? extends Page> getHomePage()
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

        if( null != m_pageMounter )
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

    /**
     * @return AuthenticatedWebSession subclass to use in this authenticated web application.
     */
    @Override
    protected final Class<? extends AuthenticatedWebSession> getWebSessionClass()
    {
        return PaxWicketSession.class;
    }

    @Override
    public final Session newSession( Request request, Response response )
    {
        return new PaxWicketSession( request );
    }

    /**
     * @return Subclass of sign-in page
     */
    @Override
    protected Class<? extends WebPage> getSignInPageClass()
    {
        return m_signInPage;
    }

    final AuthenticatedToken authenticate( String username, String password )
    {
        if( m_authenticator == null )
        {
            return TOKEN_NOT_AUTHENTICATED;
        }

        Roles roles = m_authenticator.authenticate( username, password );
        if( roles != null )
        {
            AuthenticatedToken authenticatedToken = new AuthenticatedToken();
            this.m_roles.put( authenticatedToken, roles );
            return authenticatedToken;
        }

        return null;
    }

    final Roles getRoles( AuthenticatedToken token )
    {
        if( token == null || token == TOKEN_NOT_AUTHENTICATED )
        {
            return EMPTY_ROLES;
        }

        return m_roles.get( token );
    }

    private <T> void addWicketService( final Class<T> service, final T implementation )
    {
        Properties props = new Properties();
        props.setProperty( "applicationId", m_applicationName );
        props.setProperty( APPLICATION_NAME, m_applicationName );

        m_serviceRegistrations.add( m_bundleContext.registerService( service.getName(), implementation, props ) );
    }

    @Override
    protected IRequestCycleProcessor newRequestCycleProcessor()
    {
        if( null == m_requestCycleProcessorFactory )
        {
            return super.newRequestCycleProcessor();
        }

        return m_requestCycleProcessorFactory.newRequestCycleProcessor();
    }

    @Override
    protected ISessionStore newSessionStore()
    {
        if( m_sessionStore == null )
        {
            return super.newSessionStore();
        }

        return m_sessionStore;
    }

    @Override
    protected final WebRequest newWebRequest( HttpServletRequest aRequest )
    {
        return new PaxWicketRequest( aRequest );
    }
}
