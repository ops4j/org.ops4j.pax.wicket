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

import org.apache.wicket.model.LoadableDetachableModel;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleReference;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This model makes it easier to work with OSGi services in Wicket. It is a LoadableDetachableModel that loads data via a Service accuired
 * from the Service Registry.
 *
 * @author Martin Nybo Nielsen
 * @param <T> The type of service to poll for populating the model.
 * @param <E> The returntype expected from the model.
 * @version $Id: $Id
 * @since 3.0.5
 */
public abstract class AbstractDetachableServiceModel<T extends Object, E extends Object> extends LoadableDetachableModel<E> {
    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractDetachableServiceModel.class);
    
    private final Class<T> serviceType;
    private transient BundleContext context;
	
    /**
     * Takes the type of the service to retrieve data from. When the {@code load} method is called, the first valid service of
     * {@code serviceType} will be polled for data. {@code owningBundleObject} is used to properly define the BundleContext which the
     * model should use for calling the service registry. Any object residing in the same bundle as the page where the model is used is recommended.
     *
     * @param serviceType The type of the service to retrieve model data from
     * @param owningBundleObject Any object which resides in the bundle which uses this model.
     */
    public AbstractDetachableServiceModel(Class<T> serviceType, Object owningBundleObject) {
	this(serviceType, owningBundleObject.getClass());
    }
    
    /**
     * Takes the type of the service to retrieve data from. When the {@code load} method is called, the first valid service of
     * {@code serviceType} will be polled for data. {@code owningBundleClass} is used to properly define the BundleContext which the
     * model should use for calling the service registry. Using the class containing the model is usually sufficient.
     *
     * @param serviceType The type of the service to retrieve model data from
     * @param owningBundleClass Any class which resides in the bundle which uses this model.
     */
    public AbstractDetachableServiceModel(Class<T> serviceType, Class owningBundleClass) {
	this.serviceType = serviceType;
        
        /* TODO reimplent, currently not working*/
//	if(!BundleReference.class.isAssignableFrom(owningBundleClass.getClassLoader().getClass())){
//	    throw new IllegalArgumentException("This model can only be used from within OSGi containers. The supplied class does not appear to originate in "
//		    + "a bundle "+owningBundleClass.getCanonicalName());
//	}
	
	if(!serviceType.isInterface()){
	    throw new IllegalArgumentException("The serviceType must be an interface, was: "+serviceType.getCanonicalName()+". Error occured in "+context.getBundle().getSymbolicName());
	}
    }
	
    /**
     * Implement this method to specify the data's transition from the Service to the model.
     *
     * @param source The object returned from the service registry. Due to the nature of osgi, calling this method may throw a RuntimeException, if the service
     * should, for example, disappear during invocation. If left uncaught (which is fine) it will result in the exception being logged, and the model
     * data being set to null.
     * @return The object the service should be set to.
     * @throws java.lang.Exception Any exception thrown from this method will be logged as an error, and null will be set as the model data.
     */
    protected abstract E doLoad(T source) throws Exception;

    /** {@inheritDoc} */
    @Override
    protected E load() {
	context = BundleReference.class.cast(serviceType.getClassLoader()).getBundle().getBundleContext();
	ServiceReference<T> ref = context.getServiceReference(serviceType);
	try{
	if(ref == null){
	    return null;
	}
	T service = context.getService(ref);
	return doLoad(service);
	
	}catch(Exception e){
	    LOGGER.error("Could not load object from service. Returning null. Service called: "+serviceType+" from bundle "+context.getBundle().getSymbolicName(), e);
	    return null;
	}finally{
	    if(ref != null)
	    context.ungetService(ref);
	}
	
    }
    
}
