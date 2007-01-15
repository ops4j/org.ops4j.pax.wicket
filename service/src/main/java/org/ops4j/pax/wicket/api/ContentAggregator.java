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

import java.util.Comparator;
import java.util.List;
import java.util.Locale;

import wicket.Component;

/** The <i>ContentAggregator</i> defines the <i>AggregationPoint</i>.
 * <p>
 * <i>ContentSource</i>s can attach themselves to an <i>AggregationPoint</i>, which is defined by a
 * <i>ContentAggregator</i>. During the request, i.e. a call to <code>createComponents( String contentID, Component
 * parent )</code>, the ContentAggregator must delegate the creation of components to the <i>wired</i> ContentSources.
 *
 * </p>
 * <ol>
 * <li>
 * The <i>ApplicationName</i> of both the <i>ContentAggregator</i> and the <i>ContentSource</i> must be
 * idential, done with a case-sensitive string comparison.
 * </li>
 * <li>
 * The <i>Destination</i> consists of two parts, separated by a dot ("."). The first part is the
 * <i>AggregationMatchExpression</i> and the part after the dot is called the <i>ContentMatchExpression</i>.
 * </li>
 * <li>
 * The <i>ContentAggregator</i> must track <i>ContentSource</i> services which has a <i>AggregationMatchExpression</i>
 * that evaluates to true for the <i>AggregationPoint</i> of the <i>ContentAggregator</i>.
 * </li>
 * <li>
 * If the <i>AggregationMatchExpression</i> starts with <b>"regexp("</b> (no quotes), then the
 * <i>AggregationMatchExpression</i> is a regular expression up to the closing paranthesis. If that regular expression
 * evaluates to <strong>true</strong> for the <i>AggregationPoint</i> of this <i>ContentAggregator</i>, then the
 * <i>wiring</i> is established.
 * </li>
 * <li>
 * If the <i>AggregationMatchExpression</i> does not start with <b>"regexp("</b> (no quotes), then the expression must
 * be case-sensitive equal of the <i>AggregationPoint</i> of this <i>ContentAggregator</i>.
 * </li>
 * <li>
 * On <code>createComponents( String contentID, Component parent )</code>, the <i>ContentAggregator</i> must find the
 * <i>ContentSource</i>s that are <i>wired</i> to the <i>ContentAggregator</i> and where the
 * <i>ContentMatchExpression</i> <b>matches</b> the <code>contentID</code> in the method call. This match is performed
 * identically to the one done for the <i>AggregationPoint</i> and <i>AggregationMatchExpression</i>.
 * </li>
 * <li>
 * For each found <i>ContentSource</i>, which is wired and has a matching <i>ContentMatchExpression</i>, the
 * <i>ContentAggregator</i> must call the <code>ContentSource.createComponent( Component parent )</code> method.
 * </li>
 * </ol>
 */
public interface ContentAggregator
{

    /**
     * Returns the containment id of this {@code ContentAggregator}.
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
     * @param contentId The wicket id. This argument must not be {@code null}.
     * @param parent The parent of created components. This argument must not be {@code null}.
     *
     * @return A list of component with the specified {@code contentId} and {@code parent}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     *
     * @since 1.0.0
     */
    <E extends Component, T extends Component> List<E> createComponents( String contentId, T parent )
        throws IllegalArgumentException;

    /**
     * Returns the comparator for component with the specified {@code id}. Returns {@code null} if the comparator does
     * not exists.
     *
     * @param <E> A component class.
     * @param id The component with the specified {@code id}.
     * @param locale The locale of the Comparator.
     *
     * @return The comparator of the specified {@code id}.
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     *
     * @since 1.0.0
     */
    <E extends Component> Comparator<E> getComparator( String id, Locale locale )
        throws IllegalArgumentException;

    /**
     * Dispose this {@code ContentAggregator} instance.
     *
     * @since 1.0.0
     */
    void dispose();
}
