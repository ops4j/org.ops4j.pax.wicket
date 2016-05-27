
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
 *
 * @author nmw
 * @version $Id: $Id
 */
package org.ops4j.pax.wicket.spi;

import java.util.Map;

import net.sf.cglib.proxy.MethodInterceptor;

//FIXME: Comment me!
//FIXME: Is it really neccesary to extend MethodInterceptor here?
public interface OverwriteProxy extends MethodInterceptor {

    /**
     * <p>getOverwrites.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    Map<String, String> getOverwrites();

    /**
     * <p>getInjectionSource.</p>
     *
     * @return a {@link java.lang.String} object.
     */
    String getInjectionSource();

}
