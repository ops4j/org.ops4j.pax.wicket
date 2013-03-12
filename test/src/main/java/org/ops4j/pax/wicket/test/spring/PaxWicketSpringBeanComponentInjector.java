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
package org.ops4j.pax.wicket.test.spring;

import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Named;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.InjectorHolder;
import org.ops4j.pax.wicket.internal.injection.AbstractPaxWicketInjector;
import org.ops4j.pax.wicket.spi.ProxyTarget;
import org.ops4j.pax.wicket.spi.ProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;
import org.springframework.context.ApplicationContext;

/**
 * Wicket component injector which should be used to test {@link Inject}
 * annotated fields. Those fields could be injected using an
 * {@link ApplicationContextMock}. The typical use case is almost similar to a
 * regular wicket spring test looking like: <code>
 * 1. setup dependencies and mock objects
 * 2. setup mock injection environment
 *       ApplicationContextMock appctx=new ApplicationContextMock();
 *       appctx.putBean("contactDao", dao);
 * 
 * 3. setup WicketTester and injector for @SpringBean
 *       WicketTester app=new WicketTester();
 *       app.getApplication().addComponentInstantiationListener(
 *           new PaxWicketSpringComponentInjector(app.getApplication(), appctx ));
 *     
 * 4. run the test
 * </code> For simplicity we do not provide an own mocking class for blueprint.
 * Simply reuse the spring {@link ApplicationContextMock}. Though, make sure
 * that you set the {@link #simulateBlueprint} flag to true. That way you make
 * sure that the test case simulates the special behavior for blueprint
 * injection.
 */
public class PaxWicketSpringBeanComponentInjector implements IComponentInstantiationListener {

    private static MetaDataKey<ApplicationContext> CONTEXT_KEY = new MetaDataKey<ApplicationContext>() {
                                                                   private static final long serialVersionUID = 1L;
                                                               };

    private final PaxWicketTestBeanInjector        beanInjector;
    private boolean                                simulateBlueprint;

    public PaxWicketSpringBeanComponentInjector(WebApplication webApp, ApplicationContext appContext) {
        webApp.setMetaData(CONTEXT_KEY, appContext);
        beanInjector = new PaxWicketTestBeanInjector();
        InjectorHolder.setInjector(webApp.getApplicationKey(), beanInjector);
    }

    public PaxWicketSpringBeanComponentInjector(WebApplication webApp, ApplicationContext appContext, boolean simulateBlueprint) {
        webApp.setMetaData(CONTEXT_KEY, appContext);
        beanInjector = new PaxWicketTestBeanInjector();
        InjectorHolder.setInjector(webApp.getApplicationKey(), beanInjector);
        this.simulateBlueprint = simulateBlueprint;
    }

    /**
     * This method is required in a case where you need to add the same injector
     * to an additional application.
     */
    public void registerForAdditionalName(String applicationKey) {
        InjectorHolder.setInjector(applicationKey, beanInjector);
    }

    public void onInstantiation(Component component) {
        beanInjector.inject(component, component.getClass());
    }

    private class PaxWicketTestBeanInjector extends AbstractPaxWicketInjector {

        public void inject(Object toInject, Class<?> toHandle) {
            for (Field field : getFields(toInject.getClass())) {
                Named named = field.getAnnotation(Named.class);
                if (simulateBlueprint && (named == null || named.value().isEmpty())) {
                    throw new IllegalStateException("Blueprint mode does not allow annotations without name");
                }
                String bn = "";
                if (named != null) {
                    if (named.value() != null) {
                        bn = named.value();
                    }
                }
                Object proxy = LazyInitProxyFactory.createProxy(field.getType(), new SpringTestProxyTargetLocator(bn, field.getType()));
                setField(toInject, field, proxy);
            }
        }
    }

    private static class SpringTestProxyTargetLocator implements ProxyTargetLocator {

        private static final long serialVersionUID = -4804663390878149597L;

        private final String      beanName;
        private final Class<?>    type;

        public SpringTestProxyTargetLocator(String beanName, Class<?> type) {
            super();
            this.beanName = beanName;
            this.type = type;
        }

        public ProxyTarget locateProxyTarget() {
            final ApplicationContext appContext = Application.get().getMetaData(CONTEXT_KEY);
            if (beanName.equals("")) {
                return new ProxyTarget() {

                    public Object getTarget() {
                        return appContext.getBean(type);
                    }

                };
            } else {
                return new ProxyTarget() {
                    public Object getTarget() {
                        return appContext.getBean(beanName);
                    }

                };
            }
        }

        public Class getParent() {
            return null;
        }
    }

}
