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

import org.metastringfoundation.data.DatasetIntegrityError;
import org.metastringfoundation.datareader.dataset.utils.RegexHelper;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static org.metastringfoundation.datareader.helpers.StringUtils.getValueOrDefault;

public class QueryableFields {
    private final List<FieldDescription> fields;
    private final Table table;
    private final Map<Integer, List<FieldData>> rowsAndTheirFields = new HashMap<>();
    private final Map<Integer, List<FieldData>> columnsAndTheirFields = new HashMap<>();
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
                continue;
            }
            if (fieldDescription.getPatterns() != null) {
                for (FieldRangesPatternPair pattern : fieldDescription.getPatterns()) {
                    processPattern(fieldDescription, pattern);
                }
            }

        }
    }

    private void processPattern(FieldDescription fieldDescription, FieldRangesPatternPair patternDescription) throws DatasetIntegrityError {
        String fieldName = fieldDescription.getField();
        String fieldValue = fieldDescription.getValue();
        for (TableRangeReference range : patternDescription.getRanges()) {
            TableRangeReference.RangeType rangeType = range.getRangeType();

            if (rangeType == TableRangeReference.RangeType.ROW_AND_COLUMN) {
                throw new DatasetIntegrityError("Only value can be in both column and row");
            }

            if (rangeType == TableRangeReference.RangeType.COLUMN_ONLY || rangeType == TableRangeReference.RangeType.SINGLE_CELL) {
                // the fields are written in a column. That means, their values will be applicable to rows.
                registerFieldToIndex(fieldName, fieldValue, patternDescription.getCompiledPattern(), range, TableCell::getRow, rowsAndTheirFields);
            }

            if (rangeType == TableRangeReference.RangeType.ROW_ONLY || rangeType == TableRangeReference.RangeType.SINGLE_CELL) {
                // the fields are written in a row. That means, their values will be applicable to columns.
                registerFieldToIndex(fieldName, fieldValue, patternDescription.getCompiledPattern(), range, TableCell::getColumn, columnsAndTheirFields);
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
            String fieldName,
            String fieldValue,
            Pattern pattern,
            TableRangeReference range,
            Function<TableCell, Integer> indexFinder,
            Map<Integer, List<FieldData>> register
    ) {
        List<TableCell> cellsOfTheField = table.getRange(range);

        for (TableCell cell : cellsOfTheField) {
            String fieldValueInThisCell = getValueOrDefault(
                    fieldValue,
                    parseField(pattern, cell)
            );

            if (fieldValueInThisCell == null) {
                continue;
            }
            FieldData fieldData = new FieldData(fieldName, fieldValueInThisCell);
            Integer index = indexFinder.apply(cell);

            // https://stackoverflow.com/a/3019388/589184 for what computeIfAbsent does
            register.computeIfAbsent(index, k -> new ArrayList<>()).add(fieldData);
        }
    }

    private String parseField(Pattern pattern, TableCell cell) {
        String rawCellValue = cell.getValue();
        if (pattern == null) {
            return rawCellValue;
        } else {
            return RegexHelper.getFirstMatchOrNull(rawCellValue, pattern);
        }
    }

    public Map<String, String> queryFieldsAt(TableCell cell) {
        Map<String, String> fieldsAtThisCell = new HashMap<>();

        int rowOfThisCell = cell.getRow();
        if (rowsAndTheirFields.containsKey(rowOfThisCell)) {
            stashInto(fieldsAtThisCell, rowsAndTheirFields.get(rowOfThisCell));
        }

        int columnOfThisCell = cell.getColumn();
        if (columnsAndTheirFields.containsKey(columnOfThisCell)) {
            stashInto(fieldsAtThisCell, columnsAndTheirFields.get(columnOfThisCell));
        }

        fieldsAtThisCell.put("value", cell.getValue());
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
