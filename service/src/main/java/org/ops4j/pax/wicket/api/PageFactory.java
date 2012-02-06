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

import org.apache.wicket.Page;
import org.apache.wicket.request.mapper.parameter.PageParameters;

/**
 * PageFactory instances are capable of providing bookmarkable web pages, as defined by Wicket.
 */
public interface PageFactory<T extends Page> {

    /**
     * Returns the page class instance represented by this {@code PageFactory}.
     * 
     * @return The page class represented by this {@code PageFactory}.
     * 
     * @since 1.0.0
     */
    Class<T> getPageClass();

    /**
     * Creates a page with the specified {@code params}.
     * 
     * @param params The page parameters.
     * 
     * @return An instance of page.
     * 
     * @since 1.0.0
     */
    T createPage(PageParameters params);
}
