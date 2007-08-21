/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2007 David Leangen
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
package org.ops4j.pax.wicket.toolkit.actions;

import org.apache.wicket.PageParameters;

public class PaxWicketBookmarkableLink
{
    private Class m_pageClass;
    private PageParameters m_parameters;

    public PaxWicketBookmarkableLink( Class pageClass, PageParameters parameters )
    {
        m_pageClass = pageClass;
        m_parameters = parameters;
    }

    public Class getPageClass()
    {
        return m_pageClass;
    }

    public PageParameters getParameters()
    {
        return m_parameters;
    }
}
