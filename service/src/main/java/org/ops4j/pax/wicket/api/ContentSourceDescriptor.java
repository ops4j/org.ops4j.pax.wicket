package org.ops4j.pax.wicket.api;

import java.util.List;
import java.util.Map;

public interface ContentSourceDescriptor {

    Map<String, String> getOverwrites();

    String getWicketId();

    String getContentSourceId();

    List<String> getDestinations();

    Class<?> getComponentClass();

}
