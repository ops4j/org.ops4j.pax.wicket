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

import org.apache.wicket.protocol.http.WebApplication;
import org.apache.wicket.protocol.http.WicketFilter;

import javax.servlet.Filter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A {@link WebApplicationFactory} can be annotated with this to provide a hint that the supplied "SuperFilter" must be
 * used with this application. A {@link SuperFilter} is required to allow specifying a global {@link Filter} that is
 * always called before any other custom filters and will be initialized before the {@link WebApplication} is started,
 * thus it is tied to the lifecycle of the underlying Servlet like in a classical WebApplication environment. This
 * allows to enable special features of Wicket like Atmosphere integration, Native Websockets or Shiro <br>
 * <b>There is one special case</b> when the filter (an no more than one is allowed per application!) extends
 * {@link WicketFilter} it is used as the base class of PAX Wickets Wicket integration (this is needed for
 * NativeWebsockets and maybe future Wicket extensions)
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface SuperFilter {

    /**
     * @return the filter class to use, this must implement the {@link Filter} interface an has a non-arg public visible
     *         default constructor
     */
    public Class<? extends Filter> filter();

    /**
     * @return a standard java properties file that should be used to init the filter. The file is searched in the
     *         following order: Classloader of annotated class, Classloader of class object obtained by
     *         {@link #filter()}, ThreadContext classLoader, PaxWicket service Classloader.
     */
    public String initParameter() default "";
}
