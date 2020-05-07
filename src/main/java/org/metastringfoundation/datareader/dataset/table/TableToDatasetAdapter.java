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
import org.metastringfoundation.datareader.dataset.DataPoint;
import org.metastringfoundation.datareader.dataset.Dataset;
import org.metastringfoundation.datareader.dataset.DatasetIntegrityError;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class TableToDatasetAdapter implements Dataset {
    private static final Logger LOG = LogManager.getLogger(TableToDatasetAdapter.class);
    private Table table;
    private TableDescription tableDescription;

    public TableToDatasetAdapter(Table table, TableDescription tableDescription) {
        this.table = table;
        this.tableDescription = tableDescription;
    }

    @Override
    public Collection<DataPoint> getData() throws DatasetIntegrityError {
        Collection<DataPoint> result = new ArrayList<>();
        QueryableFields queryableFields = new QueryableFields(tableDescription.getFieldDescriptionList(), table);
        Collection<TableCell> values = queryableFields.getValueCells();
        for (TableCell valueCell: values) {
            Map<String, String> fieldsAtThisCell = queryableFields.queryFieldsAt(valueCell.getReference());
            fieldsAtThisCell.put("value", valueCell.getValue());
            DataPoint dataPoint = new DataPoint(fieldsAtThisCell);
            result.add(dataPoint);
        }
        return result;
    }
}
