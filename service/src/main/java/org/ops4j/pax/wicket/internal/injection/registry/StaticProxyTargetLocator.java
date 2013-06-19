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
package org.ops4j.pax.wicket.internal.injection.registry;

import org.ops4j.pax.wicket.spi.ProxyTarget;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;

/**
 * A {@link ProxyTargetLocator} that always returns the same {@link ProxyTarget} that always returns a given Object
 * 
 */
public class StaticProxyTargetLocator implements ProxyTargetLocator {

    private static final long serialVersionUID = 1162932281104011635L;
    private final Class<?> parent;
    private final StaticProxyTarget staticProxyTarget;

    /**
     * @param target the target to always return
     * @param parent the parent for classloading
     */
    public StaticProxyTargetLocator(Object target, Class<?> parent) {
        this.parent = parent;
        staticProxyTarget = new StaticProxyTarget(target);
    }

    public ProxyTarget locateProxyTarget() {
        return staticProxyTarget;
    }

    public Class<?> getParent() {
        return parent;
    }

    private static class StaticProxyTarget implements ProxyTarget {

        private final Object target;

        private StaticProxyTarget(Object target) {
            this.target = target;
        }

        public Object getTarget() {
            return target;
        }

    }

}
