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

import java.util.HashMap;

import javax.servlet.http.HttpServletRequest;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;

import wicket.Page;
import wicket.authentication.AuthenticatedWebApplication;
import wicket.authentication.AuthenticatedWebSession;
import wicket.authorization.strategies.role.Roles;
import wicket.markup.html.WebPage;
import wicket.protocol.http.WebRequest;
import wicket.settings.IApplicationSettings;
import wicket.settings.ISessionSettings;

public final class PaxAuthenticatedWicketApplication extends AuthenticatedWebApplication
{

    private static final Roles EMPTY_ROLES = new Roles();

    private static final AuthenticatedToken TOKEN_NOT_AUTHENTICATED = new AuthenticatedToken();

    private final String m_mountPoint;
    protected Class m_homepageClass;
    private PaxWicketPageFactory m_factory;
    private DelegatingClassResolver m_delegatingClassResolver;
    private boolean m_deploymentMode;
    private PaxWicketAuthenticator m_authenticator;
    private Class< ? extends WebPage> m_signInPage;
    private HashMap<AuthenticatedToken, Roles> m_roles;


    public PaxAuthenticatedWicketApplication(
            String mountPoint, Class<? extends Page> homepageClass, PaxWicketPageFactory factory,
            DelegatingClassResolver delegatingClassResolver,
            PaxWicketAuthenticator authenticator, Class<? extends WebPage> signInPage,
            boolean deploymentMode )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotEmpty( mountPoint, "mountPoint" );
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );
        NullArgumentException.validateNotNull( factory, "factory" );
        NullArgumentException.validateNotNull( delegatingClassResolver, "delegatingClassResolver" );
        NullArgumentException.validateNotNull( authenticator, "authenticator" );
        NullArgumentException.validateNotNull( signInPage, "signInPage" );

        m_mountPoint = mountPoint;
        m_factory = factory;
        m_homepageClass = homepageClass;
        m_delegatingClassResolver = delegatingClassResolver;
        m_deploymentMode = deploymentMode;
        m_authenticator = authenticator;
        m_signInPage = signInPage;
        m_roles = new HashMap<AuthenticatedToken, Roles>();
    }

    /**
     * Application subclasses must specify a home page class by implementing
     * this abstract method.
     *
     * @return Home page class for this application
     */
    @Override
    public Class getHomePage()
    {
        return m_homepageClass;
    }

    /**
     * Initialize; if you need the wicket servlet for initialization, e.g.
     * because you want to read an initParameter from web.xml or you want to
     * read a resource from the servlet's context path, you can override this
     * method and provide custom initialization. This method is called right
     * after this application class is constructed, and the wicket servlet is
     * set. <strong>Use this method for any application setup instead of the
     * constructor.</strong>
     */
    public void init()
    {
        super.init();

        IApplicationSettings applicationSettings = getApplicationSettings();
        applicationSettings.setClassResolver( m_delegatingClassResolver );

        ISessionSettings sessionSettings = getSessionSettings();
        sessionSettings.setPageFactory( m_factory );
        if( m_deploymentMode )
        {
            configure( DEPLOYMENT );
        }
        else
        {
            configure( DEVELOPMENT );
        }
    }

    /**
     * @return AuthenticatedWebSession subclass to use in this authenticated web
     *         application.
     */
    protected Class<? extends AuthenticatedWebSession> getWebSessionClass()
    {
        return PaxWicketSession.class;
    }

    /**
     * @return Subclass of sign-in page
     */
    protected Class<? extends WebPage> getSignInPageClass()
    {
        return m_signInPage;
    }

    /**
     * Create a new WebRequest. Subclasses of WebRequest could e.g. decode and
     * obfuscated URL which has been encoded by an appropriate WebResponse.
     *
     * @param servletRequest
     *
     * @return a WebRequest object
     */
    protected WebRequest newWebRequest( final HttpServletRequest servletRequest )
    {
        return new PaxWicketRequest( m_mountPoint, servletRequest );
    }

    public AuthenticatedToken authententicate( String username, String password )
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

    public Roles getRoles( AuthenticatedToken token )
    {
        if( token == null || token == TOKEN_NOT_AUTHENTICATED )
        {
            return EMPTY_ROLES;
        }

        return m_roles.get( token );
    }
}
