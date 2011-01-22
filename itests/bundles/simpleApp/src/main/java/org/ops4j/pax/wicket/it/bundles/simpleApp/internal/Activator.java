/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.it.bundles.simpleApp.internal;

import org.ops4j.pax.wicket.api.PaxWicketApplicationFactory;
import static org.ops4j.pax.wicket.it.bundles.simpleApp.SimpleAppConstants.APPLICATION_NAME;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

/**
 * @author edward.yakop@gmail.com
 */
public final class Activator
    implements BundleActivator
{

    private PaxWicketApplicationFactory m_factory;
    private ServiceRegistration m_registration;

    public final void start( BundleContext context )
        throws Exception
    {
        m_factory = new PaxWicketApplicationFactory( context, HomePage.class, "test", APPLICATION_NAME );
        m_registration = m_factory.register();
    }

    public final void stop( BundleContext context )
        throws Exception
    {
        m_registration.unregister();
        m_registration = null;

        m_factory.dispose();
        m_factory = null;
    }
}
