/*
 * Copyright 2005 Niclas Hedhman.
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

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import wicket.protocol.http.IWebApplicationFactory;
import wicket.protocol.http.WicketServlet;

public final class Servlet extends WicketServlet
{

    private static final long serialVersionUID = 1L;

    private static final Logger m_logger = Logger.getLogger( Servlet.class );

    private IWebApplicationFactory m_appFactory;

    public Servlet( IWebApplicationFactory appFactory )
    {
        m_appFactory = appFactory;
    }

    protected IWebApplicationFactory getApplicationFactory()
    {
        return m_appFactory;
    }

    public void service( HttpServletRequest req, HttpServletResponse resp )
        throws ServletException, IOException
    {
        if( m_logger.isDebugEnabled() )
        {
            m_logger.debug( "Servlet.service( " + req + ", " + resp + " )" );
        }
        super.service( req, resp );
    }
}
