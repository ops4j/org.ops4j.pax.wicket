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

import org.jmock.Expectations;
import org.jmock.integration.junit3.MockObjectTestCase;
import static org.ops4j.pax.wicket.internal.TrackingUtil.createAllPageFactoryFilter;
import static org.ops4j.pax.wicket.internal.TrackingUtil.createContentFilter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;

public final class TrackingUtilTestCase extends MockObjectTestCase
{

    public final void testCreateContentFilter()
        throws InvalidSyntaxException
    {
        BundleContext context = mock( BundleContext.class );
        Object[][] arguments =
            {
                { null, null },
                { null, "appName" },
                { context, null }
            };

        String msg = "Invoking with [null] argument must fail.";
        for( Object[] argument : arguments )
        {
            BundleContext ctx = (BundleContext) argument[ 0 ];
            String appName = (String) argument[ 1 ];

            try
            {
                createContentFilter( ctx, appName );
                fail( msg );
            } catch( IllegalArgumentException e )
            {
                // expected
            } catch( Throwable e )
            {
                fail( msg );
            }
        }

        Expectations exp1 = new Expectations();
        exp1.one( context ).createFilter(
            "(&(pax.wicket.applicationname=appName)(objectClass=org.ops4j.pax.wicket.api.ContentSource))"
        );
        Filter expFilter = mock( Filter.class );
        exp1.will( exp1.returnValue( expFilter ) );

        checking( exp1 );
        Filter filter = createContentFilter( context, "appName" );
        assertEquals( expFilter, filter );

        Expectations exp2 = new Expectations();
        exp2.one( context ).createFilter( exp2.with( exp2.any( String.class ) ) );
        exp2.will( exp2.throwException( new InvalidSyntaxException( "msg", "filter" ) ) );

        checking( exp2 );

        try
        {
            createContentFilter( context, "appName" );
            fail( "Must throw [IllegalArgumentException]." );
        } catch( IllegalArgumentException e )
        {
            // Expected
        } catch( Throwable e )
        {
            e.printStackTrace();
            fail( "Must throw [IllegalArgumentException]." );
        }
    }

    public final void testCreateAllPageFactoryFilter()
        throws InvalidSyntaxException
    {
        BundleContext context = mock( BundleContext.class );
        Object[][] arguments =
            {
                { null, null },
                { null, "appName" },
                { context, null }
            };

        String msg = "Invoking with [null] argument must fail.";
        for( Object[] argument : arguments )
        {
            BundleContext ctx = (BundleContext) argument[ 0 ];
            String appName = (String) argument[ 1 ];

            try
            {
                createAllPageFactoryFilter( ctx, appName );
                fail( msg );
            } catch( IllegalArgumentException e )
            {
                // expected
            } catch( Throwable e )
            {
                fail( msg );
            }
        }

        Expectations exp1 = new Expectations();
        exp1.one( context ).createFilter(
            "(&(pax.wicket.applicationname=appName)(objectClass=org.ops4j.pax.wicket.api.PageFactory))"
        );
        Filter expFilter = mock( Filter.class );
        exp1.will( exp1.returnValue( expFilter ) );

        checking( exp1 );
        Filter filter = createAllPageFactoryFilter( context, "appName" );
        assertEquals( expFilter, filter );

        Expectations exp2 = new Expectations();
        exp2.one( context ).createFilter( exp2.with( exp2.any( String.class ) ) );
        exp2.will( exp2.throwException( new InvalidSyntaxException( "msg", "filter" ) ) );

        checking( exp2 );

        try
        {
            createAllPageFactoryFilter( context, "appName" );
            fail( "Must throw [IllegalArgumentException]." );
        } catch( IllegalArgumentException e )
        {
            // Expected
        } catch( Throwable e )
        {
            e.printStackTrace();
            fail( "Must throw [IllegalArgumentException]." );
        }
    }

}
