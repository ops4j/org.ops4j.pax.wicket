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

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.protocol.http.WebApplication;
import org.ops4j.pax.wicket.api.InjectorHolder;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.internal.injection.AbstractPaxWicketInjector;
import org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;
import org.springframework.context.ApplicationContext;

/**
 * Wicket component injector which should be used to test {@link PaxWicketBean} annotated fields. Those fields could be
 * injected using an {@link ApplicationContextMock}. The typical use case is almost similar to a regular wicket spring
 * test looking like:
 * 
 * <code>
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
 * </code>
 */
public class PaxWicketSpringBeanComponentInjector implements IComponentInstantiationListener {

    private static MetaDataKey<ApplicationContext> CONTEXT_KEY = new MetaDataKey<ApplicationContext>()
    {
        private static final long serialVersionUID = 1L;
    };
    
    private PaxWicketTestBeanInjector beanInjector;

    public PaxWicketSpringBeanComponentInjector(WebApplication webApp, ApplicationContext appContext) {
        webApp.setMetaData(CONTEXT_KEY, appContext);
        beanInjector = new PaxWicketTestBeanInjector();
        InjectorHolder.setInjector(webApp.getApplicationKey(), beanInjector);
    }
    
    public void onInstantiation(Component component) {
        beanInjector.inject(component);
    }
    
    private class PaxWicketTestBeanInjector extends AbstractPaxWicketInjector {

        public void inject(Object toInject) {
            for (Field field : getFields(toInject.getClass())) {
                PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
                Object proxy =
                    LazyInitProxyFactory.createProxy(field.getType(), new SpringTestProxyTargetLocator(annotation.name(),
                        field.getType()));
                setField(toInject, field, proxy);
            }
        }
    }
    
    private static class SpringTestProxyTargetLocator implements IProxyTargetLocator {

        private static final long serialVersionUID = -4804663390878149597L;

        private String beanName;
        private Class<?> type;

        public SpringTestProxyTargetLocator(String beanName, Class<?> type) {
            super();
            this.beanName = beanName;
            this.type = type;
        }

        public Object locateProxyTarget() {
            ApplicationContext appContext = Application.get().getMetaData(CONTEXT_KEY);
            if (beanName.equals("")) {
                return appContext.getBean(type);
            } else {
                return appContext.getBean(beanName);
            }
        }

    }

}
