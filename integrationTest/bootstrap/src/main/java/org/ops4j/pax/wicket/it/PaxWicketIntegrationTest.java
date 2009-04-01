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
import org.ops4j.pax.exam.Option;
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

    @Configuration
    public final Option[] configureProvisions()
    {
        return options(
            provision( "mvn:org.ops4j.pax.logging/pax-logging-api/1.3.0" ),
            provision( "mvn:org.ops4j.pax.logging/pax-logging-service/1.3.0" ),
            provision( "mvn:org.apache.felix/org.apache.felix.eventadmin/1.0.0" ),
            provision( "mvn:org.apache.felix/org.apache.felix.configadmin/1.0.10" ),
            provision( "mvn:org.knopflerfish.bundle.useradmin/useradmin_api/1.1.0" ),
            provision( "mvn:org.ops4j.pax.web/pax-web-service/0.5.2" ),
            provision( "mvn:org.apache.felix/org.osgi.compendium/1.2.0" ),
            provision( "mvn:org.ops4j.pax.wicket/pax-wicket-service" )
        );
    }

    /**
     * Return bundle given the symbolic name. Returns {@code null} if not found.
     *
     * @param bundleContext Bundle context. This argument must not be {@code null}.
     * @param symbolicName  Bundle symbolic name. This argument must not be {@code null}.
     *
     * @return The bundle given the symbolic name.
     *
     * @since 0.5.5
     */
    protected final Bundle getBundleBySymbolicName( BundleContext bundleContext, String symbolicName )
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
     * @param bundleContext Bundle context. This argument must not be {@code null}.
     *
     * @return The pax wicket service bundle.
     *
     * @since 0.5.5
     */
    protected final Bundle getPaxWicketServiceBundle( BundleContext bundleContext )
    {
        Bundle paxWicketBundle = getBundleBySymbolicName( bundleContext, SYMBOLIC_NAME_PAX_WICKET_SERVICE );
        assertNotNull( paxWicketBundle );
        return paxWicketBundle;
    }
}
