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
import wicket.IPageFactory;
import wicket.protocol.http.WebApplication;
import wicket.settings.ISessionSettings;

public final class PaxWicketApplication extends WebApplication
{

    protected IPageFactory m_factory;
    protected Class m_homepageClass;
    private String m_mountPoint;

    public PaxWicketApplication( IPageFactory factory, Class homepageClass, String mountPoint )
    {
        NullArgumentException.validateNotNull( factory, "factory" );
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );
        NullArgumentException.validateNotNull( mountPoint, "mountPoint" );

        m_mountPoint = mountPoint;
        m_homepageClass = homepageClass;
        m_factory = factory;
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
        getApplicationSettings().setContextPath( m_mountPoint );
        ISessionSettings sessionSettings = getSessionSettings();
        sessionSettings.setPageFactory( m_factory );
        configure( DEPLOYMENT );
    }
}
