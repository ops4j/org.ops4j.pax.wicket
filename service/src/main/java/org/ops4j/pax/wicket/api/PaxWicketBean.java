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
     * Optional attribute for specifying the name of the bean. If not specified, the bean will be looked up by the type
     * of the field with the annotation.
     */
    String name() default "";

    /**
     * Optional attribute for specifying which resolver you prefer. Per default the
     * {@link BeanResolverType#UNCONFIGURED} is used which means that we try to find the bean in first blueprint and
     * then spring and use the one we found first.
     */
    BeanResolverType beanResolverType() default BeanResolverType.UNCONFIGURED;

    public static enum BeanResolverType {
            /**
             * In {@link #UNCONFIGURED} mode we try to find the bean in first blueprint and then spring and use the one
             * we find first.
             */
            UNCONFIGURED,
            /**
             * We look only in the spring context for the bean to resolve
             */
            SPRING,
            /**
             * We look only in the blueprint context for the bean to resolve
             */
            BLUEPRINT
    }

}
