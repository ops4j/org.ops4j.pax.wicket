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
package org.ops4j.pax.wicket.samples.departmentstore.model;

import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public final class DepartmentStoreModelTracker extends ServiceTracker {
    private DepartmentStore departmentStore;

    public DepartmentStoreModelTracker(BundleContext bundleContext) {
        super(bundleContext, DepartmentStore.class.getName(), null);
    }

    public final DepartmentStore getDepartmentStore() {
        return departmentStore;
    }

    @Override
    public final Object addingService(ServiceReference serviceReference) {
        departmentStore = (DepartmentStore) super.addingService(serviceReference);
        return departmentStore;
    }

    @Override
    public final void removedService(ServiceReference serviceReference, Object object) {
        super.removedService(serviceReference, object);
        departmentStore = null;
    }
}
