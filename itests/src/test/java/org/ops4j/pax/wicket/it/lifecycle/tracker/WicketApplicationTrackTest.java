/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.it.lifecycle.tracker;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;

import javax.inject.Inject;

import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

@RunWith(PaxExam.class)
public final class WicketApplicationTrackTest extends PaxWicketIntegrationTest {

    /**
     * We don't use this member, except for synchronizing the test. 
     * Injecting it guarantees that the service is available before our test runs.
     */
    @Inject @Filter("(pax.wicket.applicationname=navigation)")
    private WebApplicationFactory<WebApplication> factory;
    
    @Inject
    private BundleContext bundleContext;

    @Configuration
    public final Option[] configureAdditionalProvision() {
        return combine(configureProvisions(),
            provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-util").versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-request").versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-core").versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-auth-roles").versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-extensions").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.base").artifactId("ops4j-base").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket").artifactId("org.ops4j.pax.wicket.service")
                .versionAsInProject()), provision(mavenBundle().groupId("org.ops4j.pax.wicket.samples")
                .artifactId("org.ops4j.pax.wicket.samples.navigation").versionAsInProject()));
    }

    @Test
    public final void testAppicationTraker() throws Exception {
        // FIXME long timeout for Hudson. Use @Inject and @Filter with timeout instead.
        //sleep(30000);
        Bundle paxWicketBundle = getPaxWicketServiceBundle(bundleContext);
        Bundle simpleAppBundle = getBundleBySymbolicName(bundleContext, "org.ops4j.pax.wicket.samples.navigation");
        assertNotNull(simpleAppBundle);
        //assertEquals(Bundle.ACTIVE, simpleAppBundle.getState());
        ServiceReference[] beforeStopServices = paxWicketBundle.getRegisteredServices();
        assertNotNull(beforeStopServices);
        assertEquals(3, beforeStopServices.length);

        simpleAppBundle.stop();

        ServiceReference[] services = paxWicketBundle.getRegisteredServices();
        assertNull(services);
    }
}
