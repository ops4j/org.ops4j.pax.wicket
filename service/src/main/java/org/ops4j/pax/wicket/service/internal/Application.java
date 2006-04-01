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

import wicket.ISessionFactory;
import wicket.protocol.http.WebApplication;

public class Application extends WebApplication
{
    private Class m_homePage;

    public Application()
    {
    }

    public Class getHomePage()
    {
        return m_homePage;
    }

    void setHomePage( Class homePage )
    {
        m_homePage = homePage;
    }

    /**
     * @see wicket.Application#getSessionFactory()
     */
    protected ISessionFactory getSessionFactory()
    {
        return new ISessionFactory()
        {
            public wicket.Session newSession()
            {
                return new PaxWicketSession(Application.this);
            }
        };
    }
}
