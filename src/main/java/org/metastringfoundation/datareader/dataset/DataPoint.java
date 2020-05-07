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

package org.metastringfoundation.datareader.dataset;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public class DataPoint {
    private Map<String, String> fields;

    public DataPoint(Map<String, String> fields) {
        this.fields = fields;
    }

    public DataPoint() {
        this.fields = new HashMap<>();
    }

    public Map<String, String> getFields() {
        return fields;
    }

    public void setFields(Map<String, String> fields) {
        this.fields = fields;
    }

    public Optional<String> getValueOf(String key) {
        return Optional.ofNullable(fields.get(key));
    }

    public void setValueOf(String key, String value) {
        fields.put(key, value);
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
        return Objects.hash(fields);
    }

    @Override
    public String toString() {
        return "DataPoint{" +
                "fields=" + fields +
                '}';
    }
}
