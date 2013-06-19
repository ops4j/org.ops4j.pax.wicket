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
/**
 * 
 */
package org.ops4j.pax.wicket.samples.ds.internal;

import org.ops4j.pax.wicket.api.WebApplicationFactory;

/**
 * @author Christoph Läubrich
 */
public class WicketWebApplicationFactory implements WebApplicationFactory<WicketApplication> {
	
	{
		System.err.println("### INIT " + WicketWebApplicationFactory.class.getName());
	}

    /* (non-Javadoc)
     * @see org.ops4j.pax.wicket.api.WebApplicationFactory#getWebApplicationClass()
     */
    public Class<WicketApplication> getWebApplicationClass() {
        return WicketApplication.class;
    }

    /* (non-Javadoc)
     * @see org.ops4j.pax.wicket.api.WebApplicationFactory#onInstantiation(org.apache.wicket.protocol.http.WebApplication)
     */
    public void onInstantiation(WicketApplication application) {
        //Nothing to do here...
		System.err.println("### INIT " + application );
    }

}
