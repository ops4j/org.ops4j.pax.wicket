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

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebSession;
import org.ops4j.pax.wicket.api.PaxWicketBean;
import org.ops4j.pax.wicket.api.PaxWicketInjector;

public abstract class AbstractPaxWicketInjector implements PaxWicketInjector {

    protected List<Field> getSingleLevelOfFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<Field>();
        for (Field field : clazz.getDeclaredFields()) {
            if (!field.isAnnotationPresent(PaxWicketBean.class)) {
                continue;
            }
            fields.add(field);
        }
        return fields;
    }

    protected List<Field> getFields(Class<?> clazz) {
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

    protected boolean isBoundaryClass(Class<?> clazz) {
        if (clazz.equals(WebPage.class) || clazz.equals(Page.class) || clazz.equals(Panel.class)
                || clazz.equals(MarkupContainer.class) || clazz.equals(Component.class)
                || clazz.equals(AuthenticatedWebSession.class) || clazz.equals(WebSession.class)
                || clazz.equals(Session.class) || clazz.equals(Object.class)) {
            return true;
        }
        return false;
    }

    protected void setField(Object component, Field field, Object proxy) {
        try {
            checkAccessabilityOfField(field);
            field.set(component, proxy);
        } catch (Exception e) {
            throw new RuntimeException("Bumm", e);
        }
    }

    private void checkAccessabilityOfField(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }

    protected Class<?> getBeanType(Field field) {
        Class<?> beanType = field.getType();
        return beanType;
    }

    protected int countComponentContainPaxWicketBeanAnnotatedFieldsHierachical(Class<?> component) {
        Class<?> clazz = component;
        int numberOfInjectionFields = 0;
        while (clazz != null && !isBoundaryClass(clazz)) {
            for (Field field : clazz.getDeclaredFields()) {
                if (field.isAnnotationPresent(PaxWicketBean.class)) {
                    numberOfInjectionFields++;
                }
            }
            clazz = clazz.getSuperclass();
        }
        return numberOfInjectionFields;
    }

    protected int countComponentContainPaxWicketBeanAnnotatedOneLevel(Class<?> component) {
        Class<?> clazz = component;
        int numberOfInjectionFields = 0;
        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(PaxWicketBean.class)) {
                numberOfInjectionFields++;
            }
        }
        return numberOfInjectionFields;
    }
}
