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
package org.ops4j.pax.wicket.internal.injection.spring;

import java.util.List;
import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.Model;
import org.ops4j.pax.wicket.api.ComponentContentSource;
import org.ops4j.pax.wicket.api.TabContentSource;
import org.ops4j.pax.wicket.internal.BundleAnalysingComponentInstantiationListener;
import org.ops4j.pax.wicket.internal.ComponentProxy;
import org.ops4j.pax.wicket.util.AbstractContentSource;
import org.osgi.framework.BundleContext;

public class ContentSourceFactory extends AbstractContentSource implements TabContentSource<ITab>,
        ComponentContentSource<Component> {

    private Class<?> contentSourceClass;
    private List<String> destinations;
    private BundleContext bundleContext;
    private Map<String, String> overwrites;

    public ContentSourceFactory(BundleContext bundleContext, Map<String, String> overwrites, String wicketId,
            String applicationName, Class<?> contentSourceClass, List<String> destinations)
        throws IllegalArgumentException {
        super(bundleContext, wicketId, applicationName);
        this.overwrites = overwrites;
        this.contentSourceClass = contentSourceClass;
        this.bundleContext = bundleContext;
        this.destinations = destinations;
    }

    public void start() {
        if (destinations != null && destinations.size() != 0) {
            super.setDestination(destinations.toArray(new String[0]));
        }
        super.register();
    }

    public void stop() {
        super.dispose();
    }

    public void setDestinations(List<String> destinations) {
        this.destinations = destinations;
    }

    public Component createSourceComponent(String wicketId) {
        ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(contentSourceClass.getClassLoader());
            if (overwrites != null && overwrites.size() != 0) {
                Enhancer e = new Enhancer();
                e.setSuperclass(contentSourceClass);
                e.setCallback(new ComponentProxy(overwrites));
                return (Component) e.create(new Class[]{ String.class }, new Object[]{ wicketId });
            }
            return (Component) contentSourceClass.getConstructor(String.class).newInstance(wicketId);
        } catch (Exception e) {
            throw new RuntimeException("bumm", e);
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassloader);
        }
    }

    public ITab createSourceTab() {
        BundleAnalysingComponentInstantiationListener componentInstantiationListener =
            new BundleAnalysingComponentInstantiationListener(bundleContext, getApplicationName());
        ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(contentSourceClass.getClassLoader());
            ITab tab = null;
            if (overwrites != null && overwrites.size() != 0) {
                Enhancer e = new Enhancer();
                e.setSuperclass(contentSourceClass);
                e.setCallback(new ComponentProxy(overwrites));
                tab = (ITab) e.create();
            } else {
                tab = (ITab) contentSourceClass.newInstance();
            }
            componentInstantiationListener.inject(tab);
            return tab;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bumm");
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassloader);
        }
    }

    public ITab createSourceTab(String title) {
        BundleAnalysingComponentInstantiationListener componentInstantiationListener =
            new BundleAnalysingComponentInstantiationListener(bundleContext, getApplicationName());
        ClassLoader originalClassloader = Thread.currentThread().getContextClassLoader();
        try {
            Thread.currentThread().setContextClassLoader(contentSourceClass.getClassLoader());
            ITab tab = null;
            if (overwrites != null && overwrites.size() != 0) {
                Enhancer e = new Enhancer();
                e.setSuperclass(contentSourceClass);
                e.setCallback(new ComponentProxy(overwrites));
                tab = (ITab) e.create(new Class[]{ Model.class }, new Object[]{ new Model<String>(title) });
            } else {
                tab = (ITab) contentSourceClass.getConstructor(Model.class).newInstance(new Model<String>(title));
            }
            componentInstantiationListener.inject(tab);
            return tab;
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("bumm");
        } finally {
            Thread.currentThread().setContextClassLoader(originalClassloader);
        }
    }

}
