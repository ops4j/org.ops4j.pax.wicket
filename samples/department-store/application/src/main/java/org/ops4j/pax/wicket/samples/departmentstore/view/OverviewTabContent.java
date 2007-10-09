/*
 * Copyright 2006 Niclas Hedhman.
 * Copyright 2005 Edward Yakop.
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
package org.ops4j.pax.wicket.samples.departmentstore.view;

import java.util.Locale;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;

/**
 * {@code OverviewTabContent} represents an interface that can create tab item representing a floor of Sungei Wang
 * plaza.
 * 
 * @author Edward Yakop
 * @since 1.0.0
 */
public interface OverviewTabContent
{
    /**
     * The tab identifier.
     * 
     * @return The tab identifier.
     * @since 1.0.0
     */
    String getTabId();

    /**
     * Create the tab given the specified {@code locale} as the locale of the displayed tab item label. This method must
     * not return {@code null} object.
     * 
     * @param locale The locale for the tab item label. This argument must not be {@code null}.
     * @return A new abstract tab.
     * 
     * @since 1.0.0
     */
    AbstractTab createTab( Locale locale );
}
