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
package org.ops4j.pax.wicket.test.spring;

import org.apache.wicket.util.tester.WicketTester;
import org.junit.Test;
import org.ops4j.pax.wicket.test.ExamplePage;
import org.ops4j.pax.wicket.test.ExamplePage.TestInjectionBean;

public class PaxWicketBeanInjectionTest {

    @Test
    public void testStartPage_shouldShowMockingContent() throws Exception {
        ApplicationContextMock appContext = new ApplicationContextMock();
        appContext.putBean("testBean", new TestInjectionBean("testContent"));
        WicketTester tester = new WicketTester();
        tester.getApplication().getComponentInstantiationListeners()
            .add(new PaxWicketSpringBeanComponentInjector(tester.getApplication(), appContext));

        tester.startPage(ExamplePage.class);

        tester.assertLabel("test", "testContent");
    }
}
