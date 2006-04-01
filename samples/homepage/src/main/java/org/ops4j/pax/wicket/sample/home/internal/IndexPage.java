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
package org.ops4j.pax.wicket.sample.home.internal;

import org.ops4j.pax.wicket.WicketPage;
import org.ops4j.pax.wicket.WicketSession;
import org.ops4j.pax.wicket.PagePart;

public class IndexPage
    implements WicketPage
{

    public String getResourceBasePath()
    {
        return "resources";
    }

    public void setSession( WicketSession session )
    {
        //TODO: Auto-generated, need attention.

    }

    public WicketSession getSession()
    {
        //TODO: Auto-generated, need attention.
        return null;
    }

    public PagePart[] getPageParts()
    {
        //TODO: Auto-generated, need attention.
        return new PagePart[0];
    }

    public void addPagePart( PagePart part )
    {
        //TODO: Auto-generated, need attention.

    }

    public void removePagePart( PagePart part )
    {
        //TODO: Auto-generated, need attention.

    }
}
