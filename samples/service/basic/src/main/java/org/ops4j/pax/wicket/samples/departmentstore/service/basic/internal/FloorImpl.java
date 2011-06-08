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
package org.ops4j.pax.wicket.samples.departmentstore.service.basic.internal;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;

public class FloorImpl implements Floor, Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private List<Franchisee> franchisees;

    public FloorImpl(String name) {
        this.name = name;
        franchisees = new ArrayList<Franchisee>();
    }

    public String getName() {
        return name;
    }

    public List<Franchisee> getFranchisees() {
        return franchisees;
    }

    public void addFranchisee(Franchisee franchisee) {
        franchisees.add(franchisee);
    }

    public void removeFranchise(Franchisee franchisee) {
        franchisees.remove(franchisee);
    }
}
