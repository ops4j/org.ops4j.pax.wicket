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

public interface FilterFactory extends Comparable<FilterFactory> {

    /**
     * Service property name for the configuration of the priority of a <i>Filter</i>
     */
    String FILTER_PRIORITY = "pax.wicket.filter.priority";

    Integer getPriority();

    String getApplicationName();

    Filter createFilter(ConfigurableFilterConfig filterConfig);

}
