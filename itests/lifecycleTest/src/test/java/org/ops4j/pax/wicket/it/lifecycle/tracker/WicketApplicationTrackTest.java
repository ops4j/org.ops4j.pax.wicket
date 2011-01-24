/*  Copyright 2011 Edward Yakop, Andreas Pieber
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
package org.ops4j.pax.wicket.it.lifecycle.tracker;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import org.junit.Test;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

public final class WicketApplicationTrackTest extends PaxWicketIntegrationTest {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public final Option[] configureAdditionalProvision() {
        return options(provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples.view")
            .artifactId("pax-wicket-samples-view-application").versionAsInProject()));
    }

    @Test
    public final void testApplicationTracker()
        throws Exception {
        sleep(2000);
        Bundle simpleAppBundle =
            getBundleBySymbolicName(bundleContext,
                "org.ops4j.pax.wicket.samples.view.pax-wicket-samples-view-application");
        assertNotNull(simpleAppBundle);
        assertEquals(simpleAppBundle.getState(), Bundle.ACTIVE);
        ServiceReference[] beforeStopServices = simpleAppBundle.getRegisteredServices();
        assertEquals(14, beforeStopServices.length);

        Bundle bundle = getPaxWicketServiceBundle(bundleContext);
        bundle.stop();

        ServiceReference[] services = simpleAppBundle.getRegisteredServices();
        assertNotNull(services);
        assertEquals(3, services.length);
    }
}
