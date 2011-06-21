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
package org.ops4j.pax.wicket.internal.injection;

import java.util.List;

import org.apache.wicket.Component;
import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.ops4j.pax.wicket.util.DefaultComponentContentSourceFactory;
import org.osgi.framework.BundleContext;

public class ComponentContentSourceFactoryDecorator implements ComponentContentSource<Component>,
        InjectionAwareDecorator {

    private String applicationName;
    private ComponentContentSource<? extends Component> componentContentSourceFactory;
    private BundleContext bundleContext;
    private DefaultComponentContentSourceFactory baseComponentContentSourceFactory;

    public ComponentContentSourceFactoryDecorator() {
    }

    public String getSourceId() {
        return baseComponentContentSourceFactory.getSourceId();
    }

    public List<String> getDestinations() {
        return baseComponentContentSourceFactory.getDestinations();
    }

    public Component createSourceComponent(String wicketId) {
        return baseComponentContentSourceFactory.createSourceComponent(wicketId);
    }

    public void start() throws Exception {
        baseComponentContentSourceFactory =
            new DefaultComponentContentSourceFactory(bundleContext, applicationName, componentContentSourceFactory);
        baseComponentContentSourceFactory.register();
    }

    public void stop() throws Exception {
        baseComponentContentSourceFactory.dispose();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setComponentContentSourceFactory(
            ComponentContentSource<? extends Component> componentContentSourceFactory) {
        this.componentContentSourceFactory = componentContentSourceFactory;
    }

}
