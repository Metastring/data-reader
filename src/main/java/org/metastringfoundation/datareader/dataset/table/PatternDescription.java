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
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;
import java.util.stream.Stream;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatternDescription {
    private List<TableRangeReference> ranges;
    private String pattern;
    private String value;
    private String prefix;

    @JsonIgnore
    private Pattern compiledPattern;

    @JsonCreator
    public PatternDescription(
            @JsonProperty("range") TableRangeReference range,
            @JsonProperty("ranges") List<TableRangeReference> ranges,
            @JsonProperty("pattern") String pattern,
            @JsonProperty("value") String value,
            @JsonProperty("prefix") String prefix
    ) {
        if (range != null && ranges != null) {
            throw new IllegalArgumentException("Both range and ranges specified");
        }
        if (pattern != null && value != null) {
            throw new IllegalArgumentException("Both value and pattern specified");
        }
        if (range != null) {
            this.ranges = new ArrayList<>();
            this.ranges.add(range);
        } else {
            this.ranges = ranges;
        }
        this.pattern = pattern;
        compilePattern();
        this.value = value;
        this.prefix = prefix;
    }

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        if (pattern != null) {
            this.compiledPattern = Pattern.compile(pattern);
        }
    }

    private void compilePattern() {
        if (pattern != null) {
            compiledPattern = Pattern.compile(pattern);
        }
    }

    public Pattern getCompiledPattern() {
        return compiledPattern;
    }

    public List<TableRangeReference> getRanges() {
        return ranges;
    }

    public void setRanges(List<TableRangeReference> ranges) {
        this.ranges = ranges;
    }

    public void setRange(TableRangeReference range) {
        this.ranges = Collections.singletonList(range);
    }

    public void setRange(String range) {
        Stream.of(range)
                .map(TableRangeReference::new)
                .forEach(this::setRange);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PatternDescription that = (PatternDescription) o;
        return Objects.equals(ranges, that.ranges) &&
                Objects.equals(pattern, that.pattern) &&
                Objects.equals(value, that.value) &&
                Objects.equals(prefix, that.prefix);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranges, pattern, value, prefix);
    }

    @Override
    public String toString() {
        return "PatternDescription{" +
                "ranges=" + ranges +
                ", pattern='" + pattern + '\'' +
                ", value='" + value + '\'' +
                ", prefix='" + prefix + '\'' +
                '}';
    }
}
