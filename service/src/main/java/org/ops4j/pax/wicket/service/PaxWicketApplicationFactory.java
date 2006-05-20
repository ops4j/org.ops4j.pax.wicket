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
package org.ops4j.pax.wicket.service;

import org.ops4j.lang.NullArgumentException;
import org.ops4j.pax.wicket.service.internal.PaxWicketApplication;
import wicket.IPageFactory;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public class PaxWicketApplicationFactory
    implements IWebApplicationFactory
{
    public static final String MOUNTPOINT = "mountpoint";

    private Class m_homepageClass;
    private IPageFactory m_pageFactory;

    public PaxWicketApplicationFactory( IPageFactory pageFactory, Class homepageClass )
    {
        NullArgumentException.validateNotNull( pageFactory, "pageFactory" );
        NullArgumentException.validateNotNull( homepageClass, "homepageClass" );

        m_homepageClass = homepageClass;
        m_pageFactory = pageFactory;
    }

    /**
     * Create application object
     *
     * @param servlet the wicket servlet
     *
     * @return application object instance
     */
    public WebApplication createApplication( WicketServlet servlet )
    {
        return new PaxWicketApplication( m_pageFactory, m_homepageClass );
    }
}
