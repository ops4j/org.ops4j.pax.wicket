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
package org.ops4j.pax.wicket.util;

import org.apache.wicket.Application;
import org.apache.wicket.IInitializer;

/**
 * {@code Initializer} is invoked by {@link Application} to initialize wicket application.
 * <p>
 * The problem here is that there are various activators currently hidden inside pax-wicket (.e.g extension and the base
 * initializer). Even if those initializer are externalized there's still the problem that wicket is only looking for
 * one inizializer per level in 1.4. For wicket 1.5 we've to think of a different approach here.
 * </p>
 *
 * @author Edward Yakop
 * @since 0.5.0
 */
public final class Initializer implements IInitializer {

    private final IInitializer wicketInitializer;
    private final IInitializer wicketExtensionInitializer;

    public Initializer() {
        wicketInitializer = new org.apache.wicket.Initializer();
        wicketExtensionInitializer = new org.apache.wicket.extensions.Initializer();
    }

    /**
     * Initialize the application.
     *
     * @param application The application loading the component
     *
     * @since 0.5.0
     */
    public final void init(Application application) {
        wicketInitializer.init(application);
        wicketExtensionInitializer.init(application);
    }

    public void destroy(Application application) {
        wicketExtensionInitializer.destroy(application);
        wicketInitializer.destroy(application);
    }

}
