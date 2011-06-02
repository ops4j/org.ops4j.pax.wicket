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
import org.ops4j.pax.wicket.internal.spring.util.ComponentInstantiationListener;
import org.ops4j.pax.wicket.util.AbstractPageFactory;
import org.osgi.framework.BundleContext;
import org.springframework.osgi.context.BundleContextAware;

@SuppressWarnings("rawtypes")
public class DefaultPageFactory extends AbstractPageFactory implements BundleContextAware {

    private Class<? extends Page> pageClass;
    private Map<String, String> overwrite;
    private BundleContext bundleContext;

    public void setPageClass(Class<? extends Page> pageClass) {
        this.pageClass = pageClass;
    }

    public void setOverwrite(Map<String, String> overwrite) {
        this.overwrite = overwrite;
    }

    public DefaultPageFactory(String pageId, String applicationName, String pageName)
        throws IllegalArgumentException {
        super(null, pageId, applicationName, pageName);
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
                    e.printStackTrace();
                }
            }
            try {
                return pageClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("Peng");
            }
        } finally {
            Application.get().removeComponentInstantiationListener(componentInstantiationListener);
        }
    }

    public Class<? extends Page> getPageClass() {
        return pageClass;
    }

    public void setBundleContext(BundleContext bundleContext) {
        super.setInternalBundleContext(bundleContext);
        this.bundleContext = bundleContext;
    }

}
