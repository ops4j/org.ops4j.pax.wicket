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

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WicketFilter;
import org.apache.wicket.protocol.http.WicketServlet;
import org.ops4j.lang.NullArgumentException;
import java.io.File;
import javax.servlet.ServletContext;

final class Servlet extends WicketServlet
{

    private static final long serialVersionUID = 1L;

    private IWebApplicationFactory m_appFactory;
    private static final String WICKET_REQUIRED_ATTRIBUTE = "javax.servlet.context.tempdir";
    private final File m_tmpDir;

    Servlet( IWebApplicationFactory appFactory, File tmpDir )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( appFactory, "appFactory" );
        m_tmpDir = tmpDir;
        m_tmpDir.mkdirs();
        m_appFactory = appFactory;
    }

//    @Override
//    protected IWebApplicationFactory getApplicationFactory()
//    {
//        return m_appFactory;

    //    }
    @Override
    protected WicketFilter newWicketFilter()
    {
        ServletContext servletContext = getServletContext();
        if( servletContext.getAttribute( WICKET_REQUIRED_ATTRIBUTE ) == null )
        {
            servletContext.setAttribute( WICKET_REQUIRED_ATTRIBUTE, m_tmpDir );
        }
        return new PaxWicketFilter( m_appFactory );
    }

    @Override
    public String toString()
    {
        return "Pax Wicket Servlet";
    }
}
