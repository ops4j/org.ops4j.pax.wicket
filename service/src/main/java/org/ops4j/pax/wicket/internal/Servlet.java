/*
 * Copyright 2005 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 * Copyright 2007 David Leangen
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

final class Servlet extends WicketServlet
{

    private static final long serialVersionUID = 1L;

    private final IWebApplicationFactory m_appFactory;

    Servlet( IWebApplicationFactory appFactory )
        throws IllegalArgumentException
    {
        NullArgumentException.validateNotNull( appFactory, "appFactory" );
        m_appFactory = appFactory;
    }

    @Override
    protected WicketFilter newWicketFilter()
    {
        return new PaxWicketFilter( m_appFactory );
    }
}
