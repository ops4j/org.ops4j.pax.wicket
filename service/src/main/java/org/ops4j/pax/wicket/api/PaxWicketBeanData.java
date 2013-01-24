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
package org.ops4j.pax.wicket.api;

import java.io.Serializable;
import java.lang.reflect.Field;

import javax.inject.Inject;
import javax.inject.Named;

/**
 * POJO representing injection field.
 */
public class PaxWicketBeanData implements Serializable {

    private static final long serialVersionUID = 1L;

    private String injectionSource;
    private String name;

    public PaxWicketBeanData(Field field) {
        if (field.isAnnotationPresent(PaxWicketBean.class)) {
            PaxWicketBean annotation = field.getAnnotation(PaxWicketBean.class);
            injectionSource = annotation.injectionSource();
            name = annotation.name();
        } else if (field.isAnnotationPresent(Inject.class)) {
            injectionSource = PaxWicketBean.INJECTION_SOURCE_UNDEFINED;
            name = field.isAnnotationPresent(Named.class) ? field.getAnnotation(Named.class).value() : "";
        }
    }

    public String getName() {
        return name;
    }

    public String getInjectionSource() {
        return injectionSource;
    }

    public static boolean isPaxWicketField(Field field) {
        return field.isAnnotationPresent(PaxWicketBean.class) || field.isAnnotationPresent(Inject.class);
    }

}
