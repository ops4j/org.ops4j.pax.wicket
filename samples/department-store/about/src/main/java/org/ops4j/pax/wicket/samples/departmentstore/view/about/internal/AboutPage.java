/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.wicket.samples.departmentstore.view.about.internal;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.border.BoxBorder;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class AboutPage extends WebPage
{

    private static final long serialVersionUID = 1L;

    public AboutPage( DepartmentStore store, BundleContext aContext )
    {
        super();

        Label storeLabel = new Label( "storeName", store.getName() );
        add( storeLabel );

        BoxBorder border = new BoxBorder( "border" );
        MultiLineLabel multiline = new MultiLineLabel( "history", store.getHistory() );
        border.add( multiline );
        add( border );

        WebMarkupContainer container = new WebMarkupContainer( "container" );
        add( container );
        container.setOutputMarkupId( true );
        container.add( new BundlesRepeatingView( "bundles", aContext ) );

        add( new RefreshBundlesList( "refreshBundleList", container ) );
    }

    private static class BundlesRepeatingView
        extends RepeatingView
    {

        private static final long serialVersionUID = 1L;

        private final BundleContext context;

        private BundlesRepeatingView( String aWicketId, BundleContext aContext )
        {
            super( aWicketId );
            context = aContext;
        }

        @Override
        protected final void onPopulate()
        {
            removeAll();

            Bundle[] bundles = context.getBundles();
            for( Bundle bundle : bundles )
            {
                int state = bundle.getState();
                if( Bundle.ACTIVE == state )
                {
                    String symbolicName = bundle.getSymbolicName();
                    add( new Label( newChildId(), symbolicName ) );
                }
            }
        }
    }

    private static class RefreshBundlesList extends AjaxLink
    {

        private static final long serialVersionUID = 1L;

        private final WebMarkupContainer containerToRefresh;

        private RefreshBundlesList( String aWicketId, WebMarkupContainer aContainer )
        {
            super( aWicketId );
            containerToRefresh = aContainer;
        }

        @Override
        public final void onClick( AjaxRequestTarget aTarget )
        {
            aTarget.addComponent( containerToRefresh );
        }
    }
}
