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
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.metastringfoundation.data.DataPoint;
import org.metastringfoundation.data.Dataset;
import org.metastringfoundation.data.DatasetIntegrityError;
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
    private static Collection<DataPoint> expectedForSampledata;

    @BeforeAll
    static void generateExpectedForSample() {
        expectedForSampledata = new HashSet<>();

        DataPoint d1 = DataPoint.of(
            "entity.state", "Kerala",
            "entity.district", "Kannur",
            "indicator", "MMR",
            "value", "0.5"
        );
        expectedForSampledata.add(d1);

        DataPoint d2 = DataPoint.of(
            "entity.state", "Kerala",
            "entity.district", "Kannur",
            "indicator", "U5MR",
            "value", "0.6"
        );
        expectedForSampledata.add(d2);

        DataPoint d3 = DataPoint.of(
            "entity.state", "Karnataka",
            "entity.district", "Bangalore",
            "indicator", "MMR",
            "value", "1"
        );
        expectedForSampledata.add(d3);

        DataPoint d4 = DataPoint.of(
            "entity.state", "Karnataka",
            "entity.district", "Bangalore",
            "indicator", "U5MR",
            "value", "1.2"
        );
        expectedForSampledata.add(d4);
    }

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

        assert (elements.containsAll(expectedForSampledata));
        assert (expectedForSampledata.containsAll(elements));
    }

    @Test
    void complicatedFieldsTest() throws DatasetIntegrityError, IOException {
        String csvPath = this.getClass().getResource("splitCells.csv").getPath();
        String descriptionPath = this.getClass().getResource("splitCells.metadata.json").getPath();

        Table table = CSVTable.fromPath(csvPath);
        TableDescription tableDescription = TableDescription.fromPath(descriptionPath);

        Dataset dataset = new TableToDatasetAdapter(table, tableDescription);
        Collection<DataPoint> elements = dataset.getData();
        System.out.println(elements);
        assert (elements.containsAll(expectedForSampledata));
        assert (expectedForSampledata.containsAll(elements));
    }
}