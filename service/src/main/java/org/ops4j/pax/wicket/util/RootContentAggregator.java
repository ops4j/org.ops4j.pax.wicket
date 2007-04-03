/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2006 Edward F. Yakop
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.util;

import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.internal.BaseAggregator;
import org.osgi.framework.BundleContext;
import org.osgi.service.cm.ManagedService;

public class RootContentAggregator extends BaseAggregator
{

    /**
     * @param bundleContext        The client bundle context.
     * @param applicationName      The name of the application this RootContentAggregator belongs to.
     * @param aggregationPointName The name of the AggregationPoint handled by this ContentAggregator.
     */
    public RootContentAggregator( BundleContext bundleContext, String applicationName, String aggregationPointName
    )
    {
        super( bundleContext, applicationName, aggregationPointName );
    }

    protected String[] getServiceNames()
    {
        return new String[]
            {
                ContentAggregator.class.getName(), ManagedService.class.getName()
            };
    }
}
