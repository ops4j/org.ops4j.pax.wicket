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
package org.ops4j.pax.wicket.samples.departmentstore.view.about.internal;

import static org.osgi.framework.Bundle.ACTIVE;
import static org.osgi.framework.Bundle.RESOLVED;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.border.BoxBorder;
import org.apache.wicket.markup.repeater.RepeatingView;
import org.apache.wicket.model.Model;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.samples.departmentstore.model.DepartmentStore;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;

public class AboutPage extends WebPage {

    private static final long serialVersionUID = 1L;

    @PaxWicketBean(name = "bundleContext")
    private BundleContext bundleContext;
    @PaxWicketBean(name = "departmentStoreService")
    private DepartmentStore departmentStore;

    public AboutPage() {
        super();
        createAboutPage(departmentStore, bundleContext);
    }

    public AboutPage(DepartmentStore store, BundleContext context) {
        super();
        createAboutPage(store, context);
    }

    private void createAboutPage(DepartmentStore store, BundleContext context) {
        Label storeLabel = new Label("storeName", store.getName());
        add(storeLabel);

        BoxBorder border = new BoxBorder("border");
        MultiLineLabel multiline = new MultiLineLabel("history", store.getHistory());
        border.add(multiline);
        add(border);

        WebMarkupContainer container = new WebMarkupContainer("container");
        add(container);
        container.setOutputMarkupId(true);

        Model labelModel = new Model("All bundles");
        container.add(new Label("displayTitle", labelModel));
        Model bundleViewModel = new Model(ACTIVE);
        container.add(new BundlesRepeatingView("bundles", bundleViewModel, context));

        add(new DisplayBundleList(
            "displayAllBundles", container, bundleViewModel, null, "All bundles", labelModel));
        add(new DisplayBundleList(
            "displayActiveBundles", container, bundleViewModel, ACTIVE, "Active bundles", labelModel));
        add(new DisplayBundleList(
            "displayResolvedBundles", container, bundleViewModel, RESOLVED, "Resolved bundles", labelModel));
    }

    private static class BundlesRepeatingView
            extends RepeatingView {

        private static final long serialVersionUID = 1L;

        private final BundleContext m_context;

        private BundlesRepeatingView(String wicketId, Model bundleStateToDisplay, BundleContext context) {
            super(wicketId, bundleStateToDisplay);
            m_context = context;
        }

        @Override
        protected final void onPopulate() {
            removeAll();

            Integer bundleState = (Integer) getDefaultModelObject();

            Bundle[] bundles = m_context.getBundles();
            for (Bundle bundle : bundles) {
                int state = bundle.getState();
                if (bundleState == null || bundleState.equals(state)) {
                    String symbolicName = bundle.getSymbolicName();
                    add(new Label(newChildId(), symbolicName));
                }
            }
        }
    }

    private static class DisplayBundleList extends AjaxLink {

        private static final long serialVersionUID = 1L;

        private final WebMarkupContainer m_containerToRefresh;
        private final Model m_bundleViewModel;
        private final Integer m_bundleStateToDisplay;

        private final String m_labelToDisplay;
        private final Model m_labelModel;

        private DisplayBundleList(
                String wicketId, WebMarkupContainer container, Model bundleViewModel, Integer bundleState,
                String labelToDisplay, Model labelModel) {
            super(wicketId);
            m_containerToRefresh = container;
            m_bundleViewModel = bundleViewModel;
            m_bundleStateToDisplay = bundleState;
            m_labelToDisplay = labelToDisplay;
            m_labelModel = labelModel;
        }

        @Override
        public final void onClick(AjaxRequestTarget target) {
            m_labelModel.setObject(m_labelToDisplay);
            m_bundleViewModel.setObject(m_bundleStateToDisplay);
            target.addComponent(m_containerToRefresh);
        }
    }
}
