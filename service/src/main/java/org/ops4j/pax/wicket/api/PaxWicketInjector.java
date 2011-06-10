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

import org.apache.wicket.Component;
import org.ops4j.pax.wicket.util.proxy.PaxWicketBean;

/**
 * General PaxWicket injection abstraction. This interface takes any object and tries to inject all
 * {@link PaxWicketBean} annotations. In that way there is no difference if its a Wicket {@link Component} or not.
 */
public interface PaxWicketInjector {

    public void inject(Object toInject);

}
