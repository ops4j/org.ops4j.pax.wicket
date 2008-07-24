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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import static java.lang.Class.forName;
import static junit.framework.Assert.assertEquals;
import org.apache.wicket.application.IClassResolver;
import org.junit.Test;

/**
 * @author edward.yakop@gmail.com
 */
public class DevModeObjectInputStreamTest
{

    @Test
    public final void testSuccessfulSerialization()
        throws Throwable
    {
        String objToSerialize = "pax-wicket";

        byte[] serializedBA = serialize( objToSerialize );
        ByteArrayInputStream stream = new ByteArrayInputStream( serializedBA );
        DevModeObjectInputStream inputStream = new DevModeObjectInputStream( stream, new ClassResolver() );
        Object deserializedObject = inputStream.readObject();

        assertEquals( objToSerialize, deserializedObject );
    }

    private byte[] serialize( Object object )
        throws IOException
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        DevModeObjectOutputStream devModeOS = new DevModeObjectOutputStream( byteArrayOS );

        devModeOS.writeObject( object );
        devModeOS.flush();

        return byteArrayOS.toByteArray();
    }

    private static class ClassResolver
        implements IClassResolver
    {

        public final Class resolveClass( String className )
            throws ClassNotFoundException
        {
            return forName( className );
        }
    }
}
