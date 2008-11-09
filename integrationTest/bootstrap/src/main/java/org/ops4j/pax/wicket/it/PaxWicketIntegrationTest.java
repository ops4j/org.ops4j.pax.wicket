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

import org.ops4j.pax.drone.api.BundleProvision;
import org.ops4j.pax.drone.api.DroneConnector;
import static org.ops4j.pax.drone.connector.paxrunner.GenericConnector.create;
import static org.ops4j.pax.drone.connector.paxrunner.GenericConnector.createBundleProvision;
import org.ops4j.pax.drone.connector.paxrunner.PaxRunnerConnector;
import org.ops4j.pax.drone.connector.paxrunner.Platforms;
import org.ops4j.pax.drone.spi.junit.DroneTestCase;
import org.osgi.framework.Bundle;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
public abstract class PaxWicketIntegrationTest extends DroneTestCase
{

    protected static final String SYMBOLIC_NAME_PAX_WICKET_SERVICE = "org.ops4j.pax.wicket.pax-wicket-service";

    @Override
    protected DroneConnector configure()
    {
        String platform = System.getProperty( "pax.wicket.test.platform", "EQUINOX" );

        BundleProvision bundleProvision = createBundleProvision();
        bundleProvision.addBundle( "mvn:org.ops4j.pax.logging/pax-logging-api" )
            .addBundle( "mvn:org.ops4j.pax.logging/pax-logging-service" )
            .addBundle( "mvn:org.apache.felix/org.apache.felix.eventadmin" )
            .addBundle( "mvn:org.apache.felix/org.apache.felix.configadmin" )
            .addBundle( "mvn:org.ops4j.pax.wicket/pax-wicket-service" )
            .addBundle( "mvn:org.knopflerfish.bundle.useradmin/useradmin_api" )
            .addBundle( "mvn:org.ops4j.pax.web/pax-web-service" )
            .addBundle( "mvn:org.apache.felix/org.osgi.compendium" )
            .addBundle( "mvn:org.ops4j.pax.wicket.integrationTest/bootstrap" );
        onTestBundleConfigure( bundleProvision );

        PaxRunnerConnector connector = create( bundleProvision );
        connector.setPlatform( Platforms.valueOf( platform ) );

        return connector;
    }

    /**
     * Override this method to further initialize configuration.
     */
    protected void onTestBundleConfigure( BundleProvision bundleProvision )
    {
        // Do nothing
    }

    /**
     * Return bundle given the symbolic name. Returns {@code null} if not found.
     *
     * @param symbolicName The bundle symbolic name.
     *
     * @return The bundle given the symbolic name.
     *
     * @since 0.5.4
     */
    protected final Bundle getBundleBySymbolicName( String symbolicName )
    {
        Bundle[] bundles = bundleContext.getBundles();
        for( Bundle bundle : bundles )
        {
            String bundleSymbolicName = bundle.getSymbolicName();
            if( bundleSymbolicName.equals( symbolicName ) )
            {
                return bundle;
            }
        }

        return null;
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
        Bundle paxWicketBundle = getBundleBySymbolicName( SYMBOLIC_NAME_PAX_WICKET_SERVICE );
        assertNotNull( paxWicketBundle );
        return paxWicketBundle;
    }
}
