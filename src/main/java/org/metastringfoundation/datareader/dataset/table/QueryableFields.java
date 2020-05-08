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

import org.metastringfoundation.datareader.dataset.DatasetIntegrityError;
import org.metastringfoundation.datareader.dataset.utils.RegexHelper;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class QueryableFields {
    private List<FieldDescription> fields;
    private Table table;
    private Map<Integer, List<Field>> rowMap;
    private Map<Integer, List<Field>> columnMap;
    private Collection<TableCell> valueCells = new HashSet<>();

    public QueryableFields(List<FieldDescription> fields, Table table) throws DatasetIntegrityError {
        this.fields = fields;
        this.table = table;
        calculateFields();
    }

    private String parseField(FieldDescription fieldDescription, String rawCellValue) {
        if (fieldDescription.getCompiledPattern() == null) {
            return rawCellValue;
        } else {
            return RegexHelper.getFirstMatchOrAll(rawCellValue, fieldDescription.getCompiledPattern());
        }
    }

    private void calculateFields() throws DatasetIntegrityError {
        rowMap = new HashMap<>();
        columnMap = new HashMap<>();
        for (FieldDescription field: fields) {
            if (field.getField().equals("value")) {
                // value is a special field and needs to be handled separately
                List<TableCell> cells = table.getRange(field.getRange());
                valueCells.addAll(cells);

                // let us not save value here. only references
                continue;
            }
            if (field.getRange().getRangeType() == TableRangeReference.RangeType.COLUMN_ONLY) {
                // the fields are written in a column. That means, their values will be applicable to rows.
                List<TableCell> cells = table.getRange(field.getRange());
                for (TableCell cell: cells) {
                    Integer row = cell.getRow();
                    if (!rowMap.containsKey(row)) {
                        rowMap.put(row, new ArrayList<>());
                    }
                    rowMap.get(row).add(new Field(field.getField(), parseField(field, cell.getValue())));
                }
            }

            if (field.getRange().getRangeType() == TableRangeReference.RangeType.ROW_ONLY) {
                // the fields are written in a row. That means, their values will be applicable to columns.
                List<TableCell> cells = table.getRange(field.getRange());
                for (TableCell cell: cells) {
                    Integer column = cell.getColumn();
                    if (!columnMap.containsKey(column)) {
                        columnMap.put(column, new ArrayList<>());
                    }
                    columnMap.get(column).add(new Field(field.getField(), parseField(field, cell.getValue())));
                }
            }

            if (field.getRange().getRangeType() == TableRangeReference.RangeType.ROW_AND_COLUMN &&
                    !field.getField().equals("value")
            ) {
                throw new DatasetIntegrityError("Only value can be in both column and row");
            }
        }
    }

    public Map<String, String> queryFieldsAt(TableCellReference cell) {
        Map<String, String> fieldsAtThisCell = new HashMap<>();
        if (rowMap.containsKey(cell.getRow())) {
            List<Field> fields = rowMap.get(cell.getRow());
            for (Field field: fields) {
                fieldsAtThisCell.put(field.getName(), field.getValue());
            }
        }

        if (columnMap.containsKey(cell.getColumn())) {
            List<Field> fields = columnMap.get(cell.getColumn());
            for (Field field: fields) {
                fieldsAtThisCell.put(field.getName(), field.getValue());
            }
        }
        return fieldsAtThisCell;
    }

    public Collection<TableCell> getValueCells() {
        return valueCells;
    }
}
