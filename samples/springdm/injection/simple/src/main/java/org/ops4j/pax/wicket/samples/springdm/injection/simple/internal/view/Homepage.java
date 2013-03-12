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
package org.ops4j.pax.wicket.samples.springdm.injection.simple.internal.view;

import javax.inject.Inject;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.ops4j.pax.wicket.api.PaxWicketBeanInjectionSource;
import org.ops4j.pax.wicket.samples.springdm.injection.simple.internal.service.MyService;

/**
 * Very simple page providing entry points into various other examples.
 */
public class Homepage extends WebPage {

    private static final long   serialVersionUID = 1L;
    private static final String HOMEPAGE_TEXT    = "Welcome to the most simple pax-wicket injection application based on springdm.";

    /**
     * Since you're using SpringDM it is also possible to reference the bean
     * only by type. You can reference any bean here from the bundles
     * "applicationContext". The "local/bundles" application context is created
     * automatically from all spring .xml files in the META-INF/spring/ folder.
     * You can beans by name or type. In addition osgi service or any other
     * named entity such as other pax-wicket components are also supported.
     */
    @PaxWicketBeanInjectionSource(PaxWicketBeanInjectionSource.INJECTION_SOURCE_SPRING)
    @Inject
    private MyService           serviceBean;

    public Homepage() {
        String serviceText = serviceBean.someEchoMethod(HOMEPAGE_TEXT);
        add(new Label("oneComponent", serviceText));
    }
}
