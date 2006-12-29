/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
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
package org.ops4j.pax.wicket.service;

import wicket.Component;

/** This is the model interface of Content.
 * Content is categorized as the model of a Wicket component hierarchy, which can be unloaded, replaced
 * and moved in runtime, without needing to shut the application down.
 *
 */
public interface Content<E extends Component>
{

    String MOUNTPOINT = "pax.wicket.mountpoint";

    String APPLICATION_NAME = "pax.wicket.applicationname";

    String DESTINATIONID = "pax.wicket.destinationid";

    String DESTINATIONID_UNKNOWN = "";

    String PAGE_NAME = "pax.wicket.pagename";

    String PAGE_ID = "pax.wicket.pageid";

    String CONTENTID = "pax.wicket.contentid";

    String CONTAINMENTID = "pax.wicket.containmentid";

    String DEPLOYMENT_MODE = "pax.wicket.deploymentmode";

    String HOMEPAGE_CLASSNAME = "pax.wicket.homepage.classname";

    /**
     * Returns the destination id of this {@code Content} instance. This method must not return {@code null} object.
     * <p>
     * The destination id is constructed by concatenating the containment id, ".", and the wicket component id. For
     * example, If the containment id is "overview.tabs" and the wicket component id is "quickMenu", the returned
     * destination id is "overview.tabs.quickMenu".
     * </p>
     *
     * @return The destination id of this {@code Content} instance.
     * @since 1.0.0
     */
    String getDestinationId();

    /**
     * Create the wicket component represented by this {@code Content} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * <li>In the use case of Wicket 2 environment. The parent is passed through constructor during creational of the
     * component created by this method.</li>
     * </ul>
     * </p>
     *
     * @param parent The parent component of the component to be created by this method. This argument must not be
     *            {@code null}.
     *
     * @return The wicket component represented by this {@code Content} instance.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code parent} arguement is {@code null}.
     * @since 1.0.0
     */
    <T extends Component> E createComponent( T parent );
}
