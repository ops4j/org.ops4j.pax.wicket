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
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

/**
 * @author Christoph Läubrich
 */
@RunWith(JUnit4TestRunner.class)
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
        Constructor<?> constructor = clazz.getConstructor(BundleContext.class, String.class);
        Object listener = constructor.newInstance(bundleContext, PaxWicketBean.INJECTION_SOURCE_UNDEFINED);
        //Fetch the inject method
        Method injectMethod = clazz.getMethod("inject", Object.class, Class.class);
        //Create a dummy class and let it inject
        PageClassDummy dummy = new PageClassDummy();
        injectMethod.invoke(listener, dummy, dummy.getClass());
        assertNotNull("BundleContext was not injected automatically!", dummy.injectMe);
    }

    public static class PageClassDummy {

        @PaxWicketBean
        private BundleContext injectMe;

    }
}
