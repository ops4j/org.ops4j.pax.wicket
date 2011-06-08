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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.ops4j.pax.wicket.api.AggregationPointDescriptor;
import org.ops4j.pax.wicket.api.ContentSourceDescriptor;
import org.ops4j.pax.wicket.api.ContentSourceModelMapping;

public class DefaultContentSourceModelMapping implements ContentSourceModelMapping, Serializable {

    private static final long serialVersionUID = 1L;

    private List<ContentSourceDescriptor> contentSources = new ArrayList<ContentSourceDescriptor>();
    private List<AggregationPointDescriptor> aggregationPoints = new ArrayList<AggregationPointDescriptor>();
    private Map<String, Object> modelObjects = new HashMap<String, Object>();
    private Boolean allowMultibleRegistrations = false;

    public DefaultContentSourceModelMapping() {
    }

    public DefaultContentSourceModelMapping(boolean allowMultibleRegistrations) {
        this.allowMultibleRegistrations = allowMultibleRegistrations;
    }

    public void addContentSource(ContentSourceDescriptor contentSource) {
        contentSources.add(contentSource);
    }

    public void addAggregationPoint(AggregationPointDescriptor aggregationPoint) {
        aggregationPoints.add(aggregationPoint);
    }

    public void addModelObject(String beanId, Object model) {
        modelObjects.put(beanId, model);
    }

    public List<ContentSourceDescriptor> getContenSources() {
        return contentSources;
    }

    public List<AggregationPointDescriptor> getAggregationPoints() {
        return aggregationPoints;
    }

    public Map<String, Object> getModelObjects() {
        return modelObjects;
    }

    public boolean allowMultibleRegistrations() {
        return allowMultibleRegistrations;
    }

}
