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
package org.ops4j.pax.wicket.internal.injection;

import java.util.Comparator;
import java.util.List;

import org.ops4j.pax.wicket.api.ContentAggregator;
import org.ops4j.pax.wicket.api.ContentSource;
import org.ops4j.pax.wicket.util.RootContentAggregator;
import org.osgi.framework.BundleContext;

public class RootContentAggregatorDecorator implements InjectionAwareDecorator, ContentAggregator {

    private String applicationName;
    private String aggregationPointName;
    private BundleContext bundleContext;
    private RootContentAggregator rootContentAggregator;

    public RootContentAggregatorDecorator() {
    }

    public String getAggregationPointName() {
        return rootContentAggregator.getAggregationPointName();
    }

    public String getApplicationName() {
        return rootContentAggregator.getApplicationName();
    }

    public boolean isEmpty() {
        return rootContentAggregator.isEmpty();
    }

    public List<String> getRegisteredSourceIds(String groupId) {
        return rootContentAggregator.getRegisteredSourceIds(groupId);
    }

    public List<String> getRegisteredSourceIds(String groupId, Comparator<ContentSource> comparator) {
        return rootContentAggregator.getRegisteredSourceIds(groupId, comparator);
    }

    public <ContentSourceType extends ContentSource> List<ContentSourceType> getEntireAggregationPointContent() {
        return rootContentAggregator.getEntireAggregationPointContent();
    }

    public <ContentSourceType extends ContentSource> List<ContentSourceType> getContentByGroupId(String goupId) {
        return rootContentAggregator.getContentByGroupId(goupId);
    }

    public <ContentSourceType extends ContentSource> ContentSourceType getContentBySourceId(String sourceId) {
        return rootContentAggregator.getContentBySourceId(sourceId);
    }

    public void dispose() {
        rootContentAggregator.dispose();
    }

    public void start() throws Exception {
        rootContentAggregator = new RootContentAggregator(bundleContext, applicationName, aggregationPointName);
        rootContentAggregator.register();
    }

    public void stop() throws Exception {
        rootContentAggregator.dispose();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setAggregationPointName(String aggregationPointName) {
        this.aggregationPointName = aggregationPointName;
    }

}
