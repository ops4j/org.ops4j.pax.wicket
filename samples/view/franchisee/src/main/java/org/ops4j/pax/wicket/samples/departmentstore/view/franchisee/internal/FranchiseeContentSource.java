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

import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;
import org.ops4j.pax.wicket.util.AbstractContentSource;
import org.osgi.framework.BundleContext;

/**
 * {@code FranchiseeContentSource}
 * 
 * @author Niclas Hedhman, Edward Yakop
 * @since 1.0.0
 */
public class FranchiseeContentSource extends AbstractContentSource implements ComponentContentSource<FranchiseePanel> {

    private Franchisee franchisee;

    public FranchiseeContentSource(BundleContext context, Franchisee franchisee, String applicationName) {
        super(context, franchisee.getName(), applicationName);
        this.franchisee = franchisee;
    }

    public FranchiseePanel createSourceComponent(String wicketId) {
        return new FranchiseePanel(wicketId, franchisee);
    }

}
