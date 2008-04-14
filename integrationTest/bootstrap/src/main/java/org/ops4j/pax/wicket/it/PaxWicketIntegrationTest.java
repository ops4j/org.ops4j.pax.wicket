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
package org.ops4j.pax.wicket.it;

import java.util.ArrayList;
import org.osgi.framework.Bundle;
import org.springframework.osgi.test.AbstractConfigurableBundleCreatorTests;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
public abstract class PaxWicketIntegrationTest extends AbstractConfigurableBundleCreatorTests
{

    protected static final String SYMBOLIC_NAME_PAX_WICKET_SERVICE = "org.ops4j.pax.wicket.pax-wicket-service";

    /**
     * Initialized test framework bundles.
     *
     * @return The required test framework bundles.
     *
     * @since 0.5.4
     */
    protected final String[] getTestFrameworkBundlesNames()
    {
        ArrayList<String> validBundleList = new ArrayList<String>();
        String[] bundles = super.getTestFrameworkBundlesNames();
        for( String bundle : bundles )
        {
            // Skip logging api, we want to use pax-logging
            if( bundle.indexOf( "slf4j" ) != -1 ||
                bundle.indexOf( "log4j" ) != -1 )
            {
                continue;
            }

            validBundleList.add( bundle );
        }

        // Add pax-wicket requirement
        validBundleList.add( "org.ops4j.pax.logging,pax-logging-api,1.0.0" );
        validBundleList.add( "org.ops4j.pax.logging,pax-logging-service,1.0.0" );
        validBundleList.add( "org.apache.felix,org.apache.felix.eventadmin,1.0.0" );
        validBundleList.add( "org.apache.felix,org.apache.felix.configadmin,1.0.1" );
        validBundleList.add( "org.ops4j.pax.wicket,pax-wicket-service,0.5.4-SNAPSHOT" );
        validBundleList.add( "org.knopflerfish.bundle.useradmin,useradmin_api,1.1.0" );
        validBundleList.add( "org.ops4j.pax.web,pax-web-bundle,0.4.1" );
        validBundleList.add( "org.ops4j.pax.web,pax-web-service,0.4.1" );
        validBundleList.add( "org.apache.felix,org.osgi.compendium,1.0.0" );

        return validBundleList.toArray( new String[validBundleList.size()] );
    }

    /**
     * Returns the pax wicket service bundle.
     *
     * @return The pax wicket service bundle.
     *
     * @since 0.5.4
     */
    protected final Bundle getPaxWicketServiceBundle()
    {
        Bundle paxWicketBundle = null;
        Bundle[] bundles = bundleContext.getBundles();
        for( Bundle bundle : bundles )
        {
            String bundleSymbolicName = bundle.getSymbolicName();
            if( SYMBOLIC_NAME_PAX_WICKET_SERVICE.equals( bundleSymbolicName ) )
            {
                paxWicketBundle = bundle;
                break;
            }
        }
        assertNotNull( paxWicketBundle );
        return paxWicketBundle;
    }

}
