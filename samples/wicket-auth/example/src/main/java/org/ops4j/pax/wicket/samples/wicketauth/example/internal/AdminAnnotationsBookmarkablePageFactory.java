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
package org.ops4j.pax.wicket.samples.wicketauth.example.internal;

import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

import org.ops4j.pax.wicket.samples.wicketauth.example.AdminAnnotationsBookmarkablePage;

import wicket.PageParameters;

public class AdminAnnotationsBookmarkablePageFactory
        extends AbstractPageFactory<AdminAnnotationsBookmarkablePage>
{
    public static final String PAGE_NAME = "home";

    public AdminAnnotationsBookmarkablePageFactory( 
            final BundleContext bundleContext, 
            final String applicationName )
            throws IllegalArgumentException
    {
        super( bundleContext, PAGE_NAME, applicationName, PAGE_NAME );
    }

    public AdminAnnotationsBookmarkablePage createPage( final PageParameters pageParameters )
    {
        return new AdminAnnotationsBookmarkablePage( pageParameters );
    }

    public Class<AdminAnnotationsBookmarkablePage> getPageClass()
    {
        return AdminAnnotationsBookmarkablePage.class;
    }
}
