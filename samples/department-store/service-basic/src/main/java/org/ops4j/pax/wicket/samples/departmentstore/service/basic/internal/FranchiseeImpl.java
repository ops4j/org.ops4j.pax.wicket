/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.wicket.samples.departmentstore.service.basic.internal;

import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;
import java.io.Serializable;

public class FranchiseeImpl
    implements Franchisee, Serializable
{

    private static final long serialVersionUID = 1L;

    private String m_name;
    private String m_description;

    public FranchiseeImpl( String name, String description )
    {
        m_name = name;
        m_description = description;
    }

    public String getName()
    {
        return m_name;
    }

    public void setName( String name )
    {   
        m_name = name;
    }

    public String getDescription()
    {
        return m_description;
    }

    public void setDescription( String description )
    {
        m_description = description;
    }
}
