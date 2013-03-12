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
package org.ops4j.pax.wicket.samples.blueprint.injection.simple.internal.view;

import static org.apache.wicket.util.time.Duration.seconds;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.ops4j.pax.wicket.samples.blueprint.injection.simple.internal.service.MyService;

/**
 * Very simple page providing entry points into various other examples.
 */
public class Homepage extends WebPage {

    private static final long serialVersionUID = 1L;

    /**
     * If you're using SpringDM it is also possible to reference the bean only
     * by name. For blueprint you have to add the name of the bean here. We're
     * very sorry about this problem, but blueprint offers the limitation here.
     * Otherwise you can reference any bean here from the bundles
     * "applicationContext". The "local/bundles" application context is created
     * automatically from all .xml files in the OSGI-INF/blueprint/ folder. You
     * can inject named beans, osgi service or any other named entity such as
     * other pax-wicket components.
     */

    @SuppressWarnings("unused")
    @Named("defaultMyServiceBean")
    @Inject
    private MyService         serviceBean;

    public Homepage() {
        add(new Label("oneComponent", "blub"));
        add(new AbstractAjaxTimerBehavior(seconds(1)) {
            private static final long serialVersionUID = -913071581477833094L;

            @Override
            protected void onTimer(AjaxRequestTarget target) {
                System.out.println(getPageId());
            }
        });
    }
}
