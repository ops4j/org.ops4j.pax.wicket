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
package org.ops4j.pax.wicket.spi;

import org.apache.wicket.util.io.IClusterable;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;

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
 * @see LazyInitProxyFactory#createProxy(Class, ProxyTargetLocator)
 */
// FIXME: is it necessary to extend IClusterable here??
public interface ProxyTargetLocator extends IClusterable {
    /**
     * Returns the object that will be used as target object for a lazy init proxy.
     * 
     * @return the {@link ProxyTarget} located or <code>null</code> if no proxy could be found
     * @throws IllegalStateException
     */
    ProxyTarget locateProxyTarget() throws IllegalStateException;

    /**
     * @return the parent holding the responsibility for the serialisation.
     */
    Class<?> getParent();

}
