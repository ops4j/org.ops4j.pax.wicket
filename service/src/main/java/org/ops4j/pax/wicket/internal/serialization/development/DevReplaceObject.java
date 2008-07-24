/*  Copyright 2008 Edward Yakop.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied.
 *
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.ops4j.pax.wicket.internal.serialization.development;

import java.io.Serializable;

/**
 * @author edward.yakop@gmail.com
 */
final class DevReplaceObject
    implements Serializable
{

    private static final long serialVersionUID = 1L;

    private final String m_className;
    private final byte[] m_objectByteArray;

    DevReplaceObject( String className, byte[] objectByteArray )
    {
        m_className = className;
        m_objectByteArray = objectByteArray;
    }

    public final String getClassName()
    {
        return m_className;
    }

    public final byte[] getObjectByteArray()
    {
        return m_objectByteArray;
    }
}
