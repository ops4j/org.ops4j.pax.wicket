/*
 * Copyright 2005 Niclas Hedhman.
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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ops4j.pax.servicemanager.ServiceManager;
import wicket.IPageFactory;
import wicket.protocol.http.WicketServlet;
import wicket.settings.ISessionSettings;

public class Servlet extends WicketServlet
{

    private ServiceManager m_ServiceManager;

    public Servlet( ServiceManager serviceManager )
    {
        m_ServiceManager = serviceManager;
    }

    public void init()
    {
        Log logger = LogFactory.getLog( Servlet.class );
        logger.debug( "Servlet.init()" );
        Application app = new Application();
        webApplication = app;
        webApplication.setWicketServlet( this );
        ISessionSettings settings = app.getSessionSettings();
        IPageFactory defFactory = settings.getPageFactory();
        IPageFactory pageFactory = new PageFactory( defFactory, m_ServiceManager );
        settings.setPageFactory( pageFactory );
    }

    public void service( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        Log logger = LogFactory.getLog( Servlet.class );
        logger.debug( "Servlet.service( " + req + ", " + resp + " )" );
        super.service( req, resp );
    }

}
