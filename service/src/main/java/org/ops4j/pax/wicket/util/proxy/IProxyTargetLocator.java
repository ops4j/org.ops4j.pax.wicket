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
package org.ops4j.pax.wicket.util.proxy;

import org.apache.wicket.util.io.IClusterable;
import org.ops4j.pax.wicket.api.NoBeanAvailableForInjectionException;

/**
 * Represents a service locator for lazy init proxies. When the first method invocation occurs on the lazy init proxy
 * this locator will be used to retrieve the proxy target object that will receive the method invocation.
 * <p/>
 * Generally implementations should be small when serialized because the main purpose of lazy init proxies is to be
 * stored in session when the wicket pages are serialized, and when deserialized to be able to lookup the dependency
 * again. The smaller the implementation of IProxyTargetLocator the less the drain on session size.
 * <p/>
 * A small implementation may use a static lookup to retrieve the target object.
 * <p/>
 * Example:
 * <p/>
 * 
 * <pre>
 * class UserServiceLocator implements IProxyTargetLocator {
 *     Object locateProxyObject() {
 *         MyApplication app = (MyApplication) Application.get();
 *         return app.getUserService();
 *     }
 * }
 * </pre>
 * 
 * @author Igor Vaynberg (ivaynberg)
 * @see LazyInitProxyFactory#createProxy(Class, IProxyTargetLocator)
 */
public interface IProxyTargetLocator extends IClusterable {
    /**
     * Returns the object that will be used as target object for a lazy init proxy.
     * 
     * @return retrieved object, or instance of {@link ReleasableProxyTarget}
     */
    Object locateProxyTarget();

    /**
     * Interface for return values of {@link IProxyTargetLocator#locateProxyTarget()}.
     * 
     * @author Christoph Läubrich
     * 
     */
    public interface ReleasableProxyTarget {

        /**
         * @return the final target
         */
        public Object getTarget() throws NoBeanAvailableForInjectionException;

        /**
         * invoked when the target is released. <b>Implementation note:</b> This Method should never throw an
         * RuntimeException!
         * 
         * @return the new target (might be a this pointer) or null if the target is no longer usable
         */
        public Object releaseTarget();
    }

    /*
     * Returns the parent holding the responsibility for the serialisation.
     */
    Class<?> getParent();
}
