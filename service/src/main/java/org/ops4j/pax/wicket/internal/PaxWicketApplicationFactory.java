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

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.Filter;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import org.apache.wicket.IPageFactory;
import org.apache.wicket.protocol.http.IWebApplicationFactory;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;
import org.ops4j.pax.wicket.api.Constants;
import org.ops4j.pax.wicket.api.SuperFilter;
import org.ops4j.pax.wicket.api.SuperFilters;
import org.ops4j.pax.wicket.api.WebApplicationFactory;
import org.ops4j.pax.wicket.internal.filter.FilterDelegator;
import org.ops4j.pax.wicket.internal.injection.ComponentInstantiationListenerFacade;
import org.ops4j.pax.wicket.spi.support.DelegatingComponentInstanciationListener;
import org.ops4j.pax.wicket.util.serialization.PaxWicketSerializer;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An internal wrapper for the {@link WebApplicationFactory} exported by clients who want to register an application.
 * This class adds all the logic to extract the required properties from the osgi service and wrapping the created
 * application factory with the classloading, injection and other tricks required to run the application.
 */
public class PaxWicketApplicationFactory implements IWebApplicationFactory {

    private static final Logger LOG = LoggerFactory.getLogger(PaxWicketApplicationFactory.class);

    private final BundleContext bundleContext;
    private final WebApplicationFactory<? extends WebApplication> webApplicationFactory;
    private final String applicationName;
    private final String mountPoint;
    private final Map<String, String> contextParams;
    private final File tmpDir;
    private final FilterDelegator filterDelegator;
    private final List<SuperFilter> superFilterList = new ArrayList<SuperFilter>(0);

    private Class<? extends WicketFilter> wicketFilterClass = WicketFilter.class;

    @SuppressWarnings("unchecked")
    public static PaxWicketApplicationFactory
        createPaxWicketApplicationFactory(
                BundleContext bundleContext,
                WebApplicationFactory<?> webApplicationFactory,
                ServiceReference<WebApplicationFactory<?>> reference) {
        File tmpDir = retrieveTmpFile(bundleContext);
        tmpDir.mkdirs();
        String mountPoint = (String) reference.getProperty(Constants.MOUNTPOINT);
        String applicationName = (String) reference.getProperty(Constants.APPLICATION_NAME);
        Map<String, String> contextParams = (Map<String, String>) reference.getProperty(Constants.CONTEXT_PARAMS);

        if (contextParams == null) {
            contextParams = new HashMap<String, String>();
        }
        if (!contextParams.containsKey(WicketFilter.FILTER_MAPPING_PARAM)) {
            contextParams.put(WicketFilter.FILTER_MAPPING_PARAM, "/" + mountPoint + "/*");
        }

        FilterDelegator filterDelegator =
                new FilterDelegator(reference.getBundle().getBundleContext(), applicationName);
        PaxWicketApplicationFactory factory =
            new PaxWicketApplicationFactory(bundleContext, webApplicationFactory, applicationName, mountPoint,
                contextParams, tmpDir, filterDelegator);
        return factory;
    }

    private static File retrieveTmpFile(BundleContext bundleContext) {
        File tmpDir = new File(System.getProperty("java.io.tmpdir"));
        if (tmpDir == null) {
            throw new IllegalStateException("Platform needs file system access to work correctly.");
        }
        return tmpDir;
    }

    /**
     * 
     * @return the concrete wicket filter class we will use as a base to intercept
     */
    public Class<? extends WicketFilter> getFilterClass() {
        return wicketFilterClass;
    }

    private PaxWicketApplicationFactory(BundleContext bundleContext,
            WebApplicationFactory<? extends WebApplication> webApplicationFactory,
                                        String applicationName, String mountPoint, Map<String, String> contextParams,
            File tmpDir,
                                        FilterDelegator filterDelegator) {
        this.bundleContext = bundleContext;
        this.webApplicationFactory = webApplicationFactory;
        this.applicationName = applicationName;
        this.mountPoint = mountPoint;
        this.contextParams = contextParams;
        this.tmpDir = tmpDir;
        this.filterDelegator = filterDelegator;
        Class<?> factoryClass = webApplicationFactory.getClass();
        SuperFilters superFilters = factoryClass.getAnnotation(SuperFilters.class);
        LOG.info("Scan for superfilter at class {}...", factoryClass);
        if (superFilters != null) {
            LOG.info("Found @SuperFilters annotation at WebApplicationFactory: {}", factoryClass);
            for (SuperFilter superFilter : superFilters.filters()) {
                addToSuperFilterList(superFilter);
            }
        } else {
            SuperFilter superFilter = factoryClass.getAnnotation(SuperFilter.class);
            if (superFilter != null) {
                LOG.info("Found @SuperFilter annotation at WebApplicationFactory: {}", factoryClass);
                addToSuperFilterList(superFilter);
            }
        }
    }

    @SuppressWarnings("unchecked")
    private void addToSuperFilterList(SuperFilter superFilter) {
        Class<? extends Filter> filter = superFilter.filter();
        if (WicketFilter.class.isAssignableFrom(filter)) {
            LOG.info("Using special WicketFilter from {}", filter);
            wicketFilterClass = (Class<? extends WicketFilter>) filter;
        } else {
            superFilterList.add(superFilter);
        }
    }

    /**
     * @return the current value of superFilterList
     */
    public List<SuperFilter> getSuperFilterList() {
        return Collections.unmodifiableList(superFilterList);
    }

    public boolean isValidFactory() {
        return applicationName != null && mountPoint != null;
    }

    public WebApplication createApplication(WicketFilter filter) {
        return createFromFactory(webApplicationFactory);
    }

    private <T extends WebApplication> T createFromFactory(WebApplicationFactory<T> factory) {
        Class<T> applicationClass = factory.getWebApplicationClass();
        Enhancer e = new Enhancer();
        e.setSuperclass(applicationClass);
        e.setCallback(new WebApplicationWrapper());
        @SuppressWarnings("unchecked")
        T instance = (T) e.create();
        factory.onInstantiation(instance);
        return instance;
    }

    private class WebApplicationWrapper implements MethodInterceptor {

        private PaxWicketPageFactory pageFactory;
        private DelegatingClassResolver delegatingClassResolver;
        private DelegatingComponentInstanciationListener delegatingComponentInstanciationListener;
        private PageMounterTracker mounterTracker;

        public Object intercept(Object object, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
            if (isFinalizeMethod(method)) {
                // swallow finalize call
                return null;
            } else if (isInitMethod(method)) {
                handleInit((WebApplication) object);
            } else if (isNewPageFactory(method)) {
                return handleNewPageFactory();
            } else if (isOnDestoryMethod(method)) {
                handleOnDestroy();
            }
            method.setAccessible(true);
            return methodProxy.invokeSuper(object, args);
        }

        /**
         * A helper method to verify method signatures.
         * 
         * @param method Method to check.
         * @param name Expected name.
         * @param returnType Expected return type.
         * @param parameterTypes Parameters for method.
         * @return True if all criteria matched.
         */
        private boolean checkSignature(Method method, String name, Class<?> returnType, Class<?>... parameterTypes) {
            if (method.getName().equals(name) && method.getReturnType() == returnType) {
                return Arrays.equals(method.getParameterTypes(), parameterTypes);
            }
            return false;
        }

        /**
         * Checks if the method is derived from Object.finalize()
         * 
         * @param method method being tested
         * @return true if the method is defined from Object.finalize(), false otherwise
         */
        private boolean isFinalizeMethod(Method method) {
            return checkSignature(method, "finalize", void.class);
        }

        private boolean isInitMethod(Method method) {
            return checkSignature(method, "init", void.class);
        }

        private boolean isNewPageFactory(Method method) {
            return checkSignature(method, "newPageFactory", IPageFactory.class);
        }

        private boolean isOnDestoryMethod(Method method) {
            return checkSignature(method, "onDestroy", void.class);
        }

        private void handleInit(WebApplication application) {
            // application.initApplication();
            delegatingClassResolver = new DelegatingClassResolver(bundleContext, applicationName);
            delegatingClassResolver.intialize();

            delegatingComponentInstanciationListener =
                    new DelegatingComponentInstanciationListener(bundleContext, applicationName);
            delegatingComponentInstanciationListener.intialize();

            application.getFrameworkSettings().setSerializer(new PaxWicketSerializer(getApplicationName()));
            application.getComponentInstantiationListeners().add(new ComponentInstantiationListenerFacade(
                    delegatingComponentInstanciationListener));
            application.getApplicationSettings().setClassResolver(delegatingClassResolver);
            mounterTracker = new PageMounterTracker(bundleContext, application, getApplicationName());
            mounterTracker.open();
            filterDelegator.start();
        }

        private IPageFactory handleNewPageFactory() {
            if (pageFactory == null) {
                pageFactory = new PaxWicketPageFactory(bundleContext, applicationName);
                pageFactory.initialize();
            }
            return pageFactory;
        }

        private void handleOnDestroy() {
            PaxWicketPageFactory old = pageFactory;
            pageFactory = null;
            old.dispose();
            delegatingClassResolver.dispose();
            delegatingComponentInstanciationListener.dispose();
            mounterTracker.close();
            filterDelegator.stop();
        }

    }

    public BundleContext getBundleContext() {
        return bundleContext;
    }

    public WebApplicationFactory<? extends WebApplication> getWebApplicationFactory() {
        return webApplicationFactory;
    }

    public String getApplicationName() {
        return applicationName;
    }

    public String getMountPoint() {
        return mountPoint;
    }

    public Map<String, String> getContextParams() {
        return contextParams;
    }

    public File getTmpDir() {
        return tmpDir;
    }

    public FilterDelegator getFilterDelegator() {
        return filterDelegator;
    }

    public void destroy(WicketFilter filter) {
    }

}
