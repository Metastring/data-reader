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
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CSVReadingTest {
    private static List<DataPoint> expectedForSampledata;

    @BeforeAll
    static void generateExpectedForSample() {
        expectedForSampledata = new ArrayList<>();

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
        FieldDescription range1 = new FieldDescription("indicator", null, new TableRangeReference("A1:B2"), null, null, null, null);
        rangeDescriptionList.add(range1);

        tableDescription.setFieldDescriptionList(rangeDescriptionList);

        String json = Jsonizer.asJSON(tableDescription);

        String expectedJson = "{\"fields\":[{\"field\":\"indicator\",\"patterns\":[{\"ranges\":[{\"startingCell\":{\"row\":0,\"column\":0},\"endingCell\":{\"row\":1,\"column\":1}}]}]}]}";
        assertEquals(expectedJson, json);
    }

    @Test()
    void correctlyReadsJSON() throws IOException {
        String jsonFileName = "ahs.test.json";
        String path = this.getClass().getResource(jsonFileName).getPath();
        TableDescription description = TableDescription.fromPath(path);

        FieldDescription range = new FieldDescription("indicator", null, new TableRangeReference("A1:B2"), null, null, null, null);
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
        List<DataPoint> elements = csvDataset.getData();

        assert (elements.containsAll(expectedForSampledata));
        assert (expectedForSampledata.containsAll(elements));
    }

    @Test
    void endToEndTest2() throws IOException, DatasetIntegrityError {
        String csvPath = this.getClass().getResource("sampleDataWithMissingPattern.csv").getPath();
        String descriptionPath = this.getClass().getResource("sampleDataWithMissingPattern.metadata.json").getPath();

        Table table = CSVTable.fromPath(csvPath);
        TableDescription tableDescription = TableDescription.fromPath(descriptionPath);

        Dataset csvDataset = new TableToDatasetAdapter(table, tableDescription);
        List<DataPoint> elements = csvDataset.getData();

        List<DataPoint> expected = new ArrayList<>(Arrays.asList(
                DataPoint.of(
                        "entity.state", "Kerala",
                        "entity.district", "Kannur",
                        "indicator", "MMR",
                        "settlement", "Urban",
                        "value", "0.5"
                ), DataPoint.of(
                        "entity.state", "Kerala",
                        "entity.district", "Kannur",
                        "indicator", "U5MR",
                        "value", "0.6"
                ), DataPoint.of(
                        "entity.state", "Karnataka",
                        "entity.district", "Bangalore",
                        "indicator", "MMR",
                        "settlement", "Urban",
                        "value", "1"
                ), DataPoint.of(
                        "entity.state", "Karnataka",
                        "entity.district", "Bangalore",
                        "indicator", "U5MR",
                        "value", "1.2"
                )
        ));

        System.out.println(elements);
        assert (expected.containsAll(elements));
        assert (elements.containsAll(expected));
    }

    @Test
    void complicatedFieldsTest() throws DatasetIntegrityError, IOException {
        String csvPath = this.getClass().getResource("splitCells.csv").getPath();
        String descriptionPath = this.getClass().getResource("splitCells.metadata.json").getPath();

        Table table = CSVTable.fromPath(csvPath);
        TableDescription tableDescription = TableDescription.fromPath(descriptionPath);

        Dataset dataset = new TableToDatasetAdapter(table, tableDescription);
        List<DataPoint> elements = dataset.getData();
        System.out.println(elements);
        assert (elements.containsAll(expectedForSampledata));
        assert (expectedForSampledata.containsAll(elements));
    }

    @Test
    void patternsWithoutPattern() throws DatasetIntegrityError, IOException {
        Table table = new CSVTable("""
                State,District,Maternal Mortality Rate - Urban,Maternal Mortality Rate - Rural,Infant Mortality Rate
                Karnataka,Bangalore Urban,1.3,NA,0.5
                Karnataka,Mysore,1.5,1.6,0.4
                """);
        TableDescription tableDescription = TableDescription.fromString("""
                {
                  "fields": [
                    {
                      "field": "entity.state",
                      "range": "A2:A"
                    },
                    {
                      "field": "entity.district",
                      "range": "B2:B"
                    },
                    {
                      "field": "indicator",
                      "patterns": [
                        {
                          "range": "C1:D1",
                          "pattern": "(.*) - .*"
                        },
                        {
                	      "range": "E1"
                	    }
                      ]
                    },
                    {
                      "field": "settlement",
                      "range": "C1:D1",
                      "pattern": ".* - (.*)"
                    },
                    {
                      "field": "value",
                      "range": "C2:"
                    }
                  ]
                }
                """);

        List<DataPoint> obtained = new TableToDatasetAdapter(table, tableDescription).getData();
        assert (obtained.contains(
                DataPoint.of(
                        "entity.state", "Karnataka",
                        "entity.district", "Mysore",
                        "indicator", "Infant Mortality Rate",
                        "value", "0.4")
        ));
    }
}