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

package org.metastringfoundation.datareader.dataset.table;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldDescription {
    private String field;
    private List<PatternDescription> patterns;

    public FieldDescription() {
    }

    @JsonCreator
    public FieldDescription(
            @JsonProperty("field") String field,
            @JsonProperty("patterns") List<PatternDescription> patterns,
            @JsonProperty("range") TableRangeReference range,
            @JsonProperty("ranges") List<TableRangeReference> ranges,
            @JsonProperty("pattern") String pattern,
            @JsonProperty("value") String value,
            @JsonProperty("prefix") String prefix
    ) {
        if (ranges != null && range != null) {
            throw new IllegalArgumentException("Both ranges and range specified");
        }

        if (patterns != null && value != null) {
            throw new IllegalArgumentException("Both patterns and value specified. Use 'ranges' to specify multiple ranges");
        }

        this.field = field;
        this.patterns = patterns;

        if (patterns == null) {
            this.patterns = new ArrayList<>();
            PatternDescription patternDescription = new PatternDescription(range, ranges, pattern, value, prefix);
            this.patterns.add(patternDescription);
        }
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<PatternDescription> getPatterns() {
        return patterns;
    }

    public void setPatterns(List<PatternDescription> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDescription that = (FieldDescription) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(patterns, that.patterns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, patterns);
    }

    @Override
    public String toString() {
        return "FieldDescription{" +
                "field='" + field + '\'' +
                ", patterns=" + patterns +
                '}';
    }
}
