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
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;

import javax.inject.Inject;
import javax.servlet.ServletContext;

import org.apache.wicket.protocol.http.WebApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.util.Filter;
import org.ops4j.pax.wicket.api.PaxWicketInjector;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.hooks.weaving.WeavingHook;

@RunWith(PaxExam.class)
public final class WicketApplicationTrackTest extends PaxWicketIntegrationTest {

    /**
     * We don't use these members, except for synchronizing the test. 
     * Injecting them guarantees that the services are available before our test runs.
     * The timeouts are rather high for the benefit of our CI server.
     */
    @Inject @Filter(value = "(pax.wicket.applicationname=navigation)", timeout = 30000)
    private WebApplicationFactory<WebApplication> factory;
    
    @Inject @Filter(value = "(pax.wicket.applicationname=navigation)", timeout = 30000)
    private PaxWicketInjector injector;
    
    @Inject @Filter(value = "(osgi.web.symbolicname=org.ops4j.pax.wicket.service)", timeout = 30000)
    private ServletContext servletContext;
    
    private static final int EXPECTED_SERVICE_COUNT_WITH_APPLICATION = 5;
    private static final int EXPECTED_SERVICE_COUNT_WITHOUT_APPLICATION = 2;

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
    public final void testAppicationTracker() throws InterruptedException, BundleException {
        assertNotNull(factory);
        assertNotNull(injector);
        assertNotNull(servletContext);
        Bundle paxWicketBundle = getPaxWicketServiceBundle(bundleContext);
        Bundle simpleAppBundle = getBundleBySymbolicName(bundleContext, "org.ops4j.pax.wicket.samples.navigation");
        assertNotNull("Simple Bundle was null",simpleAppBundle);
        // I hate such moves but it seams that otherwise this bundle wont be started but just starting...
        Thread.sleep(3000);
        assertEquals("Simple Bundle is not active", Bundle.ACTIVE, simpleAppBundle.getState());
        ServiceReference[] beforeStopServices = paxWicketBundle.getRegisteredServices();
        assertNotNull("No services at all", beforeStopServices);
        String failMessage = "Not enought services, the following are registered " + buildServiceGraph(beforeStopServices) + ", expected count is " + EXPECTED_SERVICE_COUNT_WITH_APPLICATION;
        assertEquals(failMessage, EXPECTED_SERVICE_COUNT_WITH_APPLICATION, beforeStopServices.length);
        simpleAppBundle.stop();
        ServiceReference[] afterStopServices = paxWicketBundle.getRegisteredServices();
        assertNotNull("No services at all anymore", afterStopServices);
        assertEquals("Not all services are unregistered, registered ones are: "+buildServiceGraph(afterStopServices), EXPECTED_SERVICE_COUNT_WITHOUT_APPLICATION, afterStopServices.length);
        //Check if the remaining service is the WeavingHook
        assertTrue("remaining service does not contain the WeavingHook", containsService(afterStopServices, WeavingHook.class));
        assertTrue("remaining service does not contain the default ProxyTargetLocatorFactory", containsService(afterStopServices, ProxyTargetLocatorFactory.class));
    }


}
