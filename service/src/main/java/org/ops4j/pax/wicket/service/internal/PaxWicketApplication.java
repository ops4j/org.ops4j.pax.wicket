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
package org.ops4j.pax.wicket.service.internal;

import org.ops4j.lang.NullArgumentException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WebRequest;
import wicket.protocol.http.servlet.ServletWebRequest;
import wicket.settings.ISessionSettings;
import wicket.settings.IApplicationSettings;
import javax.servlet.http.HttpServletRequest;

public final class PaxWicketApplication extends WebApplication
{

    protected Class m_homepageClass;
    private PaxWicketPageFactory m_factory;
    private boolean m_deploymentMode;

    public PaxWicketApplication( Class homepageClass, PaxWicketPageFactory factory, boolean deploymentMode )
    {
        m_factory = factory;
        m_deploymentMode = deploymentMode;
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );

        m_homepageClass = homepageClass;
    }

    /**
     * Application subclasses must specify a home page class by implementing
     * this abstract method.
     *
     * @return Home page class for this application
     */
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
        applicationSettings.setClassResolver( m_factory );

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
    protected WebRequest newWebRequest( final HttpServletRequest servletRequest )
    {
        return new PaxWicketRequest( servletRequest );
    }

    private static class PaxWicketRequest extends ServletWebRequest
    {
        private static final Log m_logger = LogFactory.getLog( PaxWicketRequest.class.getName() );

        /**
         * Protected constructor.
         *
         * @param httpServletRequest The servlet request information
         */
        private PaxWicketRequest( HttpServletRequest httpServletRequest )
        {
            super( httpServletRequest );
        }

        /**
         * Gets the servlet path.
         *
         * @return Servlet path
         */
        public String getServletPath()
        {
            String contextPath = getHttpServletRequest().getContextPath();
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "getServletPath() : " + contextPath );
            }
            return contextPath;
        }

        /**
         * Gets the servlet context path.
         *
         * @return Servlet context path
         */
        public String getContextPath()
        {
            String servletPath = getHttpServletRequest().getServletPath();
            if( m_logger.isDebugEnabled() )
            {
                m_logger.debug( "getContextPath() : " + servletPath );
            }
            return servletPath;
        }
    }
}
