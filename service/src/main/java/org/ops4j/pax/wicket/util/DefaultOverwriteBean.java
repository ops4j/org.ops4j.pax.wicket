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
package org.ops4j.pax.wicket.util;

import java.util.HashMap;
import java.util.Map;

import org.ops4j.pax.wicket.api.OverwriteBean;

public class DefaultOverwriteBean implements OverwriteBean {

    private String applicationName;
    private Map<String, String> beanNameMapping;

    public DefaultOverwriteBean(String applicationName) {
        this.applicationName = applicationName;
        beanNameMapping = new HashMap<String, String>();
    }

    public DefaultOverwriteBean(String applicationName, Map<String, String> beanNameMapping) {
        this.applicationName = applicationName;
        this.beanNameMapping = beanNameMapping;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public Map<String, String> getBeanNameMapping() {
        return beanNameMapping;
    }

    public void addBeanNameMapping(String oldName, String newName) {
        beanNameMapping.put(oldName, newName);
    }

    public void addBeanNameMapingAll(Map<String, String> all) {
        beanNameMapping.putAll(all);
    }

}
