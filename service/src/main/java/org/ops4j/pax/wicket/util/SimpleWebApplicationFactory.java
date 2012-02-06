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
package org.ops4j.pax.wicket.util;

import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;

/**
 * Most simple {@link IWebApplicationFactory} which is expected to be used by blueprint or spring to register an wicket
 * webapplication for pax wicket. You only have to set a {@link WebApplication} via the setter or the constructur and
 * register it as a service. Please keep in mind that you have to set at least the the "pax.wicket.mountpoint" and
 * "pax.wicket.applicationname" properties to your service to be started in pax-wicket.
 * 
 * This application does simply create a new class of your {@link WebApplication} each time requested. Please be aware
 * that the {@link WebApplication}, as well as your homepage class both have to be reachable via the same classloader
 * you expose this class!
 * 
 */
public class SimpleWebApplicationFactory implements IWebApplicationFactory {

    private Class<? extends WebApplication> wicketApplication;

    public SimpleWebApplicationFactory() {
    }

    public SimpleWebApplicationFactory(Class<? extends WebApplication> wicketApplication) {
        this.wicketApplication = wicketApplication;
    }

    public WebApplication createApplication(WicketFilter filter) {
        try {
            return wicketApplication.newInstance();
        } catch (InstantiationException e) {
            throw new IllegalStateException(
                "Wicket WebApplication has to have a default constructure to be used in the SimpleWebApplicationFactory");
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(
                "WicketWebApplication has to be accessible by the SimpleWebApplicationFactory to be used in the SimpleWebApplicationFactory");
        }
    }

    public void setWicketApplication(Class<? extends WebApplication> wicketApplication) {
        this.wicketApplication = wicketApplication;
    }

    public void destroy(WicketFilter filter) {
    }

}
