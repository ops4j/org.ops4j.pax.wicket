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

import java.io.Serializable;
import org.apache.wicket.extensions.ajax.markup.html.AjaxEditableLabel;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.ops4j.pax.wicket.samples.departmentstore.model.Franchisee;

/**
 * {@code FranchiseePanel}
 *
 * @since 1.0.0
 */
public class FranchiseePanel extends Panel
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private static final String WICKET_ID_NAME_LABEL = "name";
    private static final String WICKET_ID_DESC_LABEL = "description";

    public FranchiseePanel( String wicketId, Franchisee franchisee )
    {
        super( wicketId );

        AjaxEditableLabel nameLabel = new AjaxEditableLabel( WICKET_ID_NAME_LABEL, new PropertyModel( franchisee, "name") );
        add( nameLabel );

        AjaxEditableLabel descLabel = new AjaxEditableLabel( WICKET_ID_DESC_LABEL, new PropertyModel( franchisee, "description") );
        add( descLabel );
    }
}
