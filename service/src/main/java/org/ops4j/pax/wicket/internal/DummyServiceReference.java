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
package org.ops4j.pax.wicket.internal;

import org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceReference;

/**
 * This class is nothing more than a dummy class which is useful in case you need an empty, additional instance of a
 * service reference not coming from OSGi but still uniquely identifiable. The typical use case for this class is
 * available in the {@link org.ops4j.pax.wicket.internal.BundleDelegatingClassResolver} or {@link org.ops4j.pax.wicket.internal.injection.BundleDelegatingComponentInstanciationListener}.
 *
 * @author nmw
 * @version $Id: $Id
 */
public class DummyServiceReference<T> implements ServiceReference<T> {

    /** {@inheritDoc} */
    @Override
    public Object getProperty(String key) {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public String[] getPropertyKeys() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Bundle getBundle() {
        return null;
    }

    /** {@inheritDoc} */
    @Override
    public Bundle[] getUsingBundles() {
        return null;
    }

    /** {@inheritDoc} */
    public boolean isAssignableTo(Bundle bundle, String className) {
        return false;
    }

    /** {@inheritDoc} */
    @Override
    public int compareTo(Object reference) {
        return 0;
    }

}
