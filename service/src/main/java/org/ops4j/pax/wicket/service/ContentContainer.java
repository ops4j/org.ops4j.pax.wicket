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

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import wicket.Component;

public interface ContentContainer
{

    /**
     * Returns the containment id of this {@code ContentContainer}.
     * 
     * @return The containment id.
     * 
     * @since 1.0.0
     */
    String getContainmentId();

    /**
     * Create components that has the specified {@code id} id. Returns an empty list if there is no component with the
     * specified {@code id}.
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
     * @param wicketId The wicket id. This argument must not be {@code null}.
     * @param parent The parent of created components. This argument must not be {@code null}.
     * 
     * @return A list of component with the specified {@code wicketId} and {@code parent}.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * 
     * @since 1.0.0
     */
    <E extends Component, T extends Component> List<E> createComponents( String wicketId, T parent )
        throws IllegalArgumentException;

    /**
     * Returns the comparator for component with the specified {@code id}. Returns {@code null} if the comparator does
     * not exists.
     * 
     * @param <E> A component class.
     * @param id The component with the specified {@code id}.
     * @return The comparator of the specified {@code id}.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * 
     * @since 1.0.0
     */
    <E extends Component> Comparator<E> getComparator( String id, Locale locale )
        throws IllegalArgumentException;

    /**
     * Dispose this {@code ContentContainer} instance.
     * 
     * @since 1.0.0
     */
    void dispose();
}
