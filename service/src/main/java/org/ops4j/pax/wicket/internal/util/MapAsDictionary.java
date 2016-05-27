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
/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.ops4j.pax.wicket.internal.util;

import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Map;

/**
 * A wrapper around a {@link java.util.Map} access it as a {@link java.util.Dictionary}.
 *
 * This class is a Dictionary-implementation that delegates operations to a backing {@link java.util.Map}. The resulting
 * Dictionary can be accessed an manipulated like any other Dictionary.
 *
 * Adapted code from apache felix utils.collections
 *
 * As opposed to the original felix-implementation this Map is not immutable and can be manipulated, with the
 * restriction(s) described above.
 *
 * @author nmw
 * @version $Id: $Id
 */
public class MapAsDictionary<K, V> extends Dictionary<K, V> {

    private Map<K, V> map;

    /**
     * creates a new instance backed by the given map. Please use {@link org.ops4j.pax.wicket.internal.util.MapAsDictionary#wrap} to prevent nesting of
     * {@link org.ops4j.pax.wicket.internal.util.MapAsDictionary} and {@link org.ops4j.pax.wicket.internal.util.DictionaryAsMap}
     *
     * @param map a {@link java.util.Map} object.
     */
    public MapAsDictionary(Map<K, V> map) {
        this.map = map;
    }

    /**
     * creates a Dictionary-representation of the map. If the map is an instance of {@link org.ops4j.pax.wicket.internal.util.DictionaryAsMap} the original
     * dictionary is returned to prevent deeper nesting.
     *
     * @param map a {@link java.util.Map} object.
     * @return a {@link java.util.Dictionary} object.
     * @param <K> a K object.
     * @param <V> a V object.
     */
    public static <K, V> Dictionary<K, V> wrap(Map<K, V> map) {
        if (map instanceof DictionaryAsMap) {
            return ((DictionaryAsMap<K, V>) map).getDictionary();
        }
        return new MapAsDictionary<K, V>(map);
    }

    /**
     * <p>setSourceMap.</p>
     *
     * @param map a {@link java.util.Map} object.
     */
    public void setSourceMap(Map<K, V> map) {
        this.map = map;
    }

    /** {@inheritDoc} */
    @Override
    public Enumeration<V> elements() {
        return new IteratorToEnumeration<V>(map.values().iterator());
    }

    /** {@inheritDoc} */
    @Override
    public V get(Object key) {
        return map.get(key);
    }

    /** {@inheritDoc} */
    @Override
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /** {@inheritDoc} */
    @Override
    public Enumeration<K> keys() {
        return new IteratorToEnumeration<K>(map.keySet().iterator());
    }

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    @Override
    public V put(Object key, Object value) {
        return map.put((K) key, (V) value);
    }

    /** {@inheritDoc} */
    @Override
    public V remove(Object key) {
        return map.remove(key);
    }

    /** {@inheritDoc} */
    @Override
    public int size() {
        if (map == null) {
            return 0;
        }
        return map.size();
    }

    /**
     * <p>Getter for the field <code>map</code>.</p>
     *
     * @return a {@link java.util.Map} object.
     */
    public Map<K, V> getMap() {
        return this.map;
    }

    class IteratorToEnumeration<T> implements Enumeration<T> {
        private final Iterator<T> iter;

        public IteratorToEnumeration(Iterator<T> iter) {
            this.iter = iter;
        }

        public boolean hasMoreElements() {
            if (iter == null) {
                return false;
            }
            return iter.hasNext();
        }

        public T nextElement() {
            if (iter == null) {
                return null;
            }
            return iter.next();
        }
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    /** {@inheritDoc} */
    @Override
    public String toString() {
        return map.toString();
    }
}
