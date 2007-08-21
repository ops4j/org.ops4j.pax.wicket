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

import javax.servlet.http.HttpServletRequest;
import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;

public final class PaxWicketRequestTestCase extends MockObjectTestCase
{

    public final void testConstructor()
    {
        HttpServletRequest request = mock( HttpServletRequest.class );
        Object[][] arguments =
            {
                { null, null },
                { null, request },
                { "mountPoint", null }
            };

        String msg = "Construct with [null] argument must throw [IllegalArgumentException]";
        for( Object[] argument : arguments )
        {
            String mntPoint = (String) argument[ 0 ];
            HttpServletRequest httpRequest = (HttpServletRequest) argument[ 1 ];

            try
            {
                new PaxWicketRequest( mntPoint, httpRequest );
                fail( msg );
            } catch( IllegalArgumentException e )
            {
                // Expected
            } catch( Throwable e )
            {
                e.printStackTrace();
                fail( msg );
            }
        }

        try
        {
            new PaxWicketRequest( "mntPoint", request );
        } catch( Throwable e )
        {
            e.printStackTrace();
            fail( "Construct with valid arguments must not throw any exception." );
        }
    }

//    public final void testGetContextPath()
//    {
//        String mntPoint = "mntPoint";
//        HttpServletRequest request = mock( HttpServletRequest.class );
//        PaxWicketRequest paxWicketRequest = new PaxWicketRequest( mntPoint, request );
//
//        Expectations exp1 = new Expectations();
//        exp1.one( request ).getServletPath();
//        String exp1ContextPath = "/" + mntPoint + "/anotherPath";
//        exp1.will( exp1.returnValue( exp1ContextPath ) );
//
//        checking( exp1 );
//        String retCtxPath1 = paxWicketRequest.getContextPath();
//        assertEquals( exp1ContextPath, retCtxPath1 );
//
//        Expectations exp2 = new Expectations();
//        exp2.one( request ).getServletPath();
//        exp2.will( exp2.returnValue( "/mntPoint" ) );
//
//        checking( exp2 );
//        String retCtxPath2 = paxWicketRequest.getContextPath();
//        assertEquals( "/mntPoint/", retCtxPath2 );
//
//        Expectations exp3 = new Expectations();
//        exp3.one( request ).getServletPath();
//        exp3.will( exp3.returnValue( "/mntPoint?abc" ) );
//
//        checking( exp3 );
//        String retCtxPath3 = paxWicketRequest.getContextPath();
//        assertEquals( "/mntPoint/?abc", retCtxPath3 );
//    }

    public final void testGetServletPath()
    {
        String mntPoint = "/mntPoint";
        HttpServletRequest request = mock( HttpServletRequest.class );
        PaxWicketRequest paxWicketRequest = new PaxWicketRequest( mntPoint, request );

        Expectations exp1 = new Expectations();
        exp1.one( request ).getContextPath();
        exp1.will( exp1.returnValue( mntPoint ) );

        checking( exp1 );
        String retServletPath1 = paxWicketRequest.getServletPath();
        String expMountPoint = mntPoint + "/";
        assertEquals( expMountPoint, retServletPath1 );

        Expectations exp2 = new Expectations();
        exp2.one( request ).getContextPath();
        exp2.will( exp2.returnValue( expMountPoint ) );

        checking( exp2 );
        String retServletPath2 = paxWicketRequest.getServletPath();
        assertEquals( expMountPoint, retServletPath2 );
    }
}
