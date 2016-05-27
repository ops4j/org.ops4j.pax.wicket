
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.internal;

import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;

import org.ops4j.pax.wicket.api.ConfigurableFilterConfig;
public class DefaultConfigurableFilterConfig implements ConfigurableFilterConfig {

    private String filterName;
    private final ServletConfig servletConfig;
    private Map<String, String> additionalConfigurations = new HashMap<String, String>();

    /**
     * <p>Constructor for DefaultConfigurableFilterConfig.</p>
     *
     * @param servletConfig a {@link javax.servlet.ServletConfig} object.
     */
    public DefaultConfigurableFilterConfig(ServletConfig servletConfig) {
        this(null, servletConfig);
    }

    /**
     * <p>Constructor for DefaultConfigurableFilterConfig.</p>
     *
     * @param filterName a {@link java.lang.String} object.
     * @param servlet a {@link javax.servlet.ServletConfig} object.
     */
    public DefaultConfigurableFilterConfig(String filterName, ServletConfig servlet) {
        this.filterName = filterName;
        servletConfig = servlet;
    }

    /**
     * <p>Getter for the field <code>filterName</code>.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getFilterName() {
        return filterName;
    }

    /** {@inheritDoc} */
    public String getInitParameter(String paramName) {
        if (additionalConfigurations.containsKey(paramName)) {
            return additionalConfigurations.get(paramName);
        }
        return servletConfig.getInitParameter(paramName);
    }

    /**
     * <p>getInitParameterNames.</p>
     *
     * @return a {@link java.util.Enumeration} object.
     */
    @SuppressWarnings("unchecked")
    public Enumeration<String> getInitParameterNames() {
        List<String> initParameterNames = Collections.list(servletConfig.getInitParameterNames());
        initParameterNames.addAll(additionalConfigurations.keySet());
        return Collections.enumeration(initParameterNames);
    }

    /**
     * <p>getServletContext.</p>
     *
     * @return a {@link javax.servlet.ServletContext} object.
     */
    public ServletContext getServletContext() {
        return servletConfig.getServletContext();
    }

    /** {@inheritDoc} */
    public void setFilterName(String filterName) {
        this.filterName = filterName;
    }

    /** {@inheritDoc} */
    public void putInitParameter(String name, String parameter) {
        additionalConfigurations.put(name, parameter);
    }

    /** {@inheritDoc} */
    public void putAllInitParameter(Map<String, String> parameterMap) {
        additionalConfigurations.putAll(parameterMap);
    }

}
