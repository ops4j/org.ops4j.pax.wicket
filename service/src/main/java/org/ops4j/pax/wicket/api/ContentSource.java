/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 * Copyright 2010 David Leangen.
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

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;

/**
 * This is the model interface of ContentSource. ContentSource is categorized as the model of a Wicket component
 * hierarchy, which can be unloaded, replaced and moved in runtime, without needing to shut the application down.
 */
public interface ContentSource<E extends Component> {

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
    String DESTINATIONS = "pax.wicket.destination";

    /**
     * Service property value for the configuration of a <i>Destination</i> that is not known, and therefor will not be
     * <i>wired</i>.
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
     * Service property name for the configuration of the classname of the <i>HomePage</i>.
     */
    String HOMEPAGE_CLASSNAME = "pax.wicket.homepage.classname";

    /**
     * Basic Roles are used for ContentSource authorization.
     * <p>
     * The current user must have one or more of the roles that are listed in the BASIC_ROLES property of a
     * ContentSource. Otherwise the component(s) of that ContentSource will not be created.
     * </p>
     * <p>
     * This service property is an array of Strings.
     * </p>
     */
    String BASIC_ROLES = "pax.wicket.auth.roles.basic";

    /**
     * Required Roles are used for ContentSource authorization.
     * <p>
     * The current user must have all the roles that are listed in the REQUIRED_ROLES property of a ContentSource.
     * Otherwise the component(s) of that ContentSource will not be created.
     * </p>
     * <p>
     * This service property is an array of Strings.
     * </p>
     */
    String REQUIRED_ROLES = "pax.wicket.auth.roles.required";

    /**
     * Returns the content source id.
     * 
     * @return The content source id.
     * 
     * @since 1.0.0
     */
    String getSourceId();

    /**
     * Returns the destinations of this {@code ContentSource} instance. This method must not return {@code null} object.
     * <p>
     * The <i>Destination</i> is constructed by concatenating the <i>AggregationPointMatchExpression</i>, ".", and the
     * <i>ContentMatchExpression</i>. For example, If the <i>AggregationPointMatchExpression</i> is "overviewtabs" and
     * the <i>ContentMatchExpression</i> is "quickMenu", the returned <i>Destination</i> is "overviewtabs.quickMenu".
     * </p>
     * 
     * @return The destination id of this {@code ContentSource} instance.
     * 
     * @since 1.0.0
     */
    String[] getDestinations();

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * <p>
     * General convention:<br/>
     * <ul>
     * <li>In the use case of Wicket 1 environment. The callee of this method responsibles to add the component created
     * this method;</li>
     * </ul>
     * </p>
     * 
     * @param wicketId The wicket id. This argument must not be {@code null}.
     * 
     * @return The wicket component represented by this {@code ContentSource} instance, or null if user has no access to
     *         this ContentSource.
     * 
     * @throws IllegalArgumentException Thrown if the {@code wicketId} argument is {@code null}.
     * @since 1.0.0
     */
    <T extends MarkupContainer> E createSourceComponent(String wicketId);

    /**
     * Create the wicket component represented by this {@code ContentSource} instance. This method must not return
     * {@code null} object.
     * 
     * @param wicketId The wicket id. This argument must not be {@code null}.
     * @param parent the parent {@code MarkupContainer}
     * 
     * @return The wicket component represented by this {@code ContentSource} instance, or null if user has no access to
     *         this ContentSource.
     * 
     * @throws IllegalArgumentException Thrown if the {@code wicketId} argument is {@code null}.
     */
    <T extends MarkupContainer> E createSourceComponent(String wicketId, T parent);
}
