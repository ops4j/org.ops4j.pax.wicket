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

import javax.servlet.http.HttpServletRequest;
import org.ops4j.lang.NullArgumentException;
import wicket.Page;
import wicket.Session;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.settings.IApplicationSettings;
import wicket.settings.ISessionSettings;

public final class PaxWicketApplication extends WebApplication
{
    private final String m_mountPoint;
    protected Class m_homepageClass;
    private PaxWicketPageFactory m_factory;
    private DelegatingClassResolver m_delegatingClassResolver;
    private boolean m_deploymentMode;

    public PaxWicketApplication(
            String mountPoint, Class<? extends Page> homepageClass, PaxWicketPageFactory factory,
            DelegatingClassResolver delegatingClassResolver,
            boolean deploymentMode )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( mountPoint, "mountPoint" );
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );
        NullArgumentException.validateNotNull( factory, "factory" );
        NullArgumentException.validateNotNull( delegatingClassResolver, "delegatingClassResolver" );

        m_mountPoint = mountPoint;
        m_factory = factory;
        m_homepageClass = homepageClass;
        m_delegatingClassResolver = delegatingClassResolver;
        m_deploymentMode = deploymentMode;
    }

    /**
     * Application subclasses must specify a home page class by implementing
     * this abstract method.
     *
     * @return Home page class for this application
     */
    @Override
    public final Class getHomePage()
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
    protected final void init()
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
     * Create a new WebRequest. Subclasses of WebRequest could e.g. decode and
     * obfuscated URL which has been encoded by an appropriate WebResponse.
     *
     * @param servletRequest
     *
     * @return a WebRequest object
     */
    protected final WebRequest newWebRequest( final HttpServletRequest servletRequest )
    {
        return new PaxWicketRequest( m_mountPoint, servletRequest );
    }
}
