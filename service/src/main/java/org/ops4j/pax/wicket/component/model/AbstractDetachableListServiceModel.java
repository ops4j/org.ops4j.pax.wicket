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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.apache.wicket.model.LoadableDetachableModel;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This model makes it easier to work with OSGi services in Wicket. It is a LoadableDetachableModel that loads data via Services retrieved 
 * from the Service Registry. The model takes a Service Type and, when load is called, calls {@code doLoad} on each service. The list returned
 * from load will contain the output of every service that did not return exceptions.
 * @author Martin Nybo Nielsen
 * @param <T> The type of service to poll for populating the model.
 * @param <E> The return type expected from the model.
 */
public abstract class AbstractDetachableListServiceModel<T extends Object, E extends Object> extends LoadableDetachableModel<List<E>>{
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetachableListServiceModel.class);
    
    private final Class<T> serviceType;
    private final BundleContext context;
    private final String filter;
    
    /**
     * Takes the type of the service to retrieve data from. When the {@code load} method is called, all valid services of 
     * {@code serviceType} will be polled for data. {@code owningBundleObject} is used to properly define the BundleContext which the
     * model should use for calling the service registry. Any object residing in the same bundle as the page where the model is used is recommended.
     * @param serviceType The type of the service to retrieve model data from
     * @param owningBundleClass
     * @param filter An LDAP filter to use to narrow the search for registered services.
     */
    public AbstractDetachableListServiceModel(Class<T> serviceType, Class owningBundleClass, String filter) {
	this.serviceType = serviceType;

	if(!BundleReference.class.isAssignableFrom(owningBundleClass)){
	    throw new IllegalArgumentException("This model can only be used from within OSGi containers. The supplied class doe snot appear to originate in "
		    + "a bundle "+owningBundleClass.getCanonicalName());
	}
	context = BundleReference.class.cast(owningBundleClass.getClassLoader()).getBundle().getBundleContext();
	
	if(!serviceType.isInterface()){
	    throw new IllegalArgumentException("The serviceType must be an interface, was: "+serviceType.getCanonicalName()+". Error occured in "+context.getBundle().getSymbolicName());
	}
	
	this.filter = filter;
    }

    /**
     * Takes the type of the service to retrieve data from. When the {@code load} method is called, all valid services of 
     * {@code serviceType} will be polled for data. {@code owningBundleObject} is used to properly define the BundleContext which the
     * model should use for calling the service registry. Any object residing in the same bundle as the page where the model is used is recommended.
     * @param serviceType The type of the service to retrieve model data from
     * @param owningBundleClass
     */
    public AbstractDetachableListServiceModel(Class<T> serviceType, Class owningBundleClass) {
	this(serviceType, owningBundleClass, null);
    }
    
    /**
     * Takes the type of the service to retrieve data from. When the {@code load} method is called, all valid services of 
     * {@code serviceType} will be polled for data. {@code owningBundleObject} is used to properly define the BundleContext which the
     * model should use for calling the service registry. Any object residing in the same bundle as the page where the model is used is recommended.
     * @param serviceType The type of the service to retrieve model data from
     * @param owningBundleObject Any object which resides in the bundle which uses this model.
     * @param filter An LDAP filter to use to narrow the search for registered services.
     */
    public AbstractDetachableListServiceModel(Class<T> serviceType, Object owningBundleObject, String filter) {
	this(serviceType, owningBundleObject.getClass(), filter);
    }

    /**
     * Takes the type of the service to retrieve data from. When the {@code load} method is called, all valid services of 
     * {@code serviceType} will be polled for data. {@code owningBundleObject} is used to properly define the BundleContext which the
     * model should use for calling the service registry. Any object residing in the same bundle as the page where the model is used is recommended.
     * @param serviceType The type of the service to retrieve model data from
     * @param owningBundleObject Any object which resides in the bundle which uses this model.
    */
    public AbstractDetachableListServiceModel(Class<T> serviceType, Object owningBundleObject) {
	this(serviceType, owningBundleObject.getClass(), null);
    }
    
    /**
     *
     * @param source
     * @return
     * @throws Exception
     */
    protected abstract E doLoad(T source) throws Exception;
    
    @Override
    protected List<E> load() {
	List<E> returnValues = new ArrayList<E>();
	Collection<ServiceReference<T>> refs = Collections.EMPTY_LIST;
	try{
	refs = context.getServiceReferences(serviceType, filter);
	}catch(InvalidSyntaxException e){
	    LOGGER.error("Could not load object from service. There was a problem with the filter syntax. "
		    + "Returning null. Service called: "+serviceType+" from bundle "+context.getBundle().getSymbolicName(), e);
	    return null;
	}
	for(ServiceReference<T> ref : refs){
	    try{
	    T service = context.getService(ref);
	    returnValues.add(doLoad(service));
	    }catch(Exception e){
		LOGGER.error("Could not load object from service. Trying next service. Service called: "+serviceType+" from bundle "+context.getBundle().getSymbolicName(), e);
	    }finally{
		context.ungetService(ref);
	    }
	}
	return returnValues;
    }
    
}
