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
package org.ops4j.pax.wicket.samples.departmentstore.view.franchisee.internal;

import java.util.Locale;

import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;
import org.ops4j.pax.wicket.service.DefaultContent;
import org.osgi.framework.BundleContext;
import wicket.Component;

/**
 * {@code FranchiseeContent}
 *
 * @author Edward Yakop
 * @since 1.0.0
 */
public class FranchiseeContent extends DefaultContent
{

    private Franchisee m_franchisee;

    public FranchiseeContent( BundleContext context, Franchisee franchisee, String applicationName )
    {
        super( context, franchisee.getName(), applicationName );
        m_franchisee = franchisee;
    }

    protected Component createComponent( String id, Locale locale )
    {
        return new FranchiseePanel( id, m_franchisee );
    }
}
