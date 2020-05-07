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

import com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.metastringfoundation.datareader.dataset.table.*;
import org.metastringfoundation.datareader.dataset.table.csv.CSVTable;
import org.metastringfoundation.datareader.helpers.Jsonizer;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVReadingTest {

    @Test
    void correctlySerializesDescription() throws JsonProcessingException {
        TableDescription tableDescription = new TableDescription();

        List<FieldDescription> rangeDescriptionList = new ArrayList<>();
        FieldDescription range1 = new FieldDescription();
        range1.setField("indicator");
        range1.setRange(new TableRangeReference("A1:B2"));
        rangeDescriptionList.add(range1);

        tableDescription.setFieldDescriptionList(rangeDescriptionList);

        String json = Jsonizer.asJSON(tableDescription);

        String expectedJson = "{\"fields\":[{\"range\":{\"startingCell\":{\"row\":0,\"column\":0},\"endingCell\":{\"row\":1,\"column\":1}},\"field\":\"indicator\"}]}";
        assertEquals(expectedJson, json);
    }

    @Test()
    void correctlyReadsJSON() throws IOException {
        String jsonFileName = "ahs.test.json";
        String path = this.getClass().getResource(jsonFileName).getPath();
        TableDescription description = TableDescription.fromPath(path);

        FieldDescription range = new FieldDescription();
        range.setRange(new TableRangeReference("A1:B2"));
        range.setField("indicator");
        List<FieldDescription> fieldDescriptionList = new ArrayList<>();
        fieldDescriptionList.add(range);

        TableDescription expectedDescription = new TableDescription();
        expectedDescription.setFieldDescriptionList(fieldDescriptionList);

        assertEquals(expectedDescription, description);
    }

    @Test
    void endToEndTest() throws IOException, DatasetIntegrityError {
        String csv = IOUtils.toString(
                this.getClass().getResourceAsStream("sampleData.csv"),
                StandardCharsets.UTF_8
        );
        String csvPath = this.getClass().getResource("sampleData.csv").getPath();
        String descriptionPath = this.getClass().getResource("sampleData.description.json").getPath();

        Table table = CSVTable.fromPath(csvPath);
        TableDescription tableDescription = TableDescription.fromPath(descriptionPath);

        Dataset csvDataset = new TableToDatasetAdapter(table, tableDescription);
        Collection<DataPoint> elements = csvDataset.getData();
        Collection<DataPoint> expectedElements = new HashSet<>();

        DataPoint d1 = new DataPoint();
        d1.setValueOf("entity.state", "Kerala");
        d1.setValueOf("entity.district", "Kannur");
        d1.setValueOf("indicator", "MMR");
        d1.setValueOf("value", "0.5");
        expectedElements.add(d1);

        DataPoint d2 = new DataPoint();
        d2.setValueOf("entity.state", "Kerala");
        d2.setValueOf("entity.district", "Kannur");
        d2.setValueOf("indicator", "U5MR");
        d2.setValueOf("value", "0.6");
        expectedElements.add(d2);

        DataPoint d3 = new DataPoint();
        d3.setValueOf("entity.state", "Karnataka");
        d3.setValueOf("entity.district", "Bangalore");
        d3.setValueOf("indicator", "MMR");
        d3.setValueOf("value", "1");
        expectedElements.add(d3);

        DataPoint d4 = new DataPoint();
        d4.setValueOf("entity.state", "Karnataka");
        d4.setValueOf("entity.district", "Bangalore");
        d4.setValueOf("indicator", "U5MR");
        d4.setValueOf("value", "1.2");
        expectedElements.add(d4);

        assert(elements.containsAll(expectedElements));
        assert(expectedElements.containsAll(elements));
    }
}