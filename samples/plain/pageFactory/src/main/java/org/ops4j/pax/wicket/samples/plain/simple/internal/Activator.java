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
package org.ops4j.pax.wicket.samples.plain.simple.internal;

import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.ops4j.pax.wicket.api.support.AbstractPageFactory;
import org.ops4j.pax.wicket.api.support.DefaultWebApplicationFactory;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class Activator implements BundleActivator {

    private DefaultWebApplicationFactory applicationFactory;
    private AbstractPageFactory<SimpleTestPage> pageFactory;

    public void start(BundleContext context) throws Exception {
        applicationFactory =
                new DefaultWebApplicationFactory(context, WicketApplication.class, "plain.pagefactory", "plain/pagefactory");
        pageFactory = new AbstractPageFactory<SimpleTestPage>(context, "pagefactory", "plain.pagefactory", "pagefactory") {
            public Class<SimpleTestPage> getPageClass() {
                return SimpleTestPage.class;
            }

            public SimpleTestPage createPage(PageParameters params) {
                return new SimpleTestPage("some content to show that this really works...");
            }
        };
        pageFactory.register();
        applicationFactory.register();
    }

    public void stop(BundleContext context) throws Exception {
        pageFactory.dispose();
        applicationFactory.dispose();
    }

}
