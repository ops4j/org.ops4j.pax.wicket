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

package org.ops4j.pax.wicket.api;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation used to tag a field as a placeholder for a spring bean.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({/* ElementType.METHOD, */ElementType.FIELD })
@Documented
public @interface PaxWicketBean {

    /**
     * Will always (and only) search for a spring application context
     */
    public static final String INJECTION_SOURCE_SPRING = "spring";
    /**
     * Will always (and only) search for a blueprint application context
     */
    public static final String INJECTION_SOURCE_BLUEPRINT = "blueprint";
    /**
     * Will scan for blueprint and spring context; if none or both are found INJECTION_SOURCE_NULL is used.
     */
    public static final String INJECTION_SOURCE_SCAN = "scan";
    /**
     * Will simply inject null instead of a proxy
     */
    public static final String INJECTION_SOURCE_NULL = "null";
    /**
     * Uses other, general default values
     */
    public static final String INJECTION_SOURCE_UNDEFINED = "null";

    /**
     * Optional attribute for specifying the name of the bean. If not specified, the bean will be looked up by the type
     * of the field with the annotation.
     */
    String name() default "";

    /**
     * Allows to override the source of the injection directly in the source code
     */
    String injectionSource() default INJECTION_SOURCE_UNDEFINED;

}
