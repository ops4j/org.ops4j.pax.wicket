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

public interface Constants {

    /**
     * Service property name for the configuration of the <i>MountPoint</i>.
     */
    String MOUNTPOINT = "pax.wicket.mountpoint";

    /**
     * Service property name for the configuration of the <i>ApplicationName</i>.
     */
    String APPLICATION_NAME = "pax.wicket.applicationname";

    /**
     * Service property name for the configuration of the <i>Servlet Context Params</i> which are typically defined in
     * the web.xml.
     */
    String CONTEXT_PARAMS = "pax.wicket.contextparams";

    /**
     * Service property name for the configuration of the <i>PageName</i>.
     */
    String PAGE_NAME = "pax.wicket.pagename";

    /**
     * Service property name for the configuration of the <i>PageId</i>.
     */
    String PAGE_ID = "pax.wicket.pageid";

    /**
     * Service property name for the configuration of the classname of the <i>HomePage</i>.
     */
    String HOMEPAGE_CLASSNAME = "pax.wicket.homepage.classname";

}
