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

import com.google.common.collect.Maps;
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
            } else {
                LOG.info("Unusable field: " + fieldDescription.getField());
            }
        }
    }

    private void processFieldWithPattern(FieldDescription fieldDescription) throws DatasetIntegrityError {
        for (PatternDescription pattern : fieldDescription.getPatterns()) {
            processPattern(fieldDescription, pattern);
        }
    }

    private void processHardCodedValueWithoutRange(FieldDescription fieldDescription, PatternDescription patternDescription) {
        String fieldName = fieldDescription.getField();
        String fieldHardcodedValue = patternDescription.getValue();
        if (fieldHardcodedValue == null) {
            throw new IllegalArgumentException("Field with neither value nor pattern");
        }
        universalFields.add(new FieldData(fieldName, fieldHardcodedValue));
    }

    private void processPattern(FieldDescription fieldDescription, PatternDescription patternDescription) throws DatasetIntegrityError {
        LOG.debug("\n\nProcessing " + patternDescription);
        if (patternDescription.getRanges() == null) {
            processHardCodedValueWithoutRange(fieldDescription, patternDescription);
        } else {
            for (TableRangeReference range : patternDescription.getRanges()) {
                TableRangeReference.RangeType rangeType = range.getRangeType();

                if (rangeType == TableRangeReference.RangeType.ROW_AND_COLUMN) {
                    throw new DatasetIntegrityError("Only value can be in both column and row");
                }

                if (rangeType == TableRangeReference.RangeType.COLUMN_ONLY || rangeType == TableRangeReference.RangeType.SINGLE_CELL) {
                    // the fields are written in a column. That means, their values will be applicable to rows.
                    Map<TableCellReference, String> values = calculatePatternValues(patternDescription);
                    registerFieldToIndex(values, fieldDescription.getField(), rowsAndTheirFields, TableCellReference::getRow);
                }

                if (rangeType == TableRangeReference.RangeType.ROW_ONLY || rangeType == TableRangeReference.RangeType.SINGLE_CELL) {
                    // the fields are written in a row. That means, their values will be applicable to columns.
                    Map<TableCellReference, String> values = calculatePatternValues(patternDescription);
                    registerFieldToIndex(values, fieldDescription.getField(), columnsAndTheirFields, TableCellReference::getColumn);
                }
            }
        }
    }

    private void registerFieldToIndex(Map<TableCellReference, String> values, String
            field, Map<Integer, List<FieldData>> indexAndTheirFields, Function<TableCellReference, Integer> getIndex) {
        values.entrySet().stream()
                .map(e -> createFieldEntryFrom(e, field, getIndex))
                .forEach(e -> {
                    // https://stackoverflow.com/a/3019388/589184 for what computeIfAbsent does
                    indexAndTheirFields.computeIfAbsent(e.getKey(), k -> new ArrayList<>()).add(e.getValue());
                });
    }

    private Map.Entry<Integer, FieldData> createFieldEntryFrom
            (Map.Entry<TableCellReference, String> input, String field, Function<TableCellReference, Integer> getIndex) {
        return Maps.immutableEntry(getIndex.apply(input.getKey()), new FieldData(field, input.getValue()));
    }

    private Map<TableCellReference, String> calculatePatternValues(PatternDescription patternDescription) {
        LOG.debug(patternDescription);
        return patternDescription.getRanges().stream()
                .map(table::getRange)
                .flatMap(List::stream)
                .peek(LOG::debug)
                .map(cell -> getValueOfOneCell(cell, patternDescription))
                .peek(e -> LOG.debug(e.getKey() + ": " + e.getValue()))
                .filter(e -> {
                    if (e.getValue() != null) return true;
                    LOG.info("No value at " + e.getKey().toString() + ", although specified " + patternDescription);
                    return false;
                })
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Map.Entry<TableCellReference, String> getValueOfOneCell(TableCell cell, PatternDescription
            patternDescription) {
        TableCellReference key = new TableCellReference(cell.getRow(), cell.getColumn());
        String value;
        if (patternDescription.getValue() != null) {
            value = patternDescription.getValue();
            LOG.debug("Assigned value from pattern hardcoded: " + value);
        } else if (patternDescription.getCompiledPattern() != null) {
            value = parseFieldWithPossibleRegex(patternDescription.getCompiledPattern(), cell);
            LOG.debug("Assigned value from regex: " + value);
        } else {
            value = cell.getValue();
            LOG.debug("Assigned value from cell: " + value);
        }
        value = prepend(value, patternDescription.getPrefix());
        LOG.debug("Prepended prefix. Value now is " + value);
        return Maps.immutableEntry(key, value);
    }

    private void saveValues(FieldDescription field) {
        List<TableCell> cells = field.getPatterns().stream()
                .map(PatternDescription::getRanges)
                .flatMap(List::stream)
                .map(table::getRange)
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
        valueCells.addAll(cells);
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
