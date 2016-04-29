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

import java.util.HashMap;
import java.util.Map;
import org.apache.wicket.model.LoadableDetachableModel;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This model makes it easier to work with OSGi services in Wicket. It is a LoadableDetachableModel that loads data via Services retrieved 
 * from the Service Registry. The model has an abstract method {@code doLoad) which must be implemented with the logic to return the model
 * object. The model also contains another method [@code getService}, which allows the implementer to retrieve any service type from the
 * service registry. This Class should be used when a model object is created based of several services, these can be polled by [@code getService}
 * inside the [@code doLoad} method. When the doLoad method returns, all service references opened by [@code getService} will be closed automatically.
 * @author Martin Nybo Nielsen
 * @param <T> The models return type.
 */
public abstract class AbstractDetachableMultiServiceModel<T extends Object> extends LoadableDetachableModel<T>{

    public static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetachableMultiServiceModel.class);
    private Map<Class, ServiceReference> references;
    private final BundleContext context;

    /**
     * This constructor must be passed a class from the bundle, in which this class is used. Simply passing {@code this.getClass()} is usually sufficient.
     * The objective of the constructor is to make sure that the BundleContext that is retrieving services, is the same as the context that contains the page
     * in which the model will be used.
     * @param owningBundleClass Any class from within the bundle containing whatever page the model is used in.
     */
    public AbstractDetachableMultiServiceModel(Class owningBundleClass) {
	context = BundleReference.class.cast(owningBundleClass.getClassLoader()).getBundle().getBundleContext();
    }

    /**
     * This constructor must be passed a class from the bundle, in which this class is used. Simply passing {@code this} is usually sufficient.
     * The objective of the constructor is to make sure that the BundleContext that is retrieving services, is the same as the context that contains the page
     * in which the model will be used.
     * @param owningBundleObject Any object from within the bundle containing whatever page the model is used in.
     */
    public AbstractDetachableMultiServiceModel(Object owningBundleObject) {
	this(owningBundleObject.getClass());
    }
    
    
    
    
    
    @Override
    protected T load() {
	references = new HashMap<Class, ServiceReference>();
	try{
	    return doLoad();
	}catch(Exception ex){
	    LOGGER.error("Caught exception when loading model, returning null", ex);
	    return null;
	}finally{
	    closeAll();
	}
	
    }
    
    /**
     * Implement this method to build the model object returned by this class. Call {@code getService} to retrieve services from the service registry.
     * Any services retrieved in this way will be automatically closed after this method returns. The method can throw any exception. In this case, the 
     * exception is logged as an error, and the model will return null.
     * @return The models return object, or null if an exception is thrown.
     * @throws Exception Throw an exception in case of error situations. Note that OSGi services (also those returned by {@code getService}
     * may throw Runtime exceptions, in case of services leaving or changing. If not caught, these will be passed out of the {@ code doLoad} method
     * and get logged, resulting in the model returning null, as with any other exception.
     */
    protected abstract T doLoad() throws Exception;

    /**
     * This method will retrieve a service from the service registry, and remember the reference, so it can be closed after {@code doLoad returns}.
     * @param <E> The type of the service
     * @param serviceType The service type for which to retrieve an instance.
     * @return A service of the type defined by {@code serviceType]
     */
    protected final <E extends Object> E getService(Class<E> serviceType){
	ServiceReference<E> ref = getServiceReference(serviceType);
	if(ref == null){
	    ref = context.getServiceReference(serviceType);
	    references.put(serviceType, ref);
	}
	return context.getService(ref);
    }
    
    private <E extends Object> ServiceReference<E> getServiceReference(Class<E> clazz){
	ServiceReference ref = references.get(clazz);
	if(ref != null){
	    return (ServiceReference<E>)ref;
	}
	return null;
    }
    
    private void closeAll(){
	for(ServiceReference ref : references.values()){
	    try{
		if(ref != null){
		    context.ungetService(ref);
		}
	    }catch(Exception ex){
		LOGGER.debug("Could not close service reference "+ref+".", ex);
		//Nothing more we can do, continue to next reference
	    }
	}
    }
    
}
