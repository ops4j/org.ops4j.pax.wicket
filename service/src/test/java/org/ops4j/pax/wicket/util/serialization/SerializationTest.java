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
package org.ops4j.pax.wicket.util.serialization;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.net.URL;
import java.util.Collections;
import java.util.Iterator;
import java.util.Random;

import org.apache.wicket.application.IClassResolver;
import org.junit.Test;
import org.ops4j.pax.wicket.internal.EnumerationAdapter;

/**
 * @author edward.yakop@gmail.com
 */
public final class SerializationTest {

    @Test
    public final void testSerialization() throws Throwable {
        IClassResolver resolver = new IClassResolver()
        {

            public Class<?> resolveClass(String classname)
                throws ClassNotFoundException
            {
                ClassLoader classLoader = getClass().getClassLoader();
                return classLoader.loadClass(classname);
            }

            public Iterator<URL> getResources(String name)
            {
                try
                {
                    ClassLoader classLoader = getClass().getClassLoader();
                    return new EnumerationAdapter<URL>(classLoader.getResources(name));
                }
                catch (IOException e)
                {
                    return Collections.<URL> emptyList().iterator();
                }
            }

            /**
             * This method is uses only for some internal wicket stuff if the IClassResolver is NOT replaced and in some
             * IOC stuff also not used by pax wicket. Therefore this method should never ever be called. If it is though
             * we want to be informed about the problem as soon as possible.
             */
            public ClassLoader getClassLoader() {
                throw new UnsupportedOperationException("This method should NOT BE CALLED!");
            }
        };

        testSerializeObject("pax-wicket", resolver);
        testSerializeObject(1, resolver);

        // Test serialialize a more complex object
        SomeObject someObject = createSomeObject();
        testSerializeObject(someObject, resolver);
    }

    private SomeObject createSomeObject() {
        SomeObject someObject = new SomeObject();
        Random random = new Random(System.currentTimeMillis());
        someObject.integer = random.nextInt();
        someObject.string = "pax-wicket-rules";
        someObject.longNumber = random.nextLong();
        someObject.longObject = random.nextLong();

        return someObject;
    }

    private void testSerializeObject(Object objectToSerialize, IClassResolver resolver) throws IOException,
        ClassNotFoundException {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        PaxWicketObjectOutputStream devModeOS = new PaxWicketObjectOutputStream(byteArrayOS);

        devModeOS.writeObject(objectToSerialize);
        devModeOS.flush();

        byte[] serializedBA = byteArrayOS.toByteArray();
        assertNotNull(serializedBA);

        ByteArrayInputStream roBAIS = new ByteArrayInputStream(serializedBA);
        PaxWicketObjectInputStream roOIS = new PaxWicketObjectInputStream(roBAIS, resolver);
        Object object = roOIS.readObject();
        assertNotNull(object);
        assertEquals(objectToSerialize, object);
    }

    public static class SomeObject
            implements Serializable {
        private static final long serialVersionUID = 1L;

        private int integer;
        private String string;
        private long longNumber;
        private Long longObject;

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }

            SomeObject that = (SomeObject) o;

            if (integer != that.integer) {
                return false;
            }
            if (longNumber != that.longNumber) {
                return false;
            }
            if (longObject != null ? !longObject.equals(that.longObject) : that.longObject != null) {
                return false;
            }
            if (string != null ? !string.equals(that.string) : that.string != null) {
                return false;
            }

            return true;
        }

        @Override
        public int hashCode() {
            int result;
            result = integer;
            result = 31 * result + (string != null ? string.hashCode() : 0);
            result = 31 * result + (int) (longNumber ^ longNumber >>> 32);
            result = 31 * result + (longObject != null ? longObject.hashCode() : 0);
            return result;
        }
    }
}
