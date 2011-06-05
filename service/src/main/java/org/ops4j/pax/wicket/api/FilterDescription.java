/*
 * Copyright 2011 Fabian Souczek
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

package org.ops4j.pax.wicket.api;

public final class FilterDescription
{
    private String m_className;
    private boolean m_required;

    public FilterDescription()
    {
    }

    public FilterDescription( String className, boolean required )
    {
        m_className = className;
        m_required = required;
    }

    public String getClassName()
    {
        return m_className;
    }

    public void setClassName( String className )
    {
        m_className = className;
    }

    public boolean isRequired()
    {
        return m_required;
    }

    public void setRequired( boolean required )
    {
        m_required = required;
    }
    
    @Override
    public String toString()
    {
        return m_className + " (required=" + m_required + ")";
    }
}