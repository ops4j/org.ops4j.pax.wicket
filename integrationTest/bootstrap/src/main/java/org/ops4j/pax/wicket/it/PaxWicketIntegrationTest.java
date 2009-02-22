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

import static org.junit.Assert.assertNotNull;
import org.junit.runner.RunWith;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.logProfile;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
@RunWith( JUnit4TestRunner.class )
public abstract class PaxWicketIntegrationTest
{

    protected static final String SYMBOLIC_NAME_PAX_WICKET_SERVICE = "org.ops4j.pax.wicket.pax-wicket-service";

    @Inject
    protected BundleContext bundleContext;

    @Configuration
    public static Option[] configure()
    {
        return options(
            logProfile()
        );
    }

    @Configuration
    protected Option[] configureProvisions()
    {
        return options(
            provision( "mvn:org.apache.felix/org.apache.felix.eventadmin" ),
            provision( "mvn:org.apache.felix/org.apache.felix.configadmin" ),
            provision( "mvn:org.ops4j.pax.wicket/pax-wicket-service" ),
            provision( "mvn:org.knopflerfish.bundle.useradmin/useradmin_api" ),
            provision( "mvn:org.ops4j.pax.web/pax-web-service" ),
            provision( "mvn:org.apache.felix/org.osgi.compendium" ),
            provision( "mvn:org.ops4j.pax.wicket.integrationTest/bootstrap" )
        );
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
