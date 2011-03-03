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
package org.ops4j.pax.wicket.it;

import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.options;
import static org.ops4j.pax.exam.CoreOptions.provision;

import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

@RunWith(JUnit4TestRunner.class)
public abstract class PaxWicketIntegrationTest {

    protected static final String SYMBOLIC_NAME_PAX_WICKET_SERVICE = "org.ops4j.pax.wicket.pax-wicket-service";

    @Configuration
    public final Option[] configureProvisions() {
        return options(
            provision(mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-api").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.logging").artifactId("pax-logging-service")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.eventadmin")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.felix").artifactId("org.apache.felix.configadmin")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.geronimo.specs").artifactId("geronimo-servlet_2.5_spec")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.apache.servicemix.bundles")
                .artifactId("org.apache.servicemix.bundles.asm")
                .versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-util").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-io").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-http").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-continuation").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-server").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-security").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-servlet").versionAsInProject()),
            provision(mavenBundle().groupId("org.eclipse.jetty").artifactId("jetty-xml").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.web").artifactId("pax-web-api").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.web").artifactId("pax-web-spi").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.web").artifactId("pax-web-runtime").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.web").artifactId("pax-web-jetty").versionAsInProject()),
            provision(mavenBundle().groupId("org.osgi").artifactId("org.osgi.compendium").versionAsInProject()),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket").artifactId("pax-wicket-service")
                .versionAsInProject()));
    }

    /**
     * Return bundle given the symbolic name. Returns {@code null} if not found.
     *
     * @param bundleContext Bundle context. This argument must not be {@code null}.
     * @param symbolicName Bundle symbolic name. This argument must not be {@code null}.
     *
     * @return The bundle given the symbolic name.
     *
     * @since 0.5.5
     */
    protected final Bundle getBundleBySymbolicName(BundleContext bundleContext, String symbolicName) {
        Bundle[] bundles = bundleContext.getBundles();
        for (Bundle bundle : bundles) {
            String bundleSymbolicName = bundle.getSymbolicName();
            if (bundleSymbolicName.equals(symbolicName)) {
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
    protected final Bundle getPaxWicketServiceBundle(BundleContext bundleContext) {
        Bundle paxWicketBundle = getBundleBySymbolicName(bundleContext, SYMBOLIC_NAME_PAX_WICKET_SERVICE);
        assertNotNull(paxWicketBundle);
        return paxWicketBundle;
    }
}
