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

import org.junit.jupiter.api.Test;
import org.metastringfoundation.data.DatasetIntegrityError;
import org.metastringfoundation.datareader.dataset.table.csv.CSVTable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QueryableFieldsTest {

    @Test
    void queryFieldsAtCorrectlyIncludesPrefix() throws IOException, DatasetIntegrityError {
        Table table = new CSVTable("district,mmr,u5mr\nkannur,2,3\nkozhikode,3,4");
        List<FieldDescription> fields = new ArrayList<>();
        FieldDescription field1 = new FieldDescription();
        field1.setField("indicator");
        field1.setRange(new TableRangeReference("B1:1"));
        field1.setPrefix("IND - ");
        fields.add(field1);

        QueryableFields queryableFields = new QueryableFields(fields, table);

        Map<String, String> actual = queryableFields.queryFieldsAt(2 - 1, 2 - 1); // 0 based indexes
        Map<String, String> expected = new HashMap<>();
        expected.put("indicator", "IND - mmr");
        assertEquals(expected, actual);

    }
}