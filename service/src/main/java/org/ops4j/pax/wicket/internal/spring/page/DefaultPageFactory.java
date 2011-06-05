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

package org.ops4j.pax.wicket.internal.spring.page;

import java.lang.reflect.Constructor;
import java.util.Map;

import org.apache.wicket.Application;
import org.apache.wicket.Page;
import org.apache.wicket.PageParameters;
import org.apache.wicket.markup.html.WebPage;
import org.ops4j.pax.wicket.internal.spring.util.ComponentInstantiationListener;
import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;

@SuppressWarnings("rawtypes")
public class DefaultPageFactory extends AbstractPageFactory {

    private Class<? extends Page> pageClass;
    private Map<String, String> overwrite;
    private BundleContext bundleContext;

    public void setOverwrite(Map<String, String> overwrite) {
        this.overwrite = overwrite;
    }

    public DefaultPageFactory(BundleContext bundleContext, String pageId, String applicationName, String pageName,
            Class<? extends WebPage> pageClass)
        throws IllegalArgumentException {
        super(bundleContext, pageId, applicationName, pageName, pageClass);
        this.pageClass = pageClass;
        this.bundleContext = bundleContext;
    }

    public Page createPage(PageParameters params) {
        ComponentInstantiationListener componentInstantiationListener =
            new ComponentInstantiationListener(overwrite, pageClass, bundleContext);
        try {
            Application.get().addComponentInstantiationListener(componentInstantiationListener);
            if (params != null && !params.isEmpty()) {
                try {
                    Constructor<? extends Page> constructor = pageClass.getConstructor(PageParameters.class);
                    return constructor.newInstance(params);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
                }
            }
            try {
                return pageClass.newInstance();
            } catch (Exception e) {
                throw new RuntimeException(String.format("Creation of %s not possible", pageClass.getName()), e);
            }
        } finally {
            Application.get().removeComponentInstantiationListener(componentInstantiationListener);
        }
    }

    public Class<? extends Page> getPageClass() {
        return pageClass;
    }

}
