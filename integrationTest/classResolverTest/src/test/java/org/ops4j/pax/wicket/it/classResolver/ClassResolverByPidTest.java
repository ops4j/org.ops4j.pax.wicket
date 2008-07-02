/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.it.classResolver;

import java.util.Properties;
import org.apache.wicket.application.IClassResolver;
import static org.ops4j.pax.wicket.api.ContentSource.APPLICATION_NAME;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import static org.osgi.framework.Constants.SERVICE_PID;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.ManagedService;

/**
 * @author edward.yakop@gmail.com
 */
public final class ClassResolverByPidTest
    extends PaxWicketIntegrationTest
{

    @Override
    protected final String[] getTestBundlesNames()
    {
        return new String[]
            {
                "org.ops4j.pax.wicket.integrationTest.bundles,simpleLibraries,0.5.4-SNAPSHOT"
            };
    }

    public final void testPrivateLibraries()
        throws Throwable
    {
        ServiceReference[] references = bundleContext.getServiceReferences(
            IClassResolver.class.getName(), "(" + SERVICE_PID + "=libraryPid)"
        );
        assertNotNull( references );
        assertEquals( references.length, 1 );

        ServiceReference classResolverReference = references[ 0 ];
        assertFalse( isApplicationNameKeyExists( classResolverReference ) );

        ManagedService managedService = (ManagedService) bundleContext.getService( classResolverReference );
        Properties dictionary = new Properties();

        // Lets update configuration to expose our sample library to abc, def application
        // This is also can be done via configuration admin
        dictionary.put( APPLICATION_NAME, new String[]{ "abc", "def" } );
        managedService.updated( dictionary );

        bundleContext.ungetService( classResolverReference );

        assertTrue( isApplicationNameKeyExists( classResolverReference ) );

        validateThatClassResolverIsExposedToAbcAndDef();
    }

    private void validateThatClassResolverIsExposedToAbcAndDef()
        throws Throwable
    {
        ServiceReference[] references = bundleContext.getServiceReferences(
            IClassResolver.class.getName(), "(" + APPLICATION_NAME + "=abc)"
        );
        assertNotNull( references );
        assertEquals( references.length, 1 );
        ServiceReference reference = references[ 0 ];
        String[] applicationNames = (String[]) reference.getProperty( APPLICATION_NAME );
        assertEquals( 2, applicationNames.length );
        assertEquals( applicationNames[ 0 ], "abc" );
        assertEquals( applicationNames[ 1 ], "def" );

        // Verify that this is the simple libraries class resolver
        IClassResolver classResolver = (IClassResolver) bundleContext.getService( reference );
        String className = "org.ops4j.pax.wicket.it.bundles.simpleLibraries.internal.PrivateClass";
        Class clazz = classResolver.resolveClass( className );
        assertNotNull( clazz );
        assertEquals( clazz.getName(), className );
    }

    private boolean isApplicationNameKeyExists( ServiceReference reference )
    {
        String[] keys = reference.getPropertyKeys();
        boolean isApplicatioNameKeyExists = false;
        for( String key : keys )
        {
            if( APPLICATION_NAME.equals( key ) )
            {
                isApplicatioNameKeyExists = true;
                break;
            }
        }
        return isApplicatioNameKeyExists;
    }
}
