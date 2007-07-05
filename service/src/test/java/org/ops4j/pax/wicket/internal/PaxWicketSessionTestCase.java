/*
 * Copyright 2007 Edward Yakop.
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

import java.lang.reflect.Method;
import javax.servlet.ServletContext;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import org.ops4j.pax.wicket.api.PaxWicketAuthenticator;
import org.osgi.framework.BundleContext;
import wicket.Application;
import static wicket.Application.CONFIGURATION;
import static wicket.Application.set;
import wicket.Page;
import wicket.authorization.strategies.role.Roles;
import wicket.markup.html.WebPage;
import wicket.protocol.http.WebApplication;
import wicket.protocol.http.WicketServlet;

public class PaxWicketSessionTestCase extends MockObjectTestCase
{

    public final void testAuthentication()
        throws Throwable
    {
        // Initialize [PaxAuthenticatedWicketApplication]
        BundleContext context = mock( BundleContext.class );
        String appName = "appName";
        DelegatingClassResolver classResolver = new DelegatingClassResolver( context, appName );
        PaxWicketPageFactory pageFac = new PaxWicketPageFactory( context, appName );
        PaxWicketAuthenticator authenticator = mock( PaxWicketAuthenticator.class );
        PaxAuthenticatedWicketApplication application = new PaxAuthenticatedWicketApplication(
            context, appName, "mountPoint", Page.class, pageFac, classResolver, authenticator, WebPage.class, true
        );
        set( application );

        MockWicketServlet wicketServlet = new MockWicketServlet();
        application.setWicketServlet( wicketServlet );

        // Invoke internal init
        Class<WebApplication> aClass = WebApplication.class;
        Method method = aClass.getDeclaredMethod( "internalInit", (Class[]) null );
        method.setAccessible( true );
        method.invoke( application, (Object[]) null );

        PaxWicketSession session = new PaxWicketSession( application );

        // Test unsuccesfull authentication
        Expectations exp1 = new Expectations();
        exp1.one( authenticator ).authenticate( null, null );
        exp1.will( exp1.returnValue( null ) );

        checking( exp1 );
        assertEquals( false, session.authenticate( null, null ) );

        // Test successfull authentication
        Expectations exp2 = new Expectations();
        String username = "efy";
        String password = "password";
        exp2.one( authenticator ).authenticate( username, password );
        exp2.will( exp2.returnValue( new Roles() ) );

        checking( exp2 );
        assertEquals( true, session.authenticate( username, password ) );
        assertEquals( username, session.getLoggedInUser() );
        assertEquals( new Roles(), session.getRoles() );

        try
        {
            session.invalidateNow();
        } catch( Throwable e )
        {
            // Expected because we haven't set up the application properly
            // This also set the invalidate has never been called :( I.e. 80% test method coverage
        }

        assertEquals( null, session.getLoggedInUser() );
    }

    private class MockWicketServlet extends WicketServlet
    {

        private static final long serialVersionUID = 1L;

        private MockWicketServlet()
        {
        }

        @Override
        public String getServletName()
        {
            return "MockServletName";
        }

        @Override
        public ServletContext getServletContext()
        {
            return mock( ServletContext.class );
        }

        @Override
        public String getInitParameter( String param )
        {
            if( CONFIGURATION.equals( param ) )
            {
                return Application.DEVELOPMENT;
            }

            return "Param:" + param;
        }
    }

}
