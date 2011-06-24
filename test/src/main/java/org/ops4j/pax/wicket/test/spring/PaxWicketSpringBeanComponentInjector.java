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
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.MetaDataKey;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.application.IComponentInstantiationListener;
import org.apache.wicket.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WebSession;
import org.ops4j.pax.wicket.api.InjectorHolder;
import org.ops4j.pax.wicket.api.PaxWicketInjector;
import org.ops4j.pax.wicket.util.proxy.IProxyTargetLocator;
import org.ops4j.pax.wicket.util.proxy.LazyInitProxyFactory;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;
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
    
    private class PaxWicketTestBeanInjector implements PaxWicketInjector {

        public void inject(Object toInject) {
            for (Field field : getFields(toInject.getClass())) {
                PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
                Object proxy =
                    LazyInitProxyFactory.createProxy(field.getType(), new SpringTestProxyTargetLocator(annotation.name(),
                        field.getType()));
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                try {
                    field.set(toInject, proxy);
                } catch (Exception e) {
                    throw new IllegalStateException("Should not happen");
                }
            }
        }
    }
    
    private List<Field> getFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();

        while (clazz != null && !isBoundaryClass(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (!field.isAnnotationPresent(PaxWicketBean.class)) {
                    continue;
                }
                fields.add(field);
            }
            clazz = clazz.getSuperclass();
        }
        return fields;
    }
    
    private boolean isBoundaryClass(Class<?> clazz) {
        if (clazz.equals(WebPage.class) || clazz.equals(Page.class) || clazz.equals(Panel.class)
                || clazz.equals(MarkupContainer.class) || clazz.equals(Component.class)
                || clazz.equals(AuthenticatedWebSession.class) || clazz.equals(WebSession.class)
                || clazz.equals(Session.class) || clazz.equals(Object.class)) {
            return true;
        }
        return false;
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
