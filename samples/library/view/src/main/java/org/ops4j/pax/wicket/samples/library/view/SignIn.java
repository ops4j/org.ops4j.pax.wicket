/*
 * $Id: SignIn.java 1471 2005-03-25 16:29:01Z eelco12 $
 * $Revision: 1471 $
 * $Date: 2005-03-26 00:29:01 +0800 (Sat, 26 Mar 2005) $
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
package org.ops4j.pax.wicket.samples.library.view;

import wicket.PageParameters;
import wicket.authentication.panel.SignInPanel;
import wicket.markup.html.WebPage;
import wicket.model.IModel;
import wicket.util.string.Strings;
import org.ops4j.pax.wicket.samples.library.model.User;
import org.ops4j.pax.wicket.samples.library.controller.LibrarySession;

/**
 * Simple example of a sign in page.
 *
 * @author Jonathan Locke
 */
public final class SignIn extends WebPage
{

    public SignIn( IModel model )
    {
        super( model );
        final String packageName = getClass().getPackage().getName();
        add( new WicketExampleHeader( "mainNavigation", Strings.afterLast( packageName, '.' ), this ) );
        explain();
    }

    /**
     * Override base method to provide an explanation
     */
    protected void explain()
    {
    }

    /**
     * Constructor
     *
     * @param parameters The page parameters
     */
    public SignIn( final PageParameters parameters )
    {
        SignInPanel signInPanel = new SignInPanel( "signInPanel" )
        {
            public boolean signIn( final String username, final String password )
            {
                // Sign the user in
                final User user = ( (LibrarySession) getSession() ).authenticate( username, password );

                // If the user was signed in
                if( user != null )
                {
                    return true;
                }
                else
                {
                    error( getLocalizer().getString( "couldNotAuthenticate", this ) );
                    return false;
                }
            }
        };
        add( signInPanel );
    }
}

//
