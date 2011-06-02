package org.ops4j.pax.wicket.samples.departmentstore.view.floor.internal;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.ops4j.pax.wicket.samples.departmentstore.model.Floor;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;

public class XmlBasedFloorTab extends AbstractTab {

    private static final long serialVersionUID = 1L;

    @PaxWicketBean(name = XmlBasedModelMappingFactory.REFERENCE_SOURCE)
    private ComponentContentSource<Panel> floorPanel;
    @PaxWicketBean(name = XmlBasedModelMappingFactory.REFERENCE_MODEL)
    private Floor floor;

    public XmlBasedFloorTab() {
        this(new Model<String>(""));
    }

    public XmlBasedFloorTab(IModel<String> title) {
        super(title);
    }

    @Override
    public IModel<String> getTitle() {
        return new Model<String>(floor.getName());
    }

    @Override
    public Panel getPanel(String wicketId) {
        return floorPanel.createSourceComponent(wicketId);
    }

}
