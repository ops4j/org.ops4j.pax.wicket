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
package org.ops4j.pax.wicket.util;

import org.apache.wicket.Component;
import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.osgi.framework.BundleContext;

public class DefaultContentSourceFactory extends AbstractContentSource implements
        ComponentContentSource<Component> {

    private final ComponentContentSource<? extends Component> componentContentSourceFactory;

    public DefaultContentSourceFactory(BundleContext bundleContext, String applicationName,
            ComponentContentSource<? extends Component> componentContentSourceFactory)
        throws IllegalArgumentException {
        super(bundleContext, componentContentSourceFactory.getSourceId(), applicationName);
        this.componentContentSourceFactory = componentContentSourceFactory;
        setDestination(componentContentSourceFactory.getDestinations());
    }

    public Component createSourceComponent(String wicketId) {
        return componentContentSourceFactory.createSourceComponent(wicketId);
    }

}
