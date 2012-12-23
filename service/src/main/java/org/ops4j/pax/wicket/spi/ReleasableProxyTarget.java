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

/**
 * Interface for return values of {@link ProxyTargetLocator#locateProxyTarget()}.
 * 
 */
public interface ReleasableProxyTarget extends ProxyTarget {

    /**
     * invoked when the target is released. <b>Implementation note:</b> This Method should never throw an
     * RuntimeException!
     * 
     * @return the new target (might be a this pointer) or null if the target is no longer usable
     */
    public ProxyTarget releaseTarget();
}
