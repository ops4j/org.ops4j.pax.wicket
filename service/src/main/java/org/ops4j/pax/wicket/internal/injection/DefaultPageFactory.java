/*
 * Copyright OPS4J
 *
 * Licensed  under the  Apache License,  Version 2.0  (the "License");
 * you may not use  this file  except in  compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed  under the  License is distributed on an "AS IS" BASIS,
 * WITHOUT  WARRANTIES OR CONDITIONS  OF ANY KIND, either  express  or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.ops4j.pax.wicket.internal.injection;

import java.util.Map;

import net.sf.cglib.proxy.Enhancer;

import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.ops4j.pax.wicket.internal.ComponentProxy;
import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

@SuppressWarnings("rawtypes")
public class DefaultPageFactory extends AbstractPageFactory {

    private Class<? extends Page> pageClass;
    private Map<String, String> overwrites;
    private BundleContext bundleContext;

    public DefaultPageFactory(BundleContext bundleContext, String pageId, String applicationName, String pageName,
            Class<? extends WebPage> pageClass, Map<String, String> overwrites)
        throws IllegalArgumentException {
        super(bundleContext, pageId, applicationName, pageName, pageClass);
        this.pageClass = pageClass;
        this.bundleContext = bundleContext;
        this.overwrites = overwrites;
    }

    public Page createPage(PageParameters params) {
        if (params != null && !params.isEmpty()) {
            try {
                if (overwrites != null && overwrites.size() != 0) {
                    Enhancer e = new Enhancer();
                    e.setSuperclass(pageClass);
                    e.setCallback(new ComponentProxy(overwrites));
                    return (Page) e.create(new Class[]{ PageParameters.class }, new Object[]{ params });
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
                return (Page) e.create();
            }
            return pageClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
        }
    }

    public Class<? extends Page> getPageClass() {
        return pageClass;
    }

}
