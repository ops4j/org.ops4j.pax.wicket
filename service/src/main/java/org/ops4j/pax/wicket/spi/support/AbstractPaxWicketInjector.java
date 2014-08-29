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
package org.ops4j.pax.wicket.spi.support;

import java.lang.reflect.Field;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.apache.wicket.Component;
import org.apache.wicket.MarkupContainer;
import org.apache.wicket.Page;
import org.apache.wicket.Session;
import org.apache.wicket.authroles.authentication.AuthenticatedWebSession;
import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.protocol.http.WebSession;
import org.ops4j.pax.wicket.api.PaxWicketInjector;

public abstract class AbstractPaxWicketInjector implements PaxWicketInjector {

	 protected List<Field> getSingleLevelOfFields(final Class<?> clazz) {
	        final List<Field> fields = new ArrayList<Field>();
	        AccessController.doPrivileged(new PrivilegedAction<Void>() {
				@Override
				public Void run() {
					for (Field field : clazz.getDeclaredFields()) {
			            if (!field.isAnnotationPresent(Inject.class)) {
			                continue;
			            }
			            fields.add(field);
			        }
					return null;
				}
			});
	        
	        return fields;
	    }

	    protected List<Field> getFields(final Class<?> component) {
	        final List<Field> fields = new ArrayList<Field>();

	        AccessController.doPrivileged(new PrivilegedAction<Void>() {
				@Override
				public Void run() {
					Class<?> clazz = component;
			        while (clazz != null && !isBoundaryClass(clazz)) {
			            for (Field field : clazz.getDeclaredFields()) {
			                if (!field.isAnnotationPresent(Inject.class)) {
			                    continue;
			                }
			                fields.add(field);
			            }
			            clazz = clazz.getSuperclass();
			        }
			        return null;
				}
	        });
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

	    protected void setField(final Object component, final Field field, final Object proxy) {
	        try {
	            checkAccessabilityOfField(field);
	            AccessController.doPrivileged(new PrivilegedExceptionAction<Void>() {
	    			@Override
	    			public Void run() throws IllegalArgumentException, IllegalAccessException {
						field.set(component, proxy);
	    				return null;
	    			}
	            });
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

    protected Set<String> countComponentContainPaxWicketBeanAnnotatedFieldsHierachical(final Class<?> component) {
        final Set<String> foundAnnotation = new HashSet<String>();
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
				Class<?> clazz = component;
		        while (clazz != null && !isBoundaryClass(clazz)) {
		            for (Field field : clazz.getDeclaredFields()) {
		                if (field.isAnnotationPresent(Inject.class)) {
		                    foundAnnotation.add(field.toGenericString());
		                }
		            }
		            clazz = clazz.getSuperclass();
		        }
		        return null;
			}
        });
        return foundAnnotation;
    }

    protected Set<String> countComponentContainPaxWicketBeanAnnotatedOneLevel(final Class<?> clazz) {
        final Set<String> foundAnnotation = new HashSet<String>();
        AccessController.doPrivileged(new PrivilegedAction<Void>() {
			@Override
			public Void run() {
		        for (Field field : clazz.getDeclaredFields()) {
		            if (field.isAnnotationPresent(Inject.class)) {
		                foundAnnotation.add(field.toGenericString());
		            }
		        }
		        return null;
			}
        });
        return foundAnnotation;
    }
}
