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
package org.ops4j.pax.wicket.service.internal;

import org.ops4j.pax.wicket.service.Content;

/**
 * {@code ContentTrackingCallback} implementors handle add and removal of {@link Content}.
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
public interface ContentTrackingCallback
{
    /**
     * The specified {@code content} is added with the specified {@code wicketId} id.
     * 
     * @param wicketId The wicket identifier. This argument must not be {@code null} or empty.
     * @param content The content. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * 
     * @since 1.0.0
     */
    void addContent( String wicketId, Content content )
        throws IllegalArgumentException;

    /**
     * The specified {@code content} is not available with the specified {@code wicketId} id.
     * 
     * @param wicketId The wicket identifier. This argument must not be {@code null} or empty.
     * @param content The content. This argument must not be {@code null}.
     * 
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * 
     * @return A {@code boolean} indicator whether removal is successfull.
     * 
     * @since 1.0.0
     */
    boolean removeContent( String wicketId, Content content );
}
