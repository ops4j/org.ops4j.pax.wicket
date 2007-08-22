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
import java.util.HashMap;
import java.util.List;
import java.util.Properties;

import javax.servlet.http.HttpServletRequest;
import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

import wicket.Page;
import wicket.authentication.AuthenticatedWebApplication;
import wicket.authentication.AuthenticatedWebSession;
import wicket.authorization.strategies.role.Roles;
import wicket.markup.html.WebPage;
import wicket.protocol.http.WebRequest;
import wicket.settings.IAjaxSettings;
import wicket.settings.IApplicationSettings;
import wicket.settings.IDebugSettings;
import wicket.settings.IExceptionSettings;
import wicket.settings.IFrameworkSettings;
import wicket.settings.IMarkupSettings;
import wicket.settings.IPageSettings;
import wicket.settings.IRequestCycleSettings;
import wicket.settings.IResourceSettings;
import wicket.settings.ISecuritySettings;
import wicket.settings.ISessionSettings;

public final class PaxAuthenticatedWicketApplication extends AuthenticatedWebApplication
{

    private static final Roles EMPTY_ROLES = new Roles();

    private static final AuthenticatedToken TOKEN_NOT_AUTHENTICATED = new AuthenticatedToken();

    private final BundleContext m_bundleContext;
    private final String m_applicationName;
    private final String m_mountPoint;
    private final PageMounter m_pageMounter;
    protected Class<? extends Page> m_homepageClass;
    private PaxWicketPageFactory m_factory;
    private DelegatingClassResolver m_delegatingClassResolver;
    private boolean m_deploymentMode;
    private PaxWicketAuthenticator m_authenticator;
    private Class<? extends WebPage> m_signInPage;
    private HashMap<AuthenticatedToken, Roles> m_roles;
    private final List<ServiceRegistration> m_wicketSettings;

    public PaxAuthenticatedWicketApplication( 
            BundleContext bundleContext,
            String applicationName,
            String mountPoint, 
            PageMounter pageMounter,
            Class<? extends Page> homepageClass,
            PaxWicketPageFactory factory,
            DelegatingClassResolver delegatingClassResolver,
            PaxWicketAuthenticator authenticator, Class<? extends WebPage> signInPage,
            boolean deploymentMode )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( bundleContext, "bundleContext" );
        NullArgumentException.validateNotEmpty( applicationName, "applicationName" );
        NullArgumentException.validateNotEmpty( mountPoint, "mountPoint" );
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );
        NullArgumentException.validateNotNull( factory, "factory" );
        NullArgumentException.validateNotNull( delegatingClassResolver, "delegatingClassResolver" );
        NullArgumentException.validateNotNull( authenticator, "authenticator" );
        NullArgumentException.validateNotNull( signInPage, "signInPage" );

        m_bundleContext = bundleContext;
        m_applicationName = applicationName;
        m_mountPoint = mountPoint;
        m_pageMounter = pageMounter;
        m_factory = factory;
        m_homepageClass = homepageClass;
        m_delegatingClassResolver = delegatingClassResolver;
        m_deploymentMode = deploymentMode;
        m_authenticator = authenticator;
        m_signInPage = signInPage;
        m_roles = new HashMap<AuthenticatedToken, Roles>();

        m_wicketSettings = new ArrayList<ServiceRegistration>();
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

    /**
     * Initialize; if you need the wicket servlet for initialization, e.g. because you want to read an initParameter
     * from web.xml or you want to read a resource from the servlet's context path, you can override this method and
     * provide custom initialization. This method is called right after this application class is constructed, and the
     * wicket servlet is set. <strong>Use this method for any application setup instead of the constructor.</strong>
     */
    @Override
    protected final void init()
    {
        super.init();

        IApplicationSettings applicationSettings = getApplicationSettings();
        applicationSettings.setClassResolver( m_delegatingClassResolver );
        addWicketService( IApplicationSettings.class, applicationSettings );

        ISessionSettings sessionSettings = getSessionSettings();
        sessionSettings.setPageFactory( m_factory );
        addWicketService( ISessionSettings.class, sessionSettings );

        addWicketService( IAjaxSettings.class, getAjaxSettings() );
        addWicketService( IDebugSettings.class, getDebugSettings() );
        addWicketService( IExceptionSettings.class, getExceptionSettings() );
        addWicketService( IFrameworkSettings.class, getFrameworkSettings() );
        addWicketService( IMarkupSettings.class, getMarkupSettings() );
        addWicketService( IPageSettings.class, getPageSettings() );
        addWicketService( IRequestCycleSettings.class, getRequestCycleSettings() );
        addWicketService( IResourceSettings.class, getResourceSettings() );
        addWicketService( ISecuritySettings.class, getSecuritySettings() );

        if( m_deploymentMode )
        {
            configure( DEPLOYMENT );
        }
        else
        {
            configure( DEVELOPMENT );
        }

        if( null != m_pageMounter )
        {
            for( MountPointInfo bookmark : m_pageMounter.getMountPoints() )
            {
                mount( bookmark.getPath(), bookmark.getCodingStrategy() );
            }
        }
    }

    /**
     * @return AuthenticatedWebSession subclass to use in this authenticated web application.
     */
    @Override
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass()
    {
        return PaxWicketSession.class;
    }

    /**
     * @return Subclass of sign-in page
     */
    @Override
    protected Class<? extends WebPage> getSignInPageClass()
    {
        return m_signInPage;
    }

    /**
     * Create a new WebRequest. Subclasses of WebRequest could e.g. decode and obfuscated URL which has been encoded by
     * an appropriate WebResponse.
     *
     * @param servletRequest The servlet request.
     *
     * @return a WebRequest object
     */
    @Override
    protected WebRequest newWebRequest( final HttpServletRequest servletRequest )
    {
        return new PaxWicketRequest( m_mountPoint, servletRequest );
    }

    final AuthenticatedToken authententicate( String username, String password )
    {
        if( m_authenticator == null )
        {
            return TOKEN_NOT_AUTHENTICATED;
        }

        Roles roles = m_authenticator.authenticate( username, password );
        if( roles != null )
        {
            AuthenticatedToken authenticatedToken = new AuthenticatedToken();
            m_roles.put( authenticatedToken, roles );
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

    private <T>void addWicketService( final Class<T> service, final T implementation )
    {
        Properties props = new Properties();
        props.setProperty( "applicationId", m_applicationName );

        m_wicketSettings.add(
                m_bundleContext.registerService( 
                        service.getName(), 
                        implementation, 
                        props ) );
    }
}
