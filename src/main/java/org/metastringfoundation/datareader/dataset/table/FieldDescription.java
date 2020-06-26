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

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldDescription {
    private String field;
    private String value;

    private String prefix;

    @JsonIgnore
    private FieldRangesPatternPair singlePatternInRoot;

    private List<FieldRangesPatternPair> patterns;

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    public void setRanges(List<TableRangeReference> ranges) {
        if (singlePatternInRoot == null) {
            singlePatternInRoot = new FieldRangesPatternPair();
        }
        singlePatternInRoot.setRanges(ranges);
    }

    public void setRange(TableRangeReference range) {
        setRanges(Collections.singletonList(range));
    }

    public void setPattern(String pattern) {
        if (singlePatternInRoot == null) {
            singlePatternInRoot = new FieldRangesPatternPair();
        }
        singlePatternInRoot.setPattern(pattern);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public List<FieldRangesPatternPair> getPatterns() {
        if (singlePatternInRoot == null) {
            return patterns;
        }
        if (patterns == null) {
            return Collections.singletonList(singlePatternInRoot);
        }
        // return a concatenated list
        return Stream.of(Collections.singletonList(singlePatternInRoot), patterns)
                .flatMap(List::stream)
                .collect(Collectors.toList());
    }

    public void setPatterns(List<FieldRangesPatternPair> patterns) {
        this.patterns = patterns;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldDescription that = (FieldDescription) o;
        return Objects.equals(field, that.field) &&
                Objects.equals(value, that.value) &&
                Objects.equals(prefix, that.prefix) &&
                Objects.equals(patterns, that.patterns);
    }

    @Override
    public int hashCode() {
        return Objects.hash(field, value, prefix, patterns);
    }

    @Override
    public String toString() {
        return "FieldDescription{" +
                "field='" + field + '\'' +
                ", value='" + value + '\'' +
                ", prefix='" + prefix + '\'' +
                ", singlePatternInRoot=" + singlePatternInRoot +
                ", patterns=" + patterns +
                '}';
    }
}
