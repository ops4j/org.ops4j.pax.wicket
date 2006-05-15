/*
 * Copyright 2006 Niclas Hedhman.
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
package org.ops4j.pax.wicket.samples.library.skins.std;

import wicket.markup.html.WebPage;

public class MainPage extends WebPage
{

    /**
     * Constructor. Having this constructor public means that you page is
     * 'bookmarkable' and hence can be called/ created from anywhere.
     */
    public MainPage()
    {
        add( new TitlePanel( "title" ) );
        add( new SectionsPanel( "sections" ) );
        add( new MenuPanel( "menu" ) );
        add( new CreditsPanel( "credits" ) );
        add( new ContentPanel( "content" ) );
        add( new ArticlesPanel( "articles" ) );
        add( new QuickButtonsPanel( "quickbuttons" ) );
    }
}
