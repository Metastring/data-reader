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
import org.metastringfoundation.data.DataPoint;
import org.metastringfoundation.data.DatasetIntegrityError;
import org.metastringfoundation.datareader.dataset.table.csv.CSVTable;

import java.io.IOException;
import java.util.List;

class TableToDatasetAdapterTest {

    @Test
    void of() throws IOException, DatasetIntegrityError {
        String tableText = """
                a,source,c
                p,Source 1,2
                q,Source 2,4
                """.stripIndent();
        String descriptionText = """
                {
                    "fields": [
                        {
                            "field": "y",
                            "range": "A2:A"
                        }, {
                            "field": "x",
                            "range": "C1:1"
                        }, {
                            "field": "source",
                            "range": "B2:B" 
                        }, {
                            "field": "value",
                            "range": "C2:" 
                        }
                    ]
                }
                """.stripIndent();
        String rootDescriptionText = """
                {
                    "fields": [
                        {
                            "field": "source",
                            "value": "Test Source"
                        }
                    ]
                }
                """.stripIndent();
        Table table = new CSVTable(tableText);
        TableDescription tableDescription2 = TableDescription.fromString(descriptionText);
        TableDescription tableDescription1 = TableDescription.fromString(rootDescriptionText);

        List<DataPoint> expected = List.of(
                DataPoint.of("x", "c", "y", "p", "source", "Source 1", "value", "2"),
                DataPoint.of("x", "c", "y", "q", "source", "Source 2", "value", "4")
        );
        List<DataPoint> actual = TableToDatasetAdapter.of(table, List.of(tableDescription1, tableDescription2)).getData();
        assert (expected.containsAll(actual));
        assert (actual.containsAll(expected));
    }

    @Test
    void getData() throws IOException, DatasetIntegrityError {
        String tableText = """
                a,b,c
                p,1,2
                q,3,4
                """.stripIndent();
        String descriptionText = """
                {
                    "fields": [
                        {
                            "field": "y",
                            "range": "A2:A"
                        }, {
                            "field": "x",
                            "range": "B1:1"
                        }, {
                            "field": "value",
                            "range": "B2:" 
                        }
                    ]
                }
                """.stripIndent();
        Table table = new CSVTable(tableText);
        TableDescription tableDescription = TableDescription.fromString(descriptionText);
        List<DataPoint> expected = List.of(
                DataPoint.of("x", "b", "y", "p", "value", "1"),
                DataPoint.of("x", "c", "y", "p", "value", "2"),
                DataPoint.of("x", "b", "y", "q", "value", "3"),
                DataPoint.of("x", "c", "y", "q", "value", "4")
        );
        List<DataPoint> actual = new TableToDatasetAdapter(table, tableDescription).getData();
        assert (expected.containsAll(actual));
        assert (actual.containsAll(expected));
    }
}