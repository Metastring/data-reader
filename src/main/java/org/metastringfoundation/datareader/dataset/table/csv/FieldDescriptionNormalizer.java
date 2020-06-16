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

package org.metastringfoundation.datareader.dataset.table.csv;

import org.metastringfoundation.datareader.dataset.table.FieldDescription;
import org.metastringfoundation.datareader.dataset.table.FieldDescriptionIntelligent;
import org.metastringfoundation.datareader.dataset.table.TableRangeReference;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FieldDescriptionNormalizer {
    public static List<FieldDescription> normalize(List<FieldDescriptionIntelligent> input) {
        List<FieldDescription> result = new ArrayList<>();
        for (FieldDescriptionIntelligent fieldDescription : input) {
            if (fieldDescription.getRanges() == null) {
                result.add(getNormalizedFieldFrom(fieldDescription, fieldDescription.getRange()));
            } else {
                result.addAll(splitRangesIntoMultipleFields(fieldDescription));
            }
        }
        return result;
    }

    private static List<FieldDescription> splitRangesIntoMultipleFields(FieldDescriptionIntelligent original) {
        return original.getRanges().stream()
                .map(range -> getNormalizedFieldFrom(original, range))
                .collect(Collectors.toList());
    }

    private static FieldDescription getNormalizedFieldFrom(FieldDescriptionIntelligent original, TableRangeReference range) {
        FieldDescription converted = new FieldDescription();
        converted.setField(original.getField());
        converted.setPattern(original.getPattern());
        converted.setReplacements(original.getReplacements());
        converted.setRange(range);
        return converted;
    }
}
