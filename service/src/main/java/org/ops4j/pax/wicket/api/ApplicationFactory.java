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
 * This factory allows clients to promote their own Wicket {@link WebApplication}s instead of configuring the
 * pre-defined ones automatically created by Pax Wickets application factory. As tradeoff the {@link WebApplication} has
 * to call {@link ApplicationLifecycleListener#onInit(WebApplication)} in the overwritten init() method and
 * {@link ApplicationLifecycleListener#onDestroy(WebApplication)} in an overwritten onDestroy() method. If they are not
 * called pax-wicket could not work correctly!
 */
public interface ApplicationFactory {

    /**
     * Create a new {@link WebApplication} which have to call the events in the {@link ApplicationLifecycleListener} if
     * pax-wicket should work correctly.
     */
    WebApplication createWebApplication(ApplicationLifecycleListener lifecycleListener);

}
