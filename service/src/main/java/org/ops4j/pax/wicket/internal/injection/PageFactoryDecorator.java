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

import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.internal.ComponentProxy;
import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

public class PageFactoryDecorator implements PageFactory<WebPage>, InjectionAwareDecorator {

    private String pageId;
    private String applicationName;
    private String pageName;
    private Class<WebPage> pageClass;
    private Map<String, String> overwrites;
    private BundleContext bundleContext;
    private InternalPageFactory internalPageFactory;

    public PageFactoryDecorator() {
    }

    public Class<WebPage> getPageClass() {
        return internalPageFactory.getPageClass();
    }

    public WebPage createPage(PageParameters params) {
        return internalPageFactory.createPage(params);
    }

    public void start() throws Exception {
        internalPageFactory =
            new InternalPageFactory(bundleContext, pageId, applicationName, pageName, pageClass, overwrites);
        internalPageFactory.register();
    }

    public void stop() throws Exception {
        internalPageFactory.dispose();
    }

    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setPageClass(Class<WebPage> pageClass) {
        this.pageClass = pageClass;
    }

    public void setOverwrites(Map<String, String> overwrites) {
        this.overwrites = overwrites;
    }

    private static class InternalPageFactory extends AbstractPageFactory<WebPage> {

        private Class<WebPage> pageClass;
        private Map<String, String> overwrites;

        public InternalPageFactory(BundleContext bundleContext, String pageId, String applicationName, String pageName,
                Class<WebPage> pageClass, Map<String, String> overwrites)
            throws IllegalArgumentException {
            super(bundleContext, pageId, applicationName, pageName, pageClass);
            this.pageClass = pageClass;
            this.overwrites = overwrites;
        }

        public WebPage createPage(PageParameters params) {
            if (params != null && !params.isEmpty()) {
                try {
                    if (overwrites != null && overwrites.size() != 0) {
                        Enhancer e = new Enhancer();
                        e.setSuperclass(pageClass);
                        e.setCallback(new ComponentProxy(overwrites));
                        return (WebPage) e.create(new Class[]{ PageParameters.class }, new Object[]{ params });
                    }
                    return pageClass.getConstructor(PageParameters.class).newInstance(params);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
                }
            }
            try {
                if (overwrites != null && overwrites.size() != 0) {
                    Enhancer e = new Enhancer();
                    e.setSuperclass(pageClass);
                    e.setCallback(new ComponentProxy(overwrites));
                    return (WebPage) e.create();
                }
                return pageClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
            }
        }

        public Class<WebPage> getPageClass() {
            return pageClass;
        }

    }

}
