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
package org.ops4j.pax.wicket.samples.departmentstore.service.alternative.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;

public class AlternativeDepartmentStoreImpl
    implements DepartmentStore, Serializable
{

    private static final long serialVersionUID = 1L;

    private List<Floor> m_floors;
    private String m_name;
    private String m_history;

    public AlternativeDepartmentStoreImpl( String name )
    {
        m_name = name;

        m_floors = new ArrayList<Floor>();
        Floor floor;
        Franchisee franchisee;

        floor = new AlternativeFloorImpl( "Basement" );
        m_floors.add( floor );

        franchisee = new Franchisee( "DaPietro", "Italian restaurant" );
        floor.addFranchisee( franchisee );

        franchisee = new Franchisee( "Paese", "Corsican Cuisine" );
        floor.addFranchisee( franchisee );

        m_history = "No history available";
    }

    public String getName()
    {
        return m_name;
    }

    public List<Floor> getFloors()
    {
        return m_floors;
    }

    public List<Franchisee> getAllFranchisees()
    {
        List<Franchisee> all = new ArrayList<Franchisee>();
        for( Floor floor : m_floors )
        {
            List<Franchisee> franchisees = floor.getFranchisees();
            all.addAll( franchisees );
        }
        return all;
    }

    public String getHistory()
    {
        return m_history;
    }
}
