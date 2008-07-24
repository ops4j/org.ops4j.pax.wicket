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
import java.io.ObjectInputStream;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import org.junit.Test;

/**
 * @author edward.yakop@gmail.com
 */
public final class DevModeObjectOutputStreamTest
{
    @Test
    public final void testSerialization()
        throws Throwable
    {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        DevModeObjectOutputStream devModeOS = new DevModeObjectOutputStream( byteArrayOS );

        devModeOS.writeObject( "pax-wicket" );
        devModeOS.flush();

        byte[] serializedBA = byteArrayOS.toByteArray();
        assertNotNull( serializedBA );

        ByteArrayInputStream roBAIS = new ByteArrayInputStream( serializedBA );
        ObjectInputStream roOIS = new ObjectInputStream( roBAIS );

        // Replace object
        Object deserializedObject = roOIS.readObject();
        assertNotNull( deserializedObject );
        assertTrue( DevReplaceObject.class.equals( deserializedObject.getClass() ) );

        DevReplaceObject devRO = (DevReplaceObject) deserializedObject;
        assertEquals( String.class.getName(), devRO.getClassName() );

        // Actual object
        byte[] actualObjectArray = devRO.getObjectByteArray();
        assertNotNull( actualObjectArray );

        ByteArrayInputStream actualBAIS = new ByteArrayInputStream( actualObjectArray );
        ObjectInputStream actualOIS = new ObjectInputStream( actualBAIS );
        Object actualObject = actualOIS.readObject();
        assertTrue( String.class.getName().equals( actualObject.getClass().getName() ) );
        assertEquals( "pax-wicket", actualObject );
    }
}
