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
package org.ops4j.pax.wicket.util.authorization;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the class as denying which {@code org.apache.wicket.authorization.Action}s can be performed by a user. Whether
 * or not the Action is denied is determined by the UserAdmin service.
 * 
 * The difference between {@code DenyAction} and {@code AuthorizeAction} is that {@code AuthorizeAction} will authorize
 * any roles implied by the action, while {@code DenyAction} will deny any roles implied by the action. See the
 * UserAdmin service for more details.
 * 
 * @author David Leangen
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Inherited
public @interface DenyAction {

    /**
     * An enumeration (with a small "e") of Wicket Actions that are to be denied to the specified users. The default
     * value is empty, which signifies that this should be applied to all Actions.
     */
    String[] value() default "";
}
