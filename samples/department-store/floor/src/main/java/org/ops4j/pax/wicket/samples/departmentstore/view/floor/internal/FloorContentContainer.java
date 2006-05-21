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
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.service.DefaultContentContainer;
import org.osgi.framework.BundleContext;
import wicket.Component;

public class FloorContentContainer extends DefaultContentContainer
{
    private final Floor m_floor;

    public FloorContentContainer( Floor floor, String containmentId, String destinationId,
                                  BundleContext bundleContext, String applicationname )
    {
        super( bundleContext, applicationname, containmentId, destinationId );
        m_floor  = floor;
    }

    protected Component createComponent( String id )
    {
        return new FloorPanel( id, this, m_floor );
    }

    protected void removeComponent( Component component )
    {
        //TODO: Auto-generated, need attention.
    }

}
