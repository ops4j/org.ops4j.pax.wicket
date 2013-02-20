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
