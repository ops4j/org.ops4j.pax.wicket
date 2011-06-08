package org.ops4j.pax.wicket.api;

import java.util.Map;

/**
 * One method to create overwrites is to register an OverwriteBean in the Spring application context. This one will be
 * looked up for every call
 */
public interface OverwriteBean {

    String getApplicationName();

    Map<String, String> getBeanNameMapping();

}
