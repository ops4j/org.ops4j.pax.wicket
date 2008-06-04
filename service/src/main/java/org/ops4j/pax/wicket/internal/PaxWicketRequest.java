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
package org.ops4j.pax.wicket.internal;

import javax.servlet.http.HttpServletRequest;
import org.apache.wicket.protocol.http.servlet.ServletWebRequest;
import static org.ops4j.lang.NullArgumentException.validateNotNull;

/**
 * @author Niclas Hedhman, Edward Yakop
 * @since 1.0.0
 */
final class PaxWicketRequest extends ServletWebRequest
{

    /**
     * Protected constructor.
     *
     * @param httpServletRequest The servlet request information
     *
     * @throws IllegalArgumentException Thrown if one or both arguments are {@code null}.
     * @since 1.0.0
     */
    PaxWicketRequest( HttpServletRequest httpServletRequest )
        throws IllegalArgumentException
    {
        super( httpServletRequest );

        validateNotNull( httpServletRequest, "httpServletRequest" );
    }

    @Override
    public final String getRelativePathPrefixToContextRoot()
    {
        // Returns empty string as the wicket handler is at the same level as the servlet.
        return "";
    }

    @Override
    public int getDepthRelativeToWicketHandler()
    {
        // Returns 0, as the wicket handler is at the same level as servlet.
        return 0;
    }
}
