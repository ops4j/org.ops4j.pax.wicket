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
package org.ops4j.pax.wicket.samples.blueprint.injection.simple.internal.service;

/**
 * Most trivial implementation of the service. This implementation could also be
 * located in a different bundle and be imported as OSGi service.
 */
public class DefaultMyService implements MyService {

    private static final long serialVersionUID = -2273870410780502670L;

    public String someEchoMethod(String toEcho) {
        return toEcho;
    }

}
