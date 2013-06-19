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

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;

import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

/**
 * Backing {@link Iterable} for supporting the Collection types for injection. Since the OSGi registry is very dynmic
 * this has the following implications:
 * <ul>
 * <li>As soon as {@link #iterator()} is called a <b>Snapshot</b> of the current {@link ServiceReference}s that match
 * the type and filter are taken
 * <li>This will not update unless another call to {@link #iterator()} is performed</li>
 * <li>calls to the {@link Iterator#next()} method try to fetch the service at call time, this miht result in return an
 * Object that is <code>null</code> (the service might has vanished between call and snapshot time)!
 * <li>As soon as the service is successfully fetched, its is freed, that means the use count is decremented, so users
 * should take care to not keep reference longer than needed to prevent stale references</li>
 * <li>Services that perform cleanup when the usecount reaches zero might behave unespected</li>
 * <li>In case of DeclarativeServices it should be keept in mind that components gets activated/deactivated if the have
 * not set immediate=true and no one other is using the service (this is a concrete case for the above point)</li>
 * <li>calls to {@link Iterator#remove()} will always throw {@link UnsupportedOperationException}</li>
 * <li><strong>All in one</strong>: Handle this with care and keep the implications in mind!</li>
 * </ul>
 * 
 */
public class ServiceReferenceIterable<T> implements Iterable<T>, Serializable {

    private static final long serialVersionUID = -5424358280437237751L;
    private final BundleContext bundleContext;
    private final String filter;
    private final Class<T> type;

    public ServiceReferenceIterable(Class<T> type, String filter, BundleContext bundleContext) {
        this.type = type;
        this.filter = filter;
        this.bundleContext = bundleContext;
    }

    public Iterator<T> iterator() {
        Collection<ServiceReference<T>> fetchReferences = fetchReferences();
        return new ServiceReferenceIterator<T>(fetchReferences.iterator(), bundleContext);
    }

    private Collection<ServiceReference<T>> fetchReferences() {
        try {
            return bundleContext.getServiceReferences(type, filter);
        } catch (InvalidSyntaxException e) {
            throw new IllegalArgumentException("the provided filterstring is invalid", e);
        }
    }

    public int getCurrentSize() {
        return fetchReferences().size();
    }

    /**
     * Actual {@link Iterator} returned by this {@link Iterable} on each call to {@link Iterable#iterator()}
     * 
     * @param <E>
     */
    private static class ServiceReferenceIterator<E> implements Iterator<E> {

        private final Iterator<ServiceReference<E>> iterator;
        private final BundleContext bundleContext;

        /**
         * @param iterator
         * @param bundleContext
         */
        public ServiceReferenceIterator(Iterator<ServiceReference<E>> iterator, BundleContext bundleContext) {
            this.iterator = iterator;
            this.bundleContext = bundleContext;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public E next() {
            // Fetch the reference
            ServiceReference<E> next = iterator.next();
            E service = bundleContext.getService(next);
            if (service != null) {
                // and unget it now, so we do not leak...
                // this is not good but we have no better way to do it here...
                bundleContext.ungetService(next);
            }
            return service;
        }

        public void remove() {
            throw new UnsupportedOperationException("can't remove elements from this");
        }

    }

}
