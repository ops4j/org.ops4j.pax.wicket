
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.api;

import java.util.Map;

import javax.servlet.FilterConfig;
public interface ConfigurableFilterConfig extends FilterConfig {

    /**
     * <p>setFilterName.</p>
     *
     * @param name a {@link java.lang.String} object.
     */
    void setFilterName(String name);

    /**
     * <p>putInitParameter.</p>
     *
     * @param name a {@link java.lang.String} object.
     * @param parameter a {@link java.lang.String} object.
     */
    void putInitParameter(String name, String parameter);

    /**
     * <p>putAllInitParameter.</p>
     *
     * @param parameterMap a {@link java.util.Map} object.
     */
    void putAllInitParameter(Map<String, String> parameterMap);
}
