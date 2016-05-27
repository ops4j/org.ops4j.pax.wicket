
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
package org.ops4j.pax.wicket.spi.support;

import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.ops4j.pax.wicket.api.PageFactory;
import org.ops4j.pax.wicket.api.support.AbstractPageFactory;
import org.osgi.framework.BundleContext;
public class PageFactoryDecorator implements PageFactory<WebPage>, InjectionAwareDecorator {

    private String pageId;
    private String applicationName;
    private String pageName;
    private Class<WebPage> pageClass;
    private Map<String, String> overwrites;
    private String injectionSource;
    private BundleContext bundleContext;
    private InternalPageFactory internalPageFactory;

    /**
     * <p>Constructor for PageFactoryDecorator.</p>
     */
    public PageFactoryDecorator() {
    }

    /**
     * <p>Getter for the field <code>pageClass</code>.</p>
     *
     * @return a {@link java.lang.Class} object.
     */
    public Class<WebPage> getPageClass() {
        return internalPageFactory.getPageClass();
    }

    /** {@inheritDoc} */
    public WebPage createPage(PageParameters params) {
        return internalPageFactory.createPage(params);
    }

    /**
     * <p>start.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void start() throws Exception {
        internalPageFactory =
            new InternalPageFactory(bundleContext, pageId, applicationName, pageName, pageClass, overwrites,
                injectionSource);
        internalPageFactory.register();
    }

    /**
     * <p>stop.</p>
     *
     * @throws java.lang.Exception if any.
     */
    public void stop() throws Exception {
        internalPageFactory.dispose();
    }

    /** {@inheritDoc} */
    public void setBundleContext(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    /**
     * <p>Setter for the field <code>pageId</code>.</p>
     *
     * @param pageId a {@link java.lang.String} object.
     */
    public void setPageId(String pageId) {
        this.pageId = pageId;
    }

    /**
     * <p>Setter for the field <code>applicationName</code>.</p>
     *
     * @param applicationName a {@link java.lang.String} object.
     */
    public void setApplicationName(String applicationName) {
        this.applicationName = applicationName;
    }

    /**
     * <p>Setter for the field <code>pageName</code>.</p>
     *
     * @param pageName a {@link java.lang.String} object.
     */
    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    /**
     * <p>Setter for the field <code>pageClass</code>.</p>
     *
     * @param pageClass a {@link java.lang.Class} object.
     */
    public void setPageClass(Class<WebPage> pageClass) {
        this.pageClass = pageClass;
    }

    /**
     * <p>Setter for the field <code>overwrites</code>.</p>
     *
     * @param overwrites a {@link java.util.Map} object.
     */
    public void setOverwrites(Map<String, String> overwrites) {
        this.overwrites = overwrites;
    }

    /**
     * <p>Setter for the field <code>injectionSource</code>.</p>
     *
     * @param injectionSource a {@link java.lang.String} object.
     */
    public void setInjectionSource(String injectionSource) {
        this.injectionSource = injectionSource;
    }

    private static class InternalPageFactory extends AbstractPageFactory<WebPage> {

        private final Class<WebPage> pageClass;
        private final Map<String, String> overwrites;
        private final String injectionSource;

        public InternalPageFactory(BundleContext bundleContext, String pageId, String applicationName, String pageName,
                Class<WebPage> pageClass, Map<String, String> overwrites, String injectionSource)
            throws IllegalArgumentException {
            super(bundleContext, pageId, applicationName, pageName, pageClass);
            this.pageClass = pageClass;
            this.overwrites = overwrites;
            this.injectionSource = injectionSource;
        }

        public WebPage createPage(PageParameters params) {
            if (params != null && !params.isEmpty()) {
                try {
                    Enhancer e = new Enhancer();
                    e.setClassLoader(PageFactoryDecorator.class.getClassLoader());
                    e.setSuperclass(pageClass);
                    e.setCallback(new ComponentProxy(injectionSource, overwrites));
                    return (WebPage) e.create(new Class[]{ PageParameters.class }, new Object[]{ params });
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
                }
            }
            try {
                Enhancer e = new Enhancer();
                e.setSuperclass(pageClass);
                e.setClassLoader(PageFactoryDecorator.class.getClassLoader());
                e.setCallback(new ComponentProxy(injectionSource, overwrites));
                return (WebPage) e.create();
            } catch (Exception e) {
                throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
            }
        }

        public Class<WebPage> getPageClass() {
            return pageClass;
        }

    }

}
