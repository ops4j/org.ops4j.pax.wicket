/*
 * Copyright 2011 Fabian Souczek
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class FilterConfiguration
{

    private List<FilterDescription> m_filterDescriptions = new ArrayList<FilterDescription>();

    public FilterConfiguration add( String classNameOfFilter, boolean required )
    {
        synchronized ( m_filterDescriptions )
        {
            m_filterDescriptions.add( new FilterDescription( classNameOfFilter, required ) );
        }
        return this;
    }

    /**
     * @return an unmodifiable list of descriptions of all filters registered in this configuration
     */
    public List<FilterDescription> getFilters()
    {
        return Collections.unmodifiableList( m_filterDescriptions );
    }
    
}
