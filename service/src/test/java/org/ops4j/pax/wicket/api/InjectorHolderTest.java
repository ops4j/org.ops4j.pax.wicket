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
package org.ops4j.pax.wicket.api;

import static org.junit.Assert.assertSame;

import org.apache.wicket.Page;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;

public class InjectorHolderTest {

    @Test(expected = IllegalStateException.class)
    public void testRetrieveNonExistingInjectionHolder_shouldThrowIllegalStateException() throws Exception {
        new WicketTester();
        InjectorHolder.getInjector();
    }

    @Test
    public void testRetrieveInjectorAfterSettingIt_shouldSucceed() throws Exception {
        PaxWicketInjector paxWicketInjector = new PaxWicketInjector() {
            public void inject(Object toInject, Class<?> toHandle) {
                // not required for this test
            }
        };
        WicketTester wicketTester = new WicketTester(new WebApplication() {
            @Override
            protected void init() {
                super.init();
            }

            @Override
            public Class<? extends Page> getHomePage() {
                // it doesn't mind
                return null;
            }
        });
        InjectorHolder.setInjector(wicketTester.getApplication().getApplicationKey(), paxWicketInjector);
        assertSame(paxWicketInjector, InjectorHolder.getInjector());
    }

}
