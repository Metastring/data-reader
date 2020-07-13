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
import org.metastringfoundation.datareader.helpers.Jsonizer;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class FieldDescriptionTest {
    @Test
    public void deserializePatternInTheRoot() throws IOException {
        String fieldDescription = """
                {
                        "range": "A1",
                        "field": "patternrange",
                        "pattern": "(.*)"
                }
                """;
        FieldDescription actual = (FieldDescription) Jsonizer.fromJSON(fieldDescription, FieldDescription.class);
        FieldDescription expected = new FieldDescription();
        expected.setField("patternrange");
        expected.setPatterns(List.of(new PatternDescription(new TableRangeReference("A1"), null, "(.*)", null, null)));

        assertEquals(expected, actual);
    }

    @Test
    public void deserializeValueInTheRoot() throws IOException {
        String fieldDescription = """
                {
                     "range": "A2",
                     "field": "patternvalue",
                     "value": "patternvaluevalue"
                }
                """;
        FieldDescription actual = (FieldDescription) Jsonizer.fromJSON(fieldDescription, FieldDescription.class);
        FieldDescription expected = new FieldDescription();
        expected.setField("patternvalue");
        expected.setPatterns(List.of(new PatternDescription(new TableRangeReference("A2"), null, null, "patternvaluevalue", null)));

        assertEquals(expected, actual);
    }

    @Test
    public void deserializeMultipleRanges() throws IOException {
        String fieldDescription = """
                {
                    "field": "plainranges",
                    "ranges": ["C2", "D3"],
                    "pattern": "(.*)"
                }
                """;
        FieldDescription actual = (FieldDescription) Jsonizer.fromJSON(fieldDescription, FieldDescription.class);
        var field = "plainranges";
        var ranges = List.of(new TableRangeReference("C2"), new TableRangeReference("D3"));
        var pattern = "(.*)";
        FieldDescription expected = new FieldDescription(field, null, null, ranges, pattern, null, null);
        assertEquals(expected, actual);
    }

    @Test
    public void deserializeMultipleRangesNested() throws IOException {
        String fieldDescription = """
                {
                     "field": "nestedranges",
                     "patterns": [
                         {
                             "range": "A2",
                             "value": "nestedrangevalue"
                         }, {
                             "ranges": ["A3", "A5"],
                             "pattern": "(.*)"
                         }
                     ]
                 }
                """;
        FieldDescription actual = (FieldDescription) Jsonizer.fromJSON(fieldDescription, FieldDescription.class);
        FieldDescription expected = new FieldDescription();
        expected.setField("nestedranges");
        PatternDescription range1 = new PatternDescription(new TableRangeReference("A2"), null, null, "nestedrangevalue", null);
        PatternDescription range2 = new PatternDescription(null, List.of(new TableRangeReference("A3"), new TableRangeReference("A5")), "(.*)", null, null);
        var ranges = List.of(range1, range2);
        expected.setPatterns(ranges);
        assertEquals(expected, actual);
    }

}