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

/**
 * An injector using the ApplicationContextMock and the PaxWicketSpringBeanComponentInjector in a quite simplified
 * way making working based on them easier.
 */
public class SimplifiedPaxWicketInjector {
    private final ApplicationContextMock applicationContext = new ApplicationContextMock();

    public static SimplifiedPaxWicketInjector registerBeanInjector(WicketTester tester) {
        return new SimplifiedPaxWicketInjector(tester);
    }

    private SimplifiedPaxWicketInjector(WicketTester tester) {
        tester.getApplication().getComponentInstantiationListeners().add(
                new PaxWicketSpringBeanComponentInjector(tester.getApplication(), applicationContext));
    }

    public void registerBean(String name, Object bean) {
        applicationContext.putBean(name, bean);
    }
}
