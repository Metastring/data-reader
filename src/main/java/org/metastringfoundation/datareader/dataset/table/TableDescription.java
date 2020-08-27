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
import com.fasterxml.jackson.annotation.JsonProperty;
import org.metastringfoundation.datareader.helpers.FileManager;
import org.metastringfoundation.datareader.helpers.Jsonizer;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiFunction;

public class TableDescription {
    public static TableDescription fromPath(Path path) throws IOException {
        return fromPath(path.toString());
    }

    public static TableDescription fromPath(String path) throws IOException {
        String description = FileManager.getFileContentsAsString(path);
        return fromString(description);
    }

    public static TableDescription fromString(String jsonString) throws IOException {
        return (TableDescription) Jsonizer.fromJSON(jsonString, TableDescription.class);
    }

    public static TableDescription ofFields(List<FieldDescription> fieldDescriptions) {
        TableDescription description = new TableDescription();
        description.setFieldDescriptionList(fieldDescriptions);
        return description;
    }

    public static TableDescription add(TableDescription first, TableDescription second) {
        BiFunction<Object, Object, Object> replaceWithSecond = (firstObj, secondObj) -> secondObj;
        return add(first, second, replaceWithSecond);
    }

    public static TableDescription add(TableDescription first, TableDescription second, BiFunction<Object, Object, Object> metadataAddFunction) {
        if (first == null) {
            return second;
        }
        if (second == null) {
            return first;
        }
        TableDescription sum = new TableDescription();

        List<FieldDescription> summedFieldDescriptions = new ArrayList<>();
        summedFieldDescriptions.addAll(first.getFieldDescriptionList());
        summedFieldDescriptions.addAll(second.getFieldDescriptionList());
        sum.setFieldDescriptionList(summedFieldDescriptions);

        sum.setMetadata(metadataAddFunction.apply(first.metadata, second.metadata));

        return sum;
    }

    @JsonProperty("fields")
    private List<FieldDescription> fieldDescriptionList;

    public List<FieldDescription> getFieldDescriptionList() {
        return fieldDescriptionList;
    }

    public void setFieldDescriptionList(List<FieldDescription> fieldDescriptionList) {
        this.fieldDescriptionList = fieldDescriptionList;
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Object metadata;

    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TableDescription that = (TableDescription) o;
        return Objects.equals(fieldDescriptionList, that.fieldDescriptionList) &&
                Objects.equals(metadata, that.metadata);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fieldDescriptionList, metadata);
    }

    @Override
    public String toString() {
        return "TableDescription{" +
                "fieldDescriptionList=" + fieldDescriptionList +
                ", metadata=" + metadata +
                '}';
    }
}
