/*
 *    Copyright 2020 Metastring Foundation
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package org.metastringfoundation.data;

import org.metastringfoundation.datareader.helpers.ListToMap;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;

public class DataPoint implements Map<String, String> {
    private final Map<String, String> fields;

    public DataPoint(Map<String, String> fields) {
        this.fields = fields;
    }

    public DataPoint() {
        this.fields = new HashMap<>();
    }

    public static DataPoint of(String... args) {
        DataPoint dataPoint = new DataPoint();
        ListToMap.load(Arrays.asList(args), dataPoint);
        return dataPoint;
    }

    public Map<String, String> getAsMap() {
        return fields;
    }

    @Override
    public int size() {
        return fields.size();
    }

    @Override
    public boolean isEmpty() {
        return fields.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        if (key instanceof String) {
            return containsKey((String) key);
        }
        return false;
    }

    @Override
    public boolean containsValue(Object value) {
        if (value instanceof String) {
            return containsValue((String) value);
        }
        return false;
    }

    @Override
    public String get(Object key) {
        return fields.get(key);
    }

    public boolean containsKey(String key) {
        return fields.containsKey(key);
    }

    public boolean containsValue(String value) {
        return fields.containsValue(value);
    }

    public String get(String key) {
        return fields.get(key);
    }

    @Override
    public String put(String key, String value) {
        return fields.put(key, value);
    }

    @Override
    public String remove(Object key) {
        return null;
    }

    public String remove(String key) {
        return fields.remove(key);
    }

    @Override
    public void putAll(@Nonnull Map<? extends String, ? extends String> m) {
        fields.putAll(m);
    }

    @Override
    public void clear() {
        fields.clear();
    }

    @Override
    @Nonnull
    public Set<String> keySet() {
        return fields.keySet();
    }

    @Override
    @Nonnull
    public Collection<String> values() {
        return fields.values();
    }

    @Override
    @Nonnull
    public Set<Map.Entry<String, String>> entrySet() {
        return fields.entrySet();
    }

    public String getOrDefault(String key, String defaultValue) {
        return fields.getOrDefault(key, defaultValue);
    }

    @Override
    public void forEach(BiConsumer<? super String, ? super String> action) {
        fields.forEach(action);
    }

    @Override
    public void replaceAll(BiFunction<? super String, ? super String, ? extends String> function) {
        fields.replaceAll(function);
    }

    @Override
    public String putIfAbsent(String key, String value) {
        return fields.putIfAbsent(key, value);
    }

    public boolean remove(String key, String value) {
        return fields.remove(key, value);
    }

    @Override
    public boolean replace(String key, String oldValue, String newValue) {
        return fields.replace(key, oldValue, newValue);
    }

    @Override
    public String replace(String key, String value) {
        return fields.replace(key, value);
    }

    @Override
    public String computeIfAbsent(String key, @Nonnull Function<? super String, ? extends String> mappingFunction) {
        return fields.computeIfAbsent(key, mappingFunction);
    }

    @Override
    public String computeIfPresent(String key, @Nonnull BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return fields.computeIfPresent(key, remappingFunction);
    }

    @Override
    public String compute(String key, @Nonnull BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return fields.compute(key, remappingFunction);
    }

    @Override
    public String merge(String key, @Nonnull String value, @Nonnull BiFunction<? super String, ? super String, ? extends String> remappingFunction) {
        return fields.merge(key, value, remappingFunction);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        DataPoint dataPoint = (DataPoint) o;
        return fields.equals(dataPoint.fields);
    }

    @Override
    public int hashCode() {
        return fields.hashCode();
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "fields=" + fields +
                '}';
    }
}
