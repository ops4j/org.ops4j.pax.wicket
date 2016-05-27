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

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * This implements the Collectiontypes we support based on a backing {@link org.ops4j.pax.wicket.internal.injection.registry.ServiceReferenceIterable}. Care should be
 * taken, because of the dynamic nature of the OSGi Framework all operations must work on a snapshot at call time, this
 * can have a large impact on performance if not used whisely.
 *
 * @author nmw
 * @version $Id: $Id
 */
public class ServiceReferenceCollection<E> extends AbstractList<E> implements Set<E>, List<E> {

    private final ServiceReferenceIterable<E> iterable;

    /**
     * <p>Constructor for ServiceReferenceCollection.</p>
     *
     * @param iterable a {@link org.ops4j.pax.wicket.internal.injection.registry.ServiceReferenceIterable} object.
     */
    public ServiceReferenceCollection(ServiceReferenceIterable<E> iterable) {
        this.iterable = iterable;
    }

    /** {@inheritDoc} */
    @Override
    public Iterator<E> iterator() {
        return iterable.iterator();
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        return iterable.getCurrentSize();
    }

    /** {@inheritDoc} */
    @Override
    public E get(int index) {
        return new ArrayList<E>(this).get(index);
    }

}
