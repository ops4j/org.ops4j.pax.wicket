/*
 * $Id: LibraryHomePage.java 4226 2006-02-08 23:21:46Z joco01 $
 * $Revision: 4226 $
 * $Date: 2006-02-09 07:21:46 +0800 (Thu, 09 Feb 2006) $
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.library.app;

import wicket.PageParameters;
import wicket.markup.html.WebPage;
import wicket.markup.html.border.Border;

/**
 * Ensures that user is authenticated in session.  If no user is signed in, a sign
 * in is forced by redirecting the browser to the SignIn page.
 * <p>
 * This base class also creates a border for each page subclass, automatically adding
 * children of the page to the border.  This accomplishes two important things:
 * (1) subclasses do not have to repeat the code to create the border navigation and
 * (2) since subclasses do not repeat this code, they are not hardwired to page
 * navigation structure details
 *
 * @author Jonathan Locke
 */
public class LibraryHomePage extends WebPage
{

    private Border m_border;

    /**
     * Constructor. Having this constructor public means that your page is
     * 'bookmarkable' and hence can be called/ created from anywhere.
     */
    public LibraryHomePage()
    {
        this( null );
    }

    /**
     * Contruct
     */
    public LibraryHomePage( PageParameters parameters )
    {
        super( parameters );
        // Create border and add it to the page
        m_border = new LibraryApplicationBorder( "border" );
        m_border.setTransparentResolver( true );
        add( m_border );
    }

    protected LibrarySession getLibrarySession()
    {
        return (LibrarySession) getSession();
    }
}


