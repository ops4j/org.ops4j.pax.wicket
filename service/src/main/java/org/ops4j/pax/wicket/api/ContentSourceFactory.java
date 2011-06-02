package org.ops4j.pax.wicket.api;

public interface ContentSourceFactory<MappingType> {

    ContentSourceModelMapping createContentSourceMappings(MappingType service);

}
