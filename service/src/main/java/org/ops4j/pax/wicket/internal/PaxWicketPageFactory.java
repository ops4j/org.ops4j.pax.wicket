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
package org.ops4j.pax.wicket.internal;

import static org.ops4j.lang.NullArgumentException.validateNotNull;

import java.util.HashMap;

import org.apache.wicket.IPageFactory;
import org.apache.wicket.Page;
import org.apache.wicket.session.DefaultPageFactory;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.request.component.IRequestablePage;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.ops4j.pax.wicket.api.PageFactory;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Wrapper around the original wicket {@link DefaultPageFactory} adding lookup possiblities for own page loaders. In
 * case non are provided the original wicket algorithm for loading of pages is used.
 */
public final class PaxWicketPageFactory implements IPageFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(PaxWicketPageFactory.class);

    private final BundleContext bundleContext;
    private final String applicationName;
    private final HashMap<Class<?>, PageFactory<?>> contents;

    private ServiceTracker m_pageTracker;

    public PaxWicketPageFactory(BundleContext context, String applicationName) throws IllegalArgumentException {
        validateNotNull(context, "context");
        validateNotNull(applicationName, "applicationName");

        contents = new HashMap<Class<?>, PageFactory<?>>();
        bundleContext = context;
        this.applicationName = applicationName;
    }

    public final void initialize() {
        m_pageTracker = new PaxWicketPageTracker(bundleContext, applicationName, this);
        m_pageTracker.open();
    }

    public final void dispose() {
        synchronized (this) {
            contents.clear();
            m_pageTracker.close();
        }
    }

    /**
     * Creates a new page using a page class.
     *
     * @param pageClass The page class to instantiate
     *
     * @return The page
     *
     * @throws org.apache.wicket.WicketRuntimeException Thrown if the page cannot be constructed
     */
    public final <C extends IRequestablePage> IRequestablePage newPage(Class<C> pageClass) throws IllegalArgumentException { 
        PageFactory<?> content;
        synchronized (this) {
            content = contents.get(pageClass);
        }
        if (content != null) {
            return content.createPage(new PageParameters());
        }
        return new DefaultPageFactory().newPage(pageClass);
    }

    /**
     * Creates a new Page, passing PageParameters to the Page constructor if such a constructor exists. If no such
     * constructor exists and the parameters argument is null or empty, then any available default constructor will be
     * used.
     *
     * @param pageClass The class of Page to create
     * @param parameters Any parameters to pass to the Page's constructor
     *
     * @return The new page
     *
     * @throws org.apache.wicket.WicketRuntimeException Thrown if the page cannot be constructed
     */
    public final <C extends IRequestablePage> IRequestablePage newPage(Class<C> pageClass, PageParameters parameters)
        throws IllegalArgumentException {
        PageFactory<?> content;
        synchronized (this) {
            content = contents.get(pageClass);
        }
        if (content != null) {
            return content.createPage(parameters);
        }
        return new DefaultPageFactory().newPage(pageClass, parameters);
    }

    public <C extends IRequestablePage> boolean isBookmarkable(Class<C> pageClass) {
        return new DefaultPageFactory().isBookmarkable(pageClass);
    }

    public void add(Class<?> pageClass, PageFactory<?> pageSource) throws IllegalArgumentException {
        validateNotNull(pageClass, "pageClass");
        validateNotNull(pageSource, "pageSource");

        synchronized (this) {
            contents.put(pageClass, pageSource);
        }
    }

    public final void remove(Class<?> pageClass) throws IllegalArgumentException {
        validateNotNull(pageClass, "pageClass");

        synchronized (this) {
            contents.remove(pageClass);
        }
    }

}

