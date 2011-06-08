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
package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.markup.html.panel.Panel;
import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;

public class XmlBasedFloorPanel extends Panel {
    private static final long serialVersionUID = 1L;

    public static final String WICKET_ID_NAME_LABEL = "name";
    public static final String WICKET_ID_FRANCHISEE = "franchisee";
    public static final String WICKET_ID_FRANCHISEES = "franchisees";

    @PaxWicketBean(name = XmlBasedModelMappingFactory.REFERENCE_MODEL)
    private Floor floor;
    @PaxWicketBean(name = XmlBasedModelMappingFactory.REFERENCE_AGGREGATOR)
    private ContentAggregator sources;

    public XmlBasedFloorPanel(String wicketId) {
        super(wicketId);
        ListView<?> view;
        if (sources == null || sources.isEmpty()) {
            String message = "No Franchisees are renting on this floor.";
            view = new LabelListView(WICKET_ID_FRANCHISEES, Collections.singletonList(message));
        } else {
            List<Component> components = new ArrayList<Component>();
            for (ContentSource contentSource : sources.getContentByGroupId(WICKET_ID_FRANCHISEE)) {
                components.add(
                    ((ComponentContentSource) contentSource).createSourceComponent(WICKET_ID_FRANCHISEE));
            }
            view = new FloorListView(WICKET_ID_FRANCHISEES, components);
        }
        add(view);
    }

    private static final class LabelListView extends ListView<String> {

        private static final long serialVersionUID = 1L;

        private LabelListView(String wicketId, List<String> list) {
            super(wicketId, list);
        }

        @Override
        protected final void populateItem(ListItem<String> item) {
            String message = item.getModelObject();
            Label label = new Label(WICKET_ID_FRANCHISEE, message);
            item.add(label);
        }
    }

    private static class FloorListView extends ListView<Component> {

        private static final long serialVersionUID = 1L;

        private FloorListView(String wicketId, List<Component> sources) {
            super(wicketId, sources);
        }

        @Override
        protected void populateItem(ListItem<Component> item) {
            item.add(item.getModelObject());
        }
    }
}
