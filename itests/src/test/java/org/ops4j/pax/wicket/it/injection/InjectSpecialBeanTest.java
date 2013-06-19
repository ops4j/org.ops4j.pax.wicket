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
/**
 * 
 */
package org.ops4j.pax.wicket.it.injection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.osgi.framework.Bundle.ACTIVE;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.ops4j.pax.wicket.spi.ProxyTargetLocatorFactory;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

/**
 * @author Christoph Läubrich
 */
@RunWith(PaxExam.class)
public class InjectSpecialBeanTest extends PaxWicketIntegrationTest {

    @Inject
    private BundleContext bundleContext;

    @Configuration
    public final Option[] configureAdditionalProvision() {
        return combine(configureProvisions(), //
        provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-util").versionAsInProject()), //
        provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-request").versionAsInProject()), //
        provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-core").versionAsInProject()), //
        provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-auth-roles").versionAsInProject()), //
        provision(mavenBundle().groupId("org.apache.wicket").artifactId("wicket-extensions").versionAsInProject()), //
        provision(mavenBundle().groupId("org.ops4j.base").artifactId("ops4j-base").versionAsInProject()), //
        provision(mavenBundle().groupId("org.ops4j.pax.wicket").artifactId("org.ops4j.pax.wicket.service").versionAsInProject()));
    }

    /**
     * Tests stopping pax-wicket service bundle.
     * 
     * @throws ClassNotFoundException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchMethodException
     * @throws SecurityException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @since 0.5.4
     */
    @Test
    public final void testStopping() throws ClassNotFoundException, InstantiationException, IllegalAccessException, SecurityException, NoSuchMethodException,
            IllegalArgumentException, InvocationTargetException {
        Bundle bundle = getPaxWicketServiceBundle(bundleContext);
        // Pax wicket bundle must be active
        assertEquals(ACTIVE, bundle.getState());
        //Load the class through the bundles classloader
        Class<?> clazz = bundle.loadClass("org.ops4j.pax.wicket.internal.injection.BundleAnalysingComponentInstantiationListener");
        //Construct an instance
        Constructor<?> constructor = clazz.getConstructor(BundleContext.class, String.class, ServiceTracker.class);
        Object listener = constructor.newInstance(bundleContext, PaxWicketBeanInjectionSource.INJECTION_SOURCE_SCAN, new ServiceTracker(bundleContext, ProxyTargetLocatorFactory.class, null));
        //Fetch the inject method
        Method injectMethod = clazz.getMethod("inject", Object.class, Class.class);
        //Create a dummy class and let it inject
        PageClassDummy dummy = new PageClassDummy();
        injectMethod.invoke(listener, dummy, dummy.getClass());
        assertNotNull("BundleContext was not injected automatically!", dummy.injectMe);
    }

    public static class PageClassDummy {

        @Inject
        private BundleContext injectMe;

    }
}
