package org.ops4j.pax.wicket.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.pax.wicket.api.ContentSourceDescriptor;

public class DefaultContentSourceDescriptor implements ContentSourceDescriptor, Serializable {

    private static final long serialVersionUID = 1L;

    private Map<String, String> overwrites = new HashMap<String, String>();
    private List<String> destinations = new ArrayList<String>();

    private String wicketId;
    private String contentSourceId;
    private Class<?> componentClass;

    public DefaultContentSourceDescriptor(String wicketId, String contentSourceId, Class<?> componentClass) {
        this.wicketId = wicketId;
        this.contentSourceId = contentSourceId;
        this.componentClass = componentClass;
    }

    public void addDestination(String destination) {
        destinations.add(destination);
    }

    public void addOverwrite(String id, String beanId) {
        overwrites.put(id, beanId);
    }

    public Map<String, String> getOverwrites() {
        return overwrites;
    }

    public String getWicketId() {
        return wicketId;
    }

    public String getContentSourceId() {
        return contentSourceId;
    }

    public List<String> getDestinations() {
        return destinations;
    }

    public Class<?> getComponentClass() {
        return componentClass;
    }

}
