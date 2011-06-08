/**
 * Copyright OPS4J
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may
 * not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.samples.departmentstore.view.franchisee.internal;

import java.util.List;
import java.util.UUID;

import org.ops4j.pax.wicket.api.ContentSourceFactory;
import org.ops4j.pax.wicket.api.ContentSourceModelMapping;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;
import org.ops4j.pax.wicket.util.DefaultContentSourceDescriptor;
import org.ops4j.pax.wicket.util.DefaultContentSourceModelMapping;

public class FranchiseeModelFactory implements ContentSourceFactory<DepartmentStore> {

    public static final String REFERENCE_MODEL = "franchiseeModel";

    public ContentSourceModelMapping createContentSourceMappings(DepartmentStore departmentStore) {
        DefaultContentSourceModelMapping mapping = new DefaultContentSourceModelMapping(true);
        List<Floor> floors = departmentStore.getFloors();
        for (Floor floor : floors) {
            List<Franchisee> franchisees = floor.getFranchisees();
            for (Franchisee franchisee : franchisees) {
                String destinationId = floor.getName() + ".franchisee";
                String modelId = floor.getName() + franchisee.getName() + UUID.randomUUID();

                mapping.addModelObject(modelId, franchisee);

                DefaultContentSourceDescriptor franchiseePanel =
                    new DefaultContentSourceDescriptor(franchisee.getName(), franchisee.getName(),
                        FranchiseePanel.class);
                franchiseePanel.addDestination(destinationId);
                franchiseePanel.addOverwrite(REFERENCE_MODEL, modelId);
                mapping.addContentSource(franchiseePanel);
            }
        }
        return mapping;
    }

}
