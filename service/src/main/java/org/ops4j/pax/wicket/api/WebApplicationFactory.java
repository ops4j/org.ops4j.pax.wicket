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

import org.apache.wicket.protocol.http.WebApplication;

/**
 * WebApplicationFactory returns Wicket WebApplication instances
 *
 * @since 2.0.0
 */
public interface WebApplicationFactory<C extends WebApplication> {

    /**
     * Returns the WebApplication class instance represented by this {@code WebApplicationFactory}.
     *
     * @return The WebApplication class represented by this {@code WebApplicationFactory}.
     */
    Class<C> getWebApplicationClass();

    /**
     * Called for every web application of this factory that is instantiated. This method is called
     * just after the construction using the default constructor.
     *
     * @param application the web application that is being instantiated
     *
     */
    void onInstantiation(C application);


}
