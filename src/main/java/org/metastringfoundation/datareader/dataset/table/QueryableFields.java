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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.metastringfoundation.data.DatasetIntegrityError;
import org.metastringfoundation.datareader.dataset.utils.RegexHelper;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.metastringfoundation.datareader.helpers.StringUtils.getValueOrDefault;

public class QueryableFields {
    private static final Logger LOG = LogManager.getLogger(QueryableFields.class);
    private final List<FieldDescription> fields;
    private final Table table;
    private final Map<Integer, List<FieldData>> rowsAndTheirFields = new HashMap<>();
    private final Map<Integer, List<FieldData>> columnsAndTheirFields = new HashMap<>();
    private final List<FieldData> universalFields = new ArrayList<>();
    private final Collection<TableCell> valueCells = new HashSet<>();

    public QueryableFields(List<FieldDescription> fields, Table table) throws DatasetIntegrityError {
        this.fields = fields;
        this.table = table;
        calculateFieldValues();
    }

    private void calculateFieldValues() throws DatasetIntegrityError {
        for (FieldDescription fieldDescription : fields) {
            if (fieldDescription.getField().equals("value")) {
                // value is a special field and needs to be handled separately
                saveValues(fieldDescription);
            } else if (fieldDescription.getPatterns() != null) {
                processFieldWithPattern(fieldDescription);
            } else if (fieldDescription.getValue() != null) {
                // there is a hardcoded value, and that can be applied to the entire table
                processHardCodedValueWithoutRange(fieldDescription);
            } else {
                LOG.info("Unusable field: " + fieldDescription.getField());
            }
        }
    }

    private void processFieldWithPattern(FieldDescription fieldDescription) throws DatasetIntegrityError {
        for (FieldRangesPatternPair pattern : fieldDescription.getPatterns()) {
            processPattern(fieldDescription, pattern);
        }
    }

    private void processHardCodedValueWithoutRange(FieldDescription fieldDescription) {
        String fieldName = fieldDescription.getField();
        String fieldHardcodedValue = fieldDescription.getValue();
        universalFields.add(new FieldData(fieldName, fieldHardcodedValue));
    }

    private void processPattern(FieldDescription fieldDescription, FieldRangesPatternPair patternDescription) throws DatasetIntegrityError {
        for (TableRangeReference range : patternDescription.getRanges()) {
            TableRangeReference.RangeType rangeType = range.getRangeType();

            if (rangeType == TableRangeReference.RangeType.ROW_AND_COLUMN) {
                throw new DatasetIntegrityError("Only value can be in both column and row");
            }

            if (rangeType == TableRangeReference.RangeType.COLUMN_ONLY || rangeType == TableRangeReference.RangeType.SINGLE_CELL) {
                // the fields are written in a column. That means, their values will be applicable to rows.
                registerFieldToIndex(fieldDescription, patternDescription.getCompiledPattern(), range, TableCell::getRow, rowsAndTheirFields);
            }

            if (rangeType == TableRangeReference.RangeType.ROW_ONLY || rangeType == TableRangeReference.RangeType.SINGLE_CELL) {
                // the fields are written in a row. That means, their values will be applicable to columns.
                registerFieldToIndex(fieldDescription, patternDescription.getCompiledPattern(), range, TableCell::getColumn, columnsAndTheirFields);
            }
        }
    }

    private void saveValues(FieldDescription field) {
        List<TableCell> cells = field.getPatterns().stream()
                .map(FieldRangesPatternPair::getRanges)
                .flatMap(List::stream)
                .map(table::getRange)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        valueCells.addAll(cells);
    }

    private void registerFieldToIndex(
            FieldDescription fieldDescription,
            Pattern pattern,
            TableRangeReference range,
            Function<TableCell, Integer> indexFinder,
            Map<Integer, List<FieldData>> register
    ) {
        String fieldName = fieldDescription.getField();
        String fieldValueOverride = fieldDescription.getValue();
        String fieldValuePrefix = fieldDescription.getPrefix();

        List<TableCell> cellsOfTheField = table.getRange(range);

        for (TableCell cell : cellsOfTheField) {

            String parsedWithRegex = parseFieldWithPossibleRegex(pattern, cell);

            String fieldResultantValue = getValueOrDefault(
                    fieldValueOverride,
                    parseFieldWithPossibleRegex(pattern, cell)
            );

            if (fieldResultantValue == null) {
                continue;
            }
            fieldResultantValue = prepend(fieldResultantValue, fieldValuePrefix);

            FieldData fieldData = new FieldData(fieldName, fieldResultantValue);
            Integer index = indexFinder.apply(cell);

            // https://stackoverflow.com/a/3019388/589184 for what computeIfAbsent does
            register.computeIfAbsent(index, k -> new ArrayList<>()).add(fieldData);
        }
    }

    private String prepend(String fieldResultantValue, @Nullable String fieldValuePrefix) {
        if (fieldValuePrefix == null) {
            return fieldResultantValue;
        } else {
            return fieldValuePrefix.concat(fieldResultantValue);
        }
    }

    private String parseFieldWithPossibleRegex(Pattern pattern, TableCell cell) {
        String rawCellValue = cell.getValue();
        if (pattern == null) {
            return rawCellValue;
        } else {
            return RegexHelper.getFirstMatchOrNull(rawCellValue, pattern);
        }
    }

    public Map<String, String> queryFieldsAt(int row, int column) {
        Map<String, String> fieldsAtThisCell = new HashMap<>();

        if (rowsAndTheirFields.containsKey(row)) {
            stashInto(fieldsAtThisCell, rowsAndTheirFields.get(row));
        }

        if (columnsAndTheirFields.containsKey(column)) {
            stashInto(fieldsAtThisCell, columnsAndTheirFields.get(column));
        }

        universalFields.forEach(fieldData -> fieldsAtThisCell.put(fieldData.getName(), fieldData.getValue()));

        return fieldsAtThisCell;
    }

    private void stashInto(Map<String, String> targetMap, List<FieldData> fields) {
        targetMap.putAll(
                fields.stream().collect(toMap(FieldData::getName, FieldData::getValue))
        );
    }

    public Collection<TableCell> getValueCells() {
        return valueCells;
    }
}
