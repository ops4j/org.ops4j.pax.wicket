/*
 * Copyright 2006 Niclas Hedhman.
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
import static org.ops4j.lang.NullArgumentException.validateNotNull;

final class PaxWicketFilter extends WicketFilter
{

    private final IWebApplicationFactory m_applicationFactory;

    PaxWicketFilter( IWebApplicationFactory factory )
        throws IllegalArgumentException
    {
        validateNotNull( factory, "factory" );
        m_applicationFactory = factory;
    }

    @Override
    protected final IWebApplicationFactory getApplicationFactory()
    {
        return m_applicationFactory;
    }
}
