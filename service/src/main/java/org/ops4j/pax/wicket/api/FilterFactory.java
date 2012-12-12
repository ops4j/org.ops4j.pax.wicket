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

import javax.servlet.Filter;
import javax.servlet.ServletException;

public interface FilterFactory {

    /**
     * Service property name for the configuration of the priority of a {@link Filter}, it is used if two factories have
     * the same service rank as defined by the OSGi Service Layer
     */
    String FILTER_PRIORITY = "pax.wicket.filter.priority";

    /**
     * Service property name for the configuration wheather or not the lifecycle of a {@link Filter} is maintained by
     * PAX Wicket or not (defaults to <code>true</code>)
     */
    String MAINTAIN_LIFECYCLE = "pax.wicket.filter.maintainlifecycle";

    /**
     * Service property name for the configuration to which {@link WebApplicationFactory} this filter belongs to
     */
    String APPLICATION_NAME = Constants.APPLICATION_NAME;

    Filter createFilter(ConfigurableFilterConfig filterConfig) throws ServletException;

}
