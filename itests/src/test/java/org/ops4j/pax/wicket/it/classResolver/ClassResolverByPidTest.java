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
package org.ops4j.pax.wicket.it.classResolver;

import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.wicket.api.Constants.APPLICATION_NAME;
import static org.osgi.framework.Constants.SERVICE_PID;

import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Properties;

import javax.inject.Inject;

import org.apache.wicket.application.IClassResolver;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.ops4j.pax.tinybundles.core.TinyBundles;
import org.ops4j.pax.wicket.it.PaxWicketIntegrationTest;
import org.ops4j.pax.wicket.it.classResolver.simpleLibraries.PublicClass;
import org.ops4j.pax.wicket.it.classResolver.simpleLibraries.PublicThatAccessPrivateClass;
import org.ops4j.pax.wicket.it.classResolver.simpleLibraries.internal.Activator;
import org.ops4j.pax.wicket.it.classResolver.simpleLibraries.internal.PrivateClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.cm.Configuration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.cm.ManagedService;

@RunWith(JUnit4TestRunner.class)
public final class ClassResolverByPidTest extends PaxWicketIntegrationTest {

    @Inject
    private BundleContext bundleContext;

    @org.ops4j.pax.exam.junit.Configuration
    public final Option[] provisionSimpleLibraries() {
        return combine(configureProvisions(),
            provision(mavenBundle().groupId("org.ops4j.pax.wicket").artifactId("org.ops4j.pax.wicket.service")
                .versionAsInProject()),
            provision(TinyBundles
                .bundle()
                .add(PublicClass.class)
                .add(PublicThatAccessPrivateClass.class)
                .add(PrivateClass.class)
                .add(Activator.class)
                .set(Constants.EXPORT_PACKAGE, "org.ops4j.pax.wicket.it.classResolver.simpleLibraries")
                .set(Constants.BUNDLE_ACTIVATOR,
                    "org.ops4j.pax.wicket.it.classResolver.simpleLibraries.internal.Activator")
                .set(Constants.BUNDLE_SYMBOLICNAME, "org.ops4j.pax.wicket.it.classResolver.simpleLibraries")
                .set(Constants.IMPORT_PACKAGE, " org.ops4j.pax.wicket.*,org.apache.wicket.*,org.osgi.*;")
                .build(TinyBundles.withBnd())));
    }

    @Test
    public final void testPrivateLibrariesByUpdatingConfigurationByInvokingDirectly() throws Throwable {
        ServiceReference classResolverReference = getLibraryClassResolverReference();
        assertFalse(isApplicationNameKeyExists(classResolverReference));

        ManagedService managedService = (ManagedService) bundleContext.getService(classResolverReference);
        Properties dictionary = new Properties();

        // Lets update configuration to expose our sample library to abc, def application
        dictionary.put(APPLICATION_NAME, new String[]{ "abc", "def" });
        managedService.updated(dictionary);

        assertTrue(isApplicationNameKeyExists(classResolverReference));
        validateThatClassResolverIsExposedToAbcAndDef();

        bundleContext.ungetService(classResolverReference);
    }

    private void validateThatClassResolverIsExposedToAbcAndDef() throws Throwable {
        ServiceReference[] references = bundleContext.getServiceReferences(
            IClassResolver.class.getName(), "(" + APPLICATION_NAME + "=abc)"
            );
        assertNotNull(references);
        assertEquals(references.length, 1);
        ServiceReference reference = references[0];
        String[] applicationNames = (String[]) reference.getProperty(APPLICATION_NAME);
        assertEquals(2, applicationNames.length);
        assertEquals(applicationNames[0], "abc");
        assertEquals(applicationNames[1], "def");

        // Verify that this is the simple libraries class resolver
        IClassResolver classResolver = (IClassResolver) bundleContext.getService(reference);
        String className = "org.ops4j.pax.wicket.it.classResolver.simpleLibraries.internal.PrivateClass";
        Class clazz = classResolver.resolveClass(className);
        assertNotNull(clazz);
        assertEquals(clazz.getName(), className);

        bundleContext.ungetService(reference);
    }

    private boolean isApplicationNameKeyExists(ServiceReference reference) {
        String[] keys = reference.getPropertyKeys();
        boolean isApplicatioNameKeyExists = false;
        for (String key : keys) {
            if (APPLICATION_NAME.equals(key)) {
                isApplicatioNameKeyExists = true;
                break;
            }
        }
        return isApplicatioNameKeyExists;
    }

    @Test
    @SuppressWarnings("unchecked")
    public final void testPrivateLibrariesByUpdatingConfigurationViaConfigAdmin() throws Throwable {
        ServiceReference classResolverReference = getLibraryClassResolverReference();

        // Ensure no configuration is applied
        assertFalse(isApplicationNameKeyExists(classResolverReference));

        // Lets update configuration to expose our sample library to abc, def application via Configuration Admin
        ServiceReference configAdminRef = bundleContext.getServiceReference(ConfigurationAdmin.class.getName());
        assertNotNull(configAdminRef);
        ConfigurationAdmin configAdmin = (ConfigurationAdmin) bundleContext.getService(configAdminRef);

        String classResolverBundleLocation = classResolverReference.getBundle().getLocation();
        Configuration configuration = configAdmin.getConfiguration("libraryPid", classResolverBundleLocation);
        Dictionary properties = configuration.getProperties();
        if (properties == null) {
            properties = new Hashtable();
            properties.put(SERVICE_PID, "libraryPid");
        }
        properties.put(APPLICATION_NAME, new String[]{ "abc", "def" });
        configuration.update(properties);

        // Wait for 1 secs
        sleep(5000);

        // Lets test that configuration is now applied
        classResolverReference = getLibraryClassResolverReference();
        assertTrue(isApplicationNameKeyExists(classResolverReference));
        validateThatClassResolverIsExposedToAbcAndDef();

        // Remove configuration
        configuration.delete();

        bundleContext.ungetService(configAdminRef);
        bundleContext.ungetService(classResolverReference);
    }

    private ServiceReference getLibraryClassResolverReference() throws InvalidSyntaxException {
        ServiceReference[] references = bundleContext.getServiceReferences(
            IClassResolver.class.getName(), "(" + SERVICE_PID + "=libraryPid)"
            );
        assertNotNull(references);
        assertEquals(references.length, 1);
        return references[0];

    }
}
