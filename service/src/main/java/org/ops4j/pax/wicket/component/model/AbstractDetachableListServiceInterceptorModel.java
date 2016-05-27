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

package org.ops4j.pax.wicket.component.model;

import java.util.List;

/**
 * This model makes it easier to work with OSGi services in Wicket. It is a LoadableDetachableModel that loads data via Services retrieved
 * from the Service Registry. The model takes a Service Type and, when load is called, calls {@code doLoad} on each service. When the list is loaded,
 * it is passed to the method {@code parseList}, where the list and contents can be modified before the list is returned. The list returned
 * from load will contain the output of every service that did not return exceptions, modified by {@code parseList}.
 *
 * @author Martin "von beek" Nybo Nielsen
 * @param <T> The type of service to poll for populating the model.
 * @param <E> The return type expected from the model.
 * @version $Id: $Id
 * @since 3.0.5
 */
public abstract class AbstractDetachableListServiceInterceptorModel<T extends Object, E extends Object> extends AbstractDetachableListServiceModel<T, E>{

    /**
     * <p>Constructor for AbstractDetachableListServiceInterceptorModel.</p>
     *
     * @param serviceType a {@link java.lang.Class} object.
     * @param owningBundleClass a {@link java.lang.Class} object.
     * @param filter a {@link java.lang.String} object.
     */
    public AbstractDetachableListServiceInterceptorModel(Class<T> serviceType, Class owningBundleClass, String filter) {
	super(serviceType, owningBundleClass, filter);
    }

    /**
     * <p>Constructor for AbstractDetachableListServiceInterceptorModel.</p>
     *
     * @param serviceType a {@link java.lang.Class} object.
     * @param owningBundleClass a {@link java.lang.Class} object.
     */
    public AbstractDetachableListServiceInterceptorModel(Class<T> serviceType, Class owningBundleClass) {
	super(serviceType, owningBundleClass);
    }

    /**
     * <p>Constructor for AbstractDetachableListServiceInterceptorModel.</p>
     *
     * @param serviceType a {@link java.lang.Class} object.
     * @param owningBundleObject a {@link java.lang.Object} object.
     * @param filter a {@link java.lang.String} object.
     */
    public AbstractDetachableListServiceInterceptorModel(Class<T> serviceType, Object owningBundleObject, String filter) {
	super(serviceType, owningBundleObject, filter);
    }

    /**
     * <p>Constructor for AbstractDetachableListServiceInterceptorModel.</p>
     *
     * @param serviceType a {@link java.lang.Class} object.
     * @param owningBundleObject a {@link java.lang.Object} object.
     */
    public AbstractDetachableListServiceInterceptorModel(Class<T> serviceType, Object owningBundleObject) {
	super(serviceType, owningBundleObject);
    }

    
    
    /**
     * <p>parseList.</p>
     *
     * @param tpParse a {@link java.util.List} object.
     */
    protected abstract void parseList(List<E> tpParse);
    
    /** {@inheritDoc} */
    @Override
    protected List<E> load() {
	List<E> loadedList = super.load();
	parseList(loadedList);
	return loadedList;
    }

    

    
}
