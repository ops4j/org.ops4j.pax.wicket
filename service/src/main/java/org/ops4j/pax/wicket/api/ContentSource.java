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
package org.ops4j.pax.wicket.api;

import wicket.Component;

/**
 * This is the model interface of ContentSource.
 * ContentSource is categorized as the model of a Wicket component hierarchy, which can be unloaded, replaced
 * and moved in runtime, without needing to shut the application down.
 */
public interface ContentSource<E extends Component>
{

    /**
     * Service property name for the configuration of the <i>MountPoint</i>.
     */
    String MOUNTPOINT = "pax.wicket.mountpoint";

    /**
     * Service property name for the configuration of the <i>ApplicationName</i>.
     */
    String APPLICATION_NAME = "pax.wicket.applicationname";

    /**
     * Service property name for the configuration of the <i>Destination</i> of a <i>ContentSource</i>.
     */
    String DESTINATION = "pax.wicket.destination";

    /**
     * Service property value for the configuration of a <i>Destination</i> that is not known, and therefor will not
     * be <i>wired</i>.
     */
    String DESTINATION_UNKNOWN = "";

    /**
     * Service property name for the configuration of the <i>PageName</i>.
     */
    String PAGE_NAME = "pax.wicket.pagename";

    /**
     * Service property name for the configuration of the <i>PageId</i>.
     */
    String PAGE_ID = "pax.wicket.pageid";

    /**
     * Prefix used in the Constants.SERVICE_PID to uniquely identify Pax Wicket ContentSource instances for the
     * Configuration Admin service.
     */
    String SOURCE_ID = "pax.wicket.source";

    /**
     * Service property name for the configuration of the <i>AggregationPoint</i>.
     */
    String AGGREGATION_POINT = "pax.wicket.aggregation.point";

    /**
     * Service property name for the configuration of <i>DeploymentMode</i>.
     */
    String DEPLOYMENT_MODE = "pax.wicket.deploymentmode";

    /**
     * Service property name for the configuration of the classname of the <i>HomePage</i>.
     */
    String HOMEPAGE_CLASSNAME = "pax.wicket.homepage.classname";

    /**
     * Returns the destination id of this {@code ContentSource} instance. This method must not return {@code null} object.
     * <p>
     * The destination id is constructed by concatenating the containment id, ".", and the wicket component id. For
     * example, If the containment id is "overview.tabs" and the wicket component id is "quickMenu", the returned
     * destination id is "overview.tabs.quickMenu".
     * </p>
     *
     * @return The destination id of this {@code ContentSource} instance.
     *
     * @since 1.0.0
     */
    String getDestinationId();

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
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
     *               {@code null}.
     *
     * @return The wicket component represented by this {@code ContentSource} instance.
     *
     * @throws IllegalArgumentException Thrown if the specified {@code parent} arguement is {@code null}.
     * @since 1.0.0
     */
    <T extends Component> E createComponent( T parent );
}
