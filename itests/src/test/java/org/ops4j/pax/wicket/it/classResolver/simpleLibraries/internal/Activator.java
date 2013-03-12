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
package org.ops4j.pax.wicket.it.classResolver.simpleLibraries.internal;

import org.ops4j.pax.wicket.spi.support.BundleClassResolverHelper;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

/**
 * @author edward.yakop@gmail.com
 */
public final class Activator implements BundleActivator {

    private BundleClassResolverHelper helper;

    public final void start(BundleContext context) throws Exception {
        helper = new BundleClassResolverHelper(context);
        helper.setServicePid("libraryPid");
        helper.register();
    }

    public final void stop(BundleContext context)
        throws Exception {
        helper.dispose();
        helper = null;
    }
}
