/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.wicket.api;

import org.apache.wicket.protocol.http.WebApplication;

/**
 * Since pax-wicket does not have full control about self-created {@link WebApplication} it's required to use the
 * {@link #onInit(WebApplication)} and {@link #onDestroy(WebApplication)} methods in the listener to allow pax-wicket to
 * react on changes in the application.
 */
public interface ApplicationLifecycleListener {

    /**
     * This method should be called during the init method overwritten.
     */
    void onInit(WebApplication wicketApplication);

    /**
     * This method should be called during the onDestroy method overwritten.
     */
    void onDestroy(WebApplication wicketApplication);

}
