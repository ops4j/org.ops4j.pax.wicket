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
package org.ops4j.pax.wicket.it.lifecycle;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;

import javax.inject.Inject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.RESOLVED;

/**
 * {@code LifecycleTest} tests pax wicket service lifecycle.
 *
 * @author edward.yakop@gmail.com
 * @since 0.5.4
 */
@RunWith(PaxExam.class)
public final class LifecycleTest extends PaxWicketIntegrationTest {
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
                provision(mavenBundle().groupId("org.ops4j.pax.wicket")
                        .artifactId("org.ops4j.pax.wicket.service").versionAsInProject()));
    }

    /**
     * Tests stopping pax-wicket service bundle.
     *
     * @since 0.5.4
     */
    @Test
    public final void testStopping() {
        Bundle bundle = getPaxWicketServiceBundle(bundleContext);

        // Pax wicket bundle must be active
        assertEquals(ACTIVE, bundle.getState());

        try {
            bundle.stop();
        } catch (BundleException e) {
            fail("Stopping bundle must not throw any exception.");
        }

        // Once stopped, bundle state must be in resolved state
        assertEquals(RESOLVED, bundle.getState());
    }
}
