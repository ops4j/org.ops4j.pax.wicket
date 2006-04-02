/*
 * Copyright 2005 Niclas Hedhman.
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
package org.ops4j.pax.wicket.sample.page1.internal;

import org.ops4j.pax.servicemanager.ServiceManager;
import wicket.markup.html.WebPage;

public class CalculatorPage extends WebPage
{

    private ServiceManager m_serviceManager;

    /**
     * Constructor which receives wrapped query string parameters for a request.
     * Having this constructor public means that you page is 'bookmarkable' and
     * hence can be called/ created from anywhere. For bookmarkable pages (as
     * opposed to when you construct page instances yourself, this constructor
     * will be used in preference to a no-arg constructor, if both exist. Note
     * that nothing is done with the page parameters argument. This constructor
     * is provided so that tools such as IDEs will include it their list of
     * suggested constructors for derived classes.
     *
     */
    public CalculatorPage( ServiceManager serviceManager )
    {
        m_serviceManager = serviceManager;
    }

}
