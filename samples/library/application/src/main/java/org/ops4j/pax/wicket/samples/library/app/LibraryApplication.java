/*
 * $Id: LibraryApplication.java 4545 2006-02-17 20:56:58Z eelco12 $
 * $Revision: 4545 $ $Date: 2006-02-18 04:56:58 +0800 (Sat, 18 Feb 2006) $
 *
 * ==================================================================== Licensed
 * under the Apache License, Version 2.0 (the "License"); you may not use this
 * file except in compliance with the License. You may obtain a copy of the
 * License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ops4j.pax.wicket.samples.library.app;

import wicket.authentication.AuthenticatedWebApplication;
import wicket.authentication.AuthenticatedWebSession;
import wicket.authentication.pages.SignInPage;
import wicket.markup.html.WebPage;

/**
 * WicketServlet class for example.
 *
 * @author Jonathan Locke
 */
public final class LibraryApplication extends AuthenticatedWebApplication
{
    private Class< ? extends WebPage> m_signInPage;
    private Class< ? extends WebPage> m_homePage;

    public LibraryApplication( Class<? extends WebPage> homePage )
    {
        m_homePage = homePage;
        m_signInPage = SignInPage.class; 
    }

    @Override
    protected Class< ? extends AuthenticatedWebSession> getWebSessionClass()
    {
        return LibrarySession.class;
    }

    @Override
    protected Class< ? extends WebPage> getSignInPageClass()
    {
        return m_signInPage;
    }

    /**
     * @see wicket.Application#getHomePage()
     */
    @Override
    public Class getHomePage()
    {
        return m_homePage;
    }
}
