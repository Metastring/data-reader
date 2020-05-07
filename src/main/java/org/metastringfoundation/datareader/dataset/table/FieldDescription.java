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

import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.List;

public class FieldDescription {
    private TableRangeReference range;
    private String field;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<String> replacements;

    public TableRangeReference getRange() {
        return range;
    }

    public void setRange(TableRangeReference range) {
        this.range = range;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public List<String> getReplacements() {
        return replacements;
    }

    public void setReplacements(List<String> replacements) {
        this.replacements = replacements;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        FieldDescription other = (FieldDescription) obj;
        return (other.getRange().equals(this.getRange())
                && other.getField().equals(this.getField())
        );
    }

    @Override
    public String toString() {
        return "FieldDescription{" +
                "range=" + range +
                ", field='" + field + '\'' +
                '}';
    }
}
