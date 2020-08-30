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

import org.metastringfoundation.data.DataPoint;
import org.metastringfoundation.data.Dataset;
import org.metastringfoundation.data.DatasetIntegrityError;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class TableToDatasetAdapter implements Dataset {
    private final List<DataPoint> dataPoints;
    private final QueryableFields queryableFields;
    private final Boolean shouldAddAddressToDatapoint;

    public TableToDatasetAdapter(Table table, TableDescription tableDescription) throws DatasetIntegrityError {
        this(table, tableDescription, false);
    }

    public TableToDatasetAdapter(Table table, TableDescription tableDescription, Boolean shouldAddAddressToDatapoint) throws DatasetIntegrityError {
        this.shouldAddAddressToDatapoint = shouldAddAddressToDatapoint;
        queryableFields = new QueryableFields(tableDescription.getFieldDescriptionList(), table);
        this.dataPoints = calculateDataPoints();
    }

    public static TableToDatasetAdapter of(Table table, List<TableDescription> tableDescriptions) throws DatasetIntegrityError {
        return TableToDatasetAdapter.of(table, tableDescriptions, false);
    }

    public static TableToDatasetAdapter of(Table table, List<TableDescription> tableDescriptions, Boolean shouldAddAddressToDatapoint) throws DatasetIntegrityError {
        TableDescription mergedDescription = TableDescription.ofFields(tableDescriptions.stream()
                .flatMap(d -> d.getFieldDescriptionList().stream())
                .collect(Collectors.toList()));
        return new TableToDatasetAdapter(table, mergedDescription, shouldAddAddressToDatapoint);
    }

    private List<DataPoint> calculateDataPoints() {
        return queryableFields.getValueCells()
                .stream()
                .map(this::fetchFieldsAndMakeDataPoint)
                .map(DataPoint::new)
                .collect(Collectors.toList());
    }

    private Map<String, String> fetchFieldsAndMakeDataPoint(TableCell cell) {
        Map<String, String> fields = queryableFields.queryFieldsAt(cell.getRow(), cell.getColumn());
        if (shouldAddAddressToDatapoint) {
            fields.put("meta.dataFileType", "table");
            fields.put("meta.addressInDataFile.row", String.valueOf(cell.getRow()));
            fields.put("meta.addressInDataFile.column", String.valueOf(cell.getColumn()));
        }
        fields.put("value", cell.getValue());
        return fields;
    }

    @Override
    public List<DataPoint> getData() {
        return dataPoints;
    }
}
