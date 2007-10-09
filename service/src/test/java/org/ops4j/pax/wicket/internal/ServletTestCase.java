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

import org.jmock.integration.junit3.MockObjectTestCase;

public final class ServletTestCase extends MockObjectTestCase
{

    public void testConstructor()
    {
        String msg = "Construct with [null] argument must throw [IllegalArgumentException].";
        try
        {
            new Servlet( null, null );
            fail( msg );
        } catch( IllegalArgumentException e )
        {
            // Expected
        } catch( Throwable e )
        {
            fail( msg );
        }
    }

//    public final void testGetApplicationFactory()
//    {
//        IWebApplicationFactory expected = mock( IWebApplicationFactory.class );
//        Servlet servlet = new Servlet( expected );
//
//        IWebApplicationFactory appFac = servlet.getApplicationFactory();
//        assertEquals( expected, appFac );
//    }

}
