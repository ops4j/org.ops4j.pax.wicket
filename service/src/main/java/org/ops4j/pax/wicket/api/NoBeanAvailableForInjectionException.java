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

/**
 * Since the {@link PaxWicketInjector#onInstantiation(org.apache.wicket.Component)} method does not have a return value
 * we couldn't ask a service simply if it could find a bean to inject for a component to inject at all. Therefore we add
 * the notation to pax-wicket that every {@link PaxWicketInjector#inject(Object)} method call can throw this
 * {@link NoBeanAvailableForInjectionException}. Using this exception we can iterate over multible provider checking if
 * a bean is available.
 */
public class NoBeanAvailableForInjectionException extends RuntimeException {

    private static final long serialVersionUID = -8910110478566112761L;

    public NoBeanAvailableForInjectionException() {
        super();
    }

    public NoBeanAvailableForInjectionException(String message, Throwable cause) {
        super(message, cause);
    }

    public NoBeanAvailableForInjectionException(String message) {
        super(message);
    }

    public NoBeanAvailableForInjectionException(Throwable cause) {
        super(cause);
    }

}
