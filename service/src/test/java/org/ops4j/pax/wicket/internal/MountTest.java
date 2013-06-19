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
package org.ops4j.pax.wicket.internal;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;

import org.apache.wicket.Page;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.ops4j.pax.wicket.api.support.DefaultPageMounter;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.ServiceReference;

public class MountTest {

    private BundleContext bundleContext;
    private WicketTester wicketTester;

    @SuppressWarnings("serial")
    public static final class TestPage extends Page {
    }

    @Before
    public void setup() throws Exception {
        wicketTester = new WicketTester();
        bundleContext = mock(BundleContext.class);
        when(bundleContext.createFilter(anyString()))
            .thenAnswer(new Answer<Filter>() {
                public Filter answer(InvocationOnMock invocation) throws Throwable {
                    return FrameworkUtil.createFilter((String) invocation.getArguments()[0]);
                }
            });
        when(bundleContext.getProperty(Constants.FRAMEWORK_VERSION)).thenReturn("1.5.0");
    }

    @Test
    public void mountAndUnmountPageInSeparateThread() throws Exception {
        final DefaultPageMounter defaultPageMounter = new DefaultPageMounter("testapp", bundleContext);
        defaultPageMounter.addMountPoint("test", TestPage.class);
        final ServiceReference reference = mock(ServiceReference.class);
        when(bundleContext.getService(reference)).thenReturn(defaultPageMounter);
        final PageMounterTracker pageMounterTracker =
            new PageMounterTracker(bundleContext, wicketTester.getApplication(), "testapp");

        /*
         * execute the ServiceTracker-operations in another thread. E.g. in Felix this is executed in the
         * FrameworkStartLevel-Thread, that is not associated to any Session
         */
        Callable<Void> mounterTask = new Callable<Void>() {
            public Void call() throws Exception {
                pageMounterTracker.addingService(reference);
                pageMounterTracker.removedService(reference, defaultPageMounter);
                return null;
            }
        };

        FutureTask<Void> futureTask = new FutureTask<Void>(mounterTask);
        new Thread(futureTask).start();
        futureTask.get(); // should not throw an ExecutionException
    }
}
