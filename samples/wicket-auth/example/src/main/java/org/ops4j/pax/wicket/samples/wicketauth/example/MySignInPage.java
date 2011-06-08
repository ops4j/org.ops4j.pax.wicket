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
/*
 * $Id: MySignInPage.java 459307 2006-02-14 09:57:21Z jonl $
 * $Revision: 459307 $ $Date: 2006-02-14 18:57:21 +0900 (Tue, 14 Feb 2006) $
 * 
 * ==============================================================================
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.ops4j.pax.wicket.samples.wicketauth.example;

import wicket.PageParameters;
import wicket.authentication.pages.SignInPage;

/**
 * Simple example of a sign in page. It extends MySignInPage, a base class which
 * provide standard functionality for typical log-in pages
 * 
 * @author Jonathan Locke
 */
public final class MySignInPage extends SignInPage
{
	/**
	 * Constructor
	 */
	public MySignInPage()
	{
	}
	
	/**
	 * Constructor
	 * 
	 * @param parameters
	 *            Parameters to page
	 */
	public MySignInPage(final PageParameters parameters)
	{
	}
}
