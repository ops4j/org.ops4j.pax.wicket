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

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
     * Create components that has the specified {@code id} id. Returns an empty list
     * if there is no component with the specified {@code id}.
     * 
     * @param wicketId The wicket id. This argument must not be {@code null}.
     * @return A list of component id.
     * 
     * @throws IllegalArgumentException Thrown if the specified {@code wicketId} argument is {@code null}.
     * 
     * @since 1.0.0
     */
    <T extends Component> List<T> createComponents( String wicketId )
        throws IllegalArgumentException;
    
    /**
     * Returns the comparator for component with the specified {@code id}.
     * Returns {@code null} if the comparator does not exists.
     * 
     * @param <T> A component class.
     * @param id The component with the specified {@code id}.
     * @return The comparator of the specified {@code id}.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * 
     * @since 1.0.0
     */
    <T extends Component> Comparator<T> getComparator( String id, Locale locale )
        throws IllegalArgumentException;

    /**
     * Dispose this {@code ContentContainer} instance.
     *
     * @since 1.0.0
     */
    void dispose();
}
