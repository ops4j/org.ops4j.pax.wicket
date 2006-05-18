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
package org.ops4j.pax.wicket.samples.departmentstore.model.internal;

import java.util.ArrayList;
import java.util.List;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;

public class DepartmentStoreImpl implements DepartmentStore
{
    private List<Floor> m_floors;
    private String m_name;

    public DepartmentStoreImpl( String name )
    {
        m_name = name;
        m_floors = new ArrayList<Floor>();
        Floor floor;
        Franchisee franchisee;

        floor = new FloorImpl( "C" );
        m_floors.add( floor );
        franchisee = new FranchiseeImpl( "McDonald", "Fast food, bad mouth feel.");
        floor.addFranchisee( franchisee );
        franchisee = new FranchiseeImpl( "KFC", "Fast Food, Licking your ...." );
        floor.addFranchisee( franchisee );
        floor = new FloorImpl( "LG" );
        franchisee = new FranchiseeImpl( "Levi", "Jeans" );
        floor.addFranchisee( franchisee );
        franchisee = new FranchiseeImpl( "Vinci", "Shoes, shoes, shoes..." );
        floor.addFranchisee( franchisee );
        m_floors.add( floor );
        floor = new FloorImpl( "G" );
        franchisee = new FranchiseeImpl( "Famous Amos", "Who?" );
        floor.addFranchisee( franchisee );
        franchisee = new FranchiseeImpl( "RejectShop", "Reject the shop" );
        floor.addFranchisee( franchisee );
        franchisee = new FranchiseeImpl( "Watson's", "Drugs and Rock'n Roll");
        floor.addFranchisee( franchisee );
        m_floors.add( floor );
        floor = new FloorImpl( "1st" );
        franchisee = new FranchiseeImpl( "Esquire Kitchen", "Chinese Food" );
        floor.addFranchisee( franchisee );
        m_floors.add( floor );
        floor = new FloorImpl( "2nd" );
        franchisee = new FranchiseeImpl( "Teppanyaki", "Fried Food of Japan like the Japanese never tasted." );
        floor.addFranchisee( franchisee );
        m_floors.add( floor );
        floor = new FloorImpl( "3rd" );
        franchisee = new FranchiseeImpl( "Apple Computers", "Fruity machines" );
        floor.addFranchisee( franchisee );
        m_floors.add( floor );
        floor = new FloorImpl( "4th" );
        franchisee = new FranchiseeImpl( "Hang Wah Seng", "More food" );
        m_floors.add( floor );
        floor = new FloorImpl( "RoofTop" );
        m_floors.add( floor );
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
}
