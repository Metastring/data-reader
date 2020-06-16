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
import java.util.regex.Pattern;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FieldRangesPatternPair {

    private List<TableRangeReference> ranges;
    private TableRangeReference range;
    private String pattern;

    @JsonIgnore
    private Pattern compiledPattern;

    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
        if (pattern != null) {
            this.compiledPattern = Pattern.compile(pattern);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldRangesPatternPair that = (FieldRangesPatternPair) o;
        return Objects.equals(ranges, that.ranges) &&
                Objects.equals(pattern, that.pattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ranges, pattern);
    }

    @Override
    public String toString() {
        return "FieldRangesPatternPair{" +
                "ranges=" + ranges +
                ", pattern='" + pattern + '\'' +
                '}';
    }
}
