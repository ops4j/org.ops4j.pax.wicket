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
import org.apache.wicket.Page;
import org.apache.wicket.Request;
import org.apache.wicket.Response;
import org.apache.wicket.Session;
import org.apache.wicket.authentication.AuthenticatedWebApplication;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.authorization.strategies.role.Roles;
import org.apache.wicket.markup.html.WebPage;
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
import org.ops4j.pax.wicket.api.MountPointInfo;
import org.ops4j.pax.wicket.api.PageMounter;
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

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
        PaxWicketAuthenticator authenticator, Class<? extends WebPage> signInPage )
        throws IllegalArgumentException
    {
        validateNotNull( bundleContext, "bundleContext" );
        validateNotEmpty( applicationName, "applicationName" );
        validateNotEmpty( mountPoint, "mountPoint" );
        validateNotNull( homepageClass, "homepageClass" );
        validateNotNull( factory, "factory" );
        validateNotNull( delegatingClassResolver, "delegatingClassResolver" );
        validateNotNull( authenticator, "authenticator" );
        validateNotNull( signInPage, "signInPage" );

        m_bundleContext = bundleContext;
        m_applicationName = applicationName;
        m_mountPoint = mountPoint;
        m_pageMounter = pageMounter;
        m_factory = factory;
        m_homepageClass = homepageClass;
        m_delegatingClassResolver = delegatingClassResolver;
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

    private <T> void addWicketService( final Class<T> service, final T implementation )
    {
        Properties props = new Properties();
        props.setProperty( "applicationId", m_applicationName );

        m_wicketSettings.add(
            m_bundleContext.registerService(
                service.getName(),
                implementation,
                props
            )
        );
    }

    @Override
    protected final WebRequest newWebRequest( HttpServletRequest aRequest )
    {
        return new PaxWicketRequest( aRequest );
    }
}
