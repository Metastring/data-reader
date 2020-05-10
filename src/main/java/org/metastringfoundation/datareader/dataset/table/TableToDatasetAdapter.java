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
import org.metastringfoundation.data.DataPoint;
import org.metastringfoundation.data.Dataset;
import org.metastringfoundation.data.DatasetIntegrityError;

import java.util.Collection;
import java.util.stream.Collectors;

public class TableToDatasetAdapter implements Dataset {
    private static final Logger LOG = LogManager.getLogger(TableToDatasetAdapter.class);
    private final Table table;
    private final TableDescription tableDescription;
    private final Collection<DataPoint> dataPoints;

    public TableToDatasetAdapter(Table table, TableDescription tableDescription) throws DatasetIntegrityError {
        this.table = table;
        this.tableDescription = tableDescription;
        this.dataPoints = calculateDataPoints();
    }

    private Collection<DataPoint> calculateDataPoints() throws DatasetIntegrityError {
        QueryableFields queryableFields = new QueryableFields(tableDescription.getFieldDescriptionList(), table);

        return queryableFields.getValueCells()
                .stream()
                .map(queryableFields::queryFieldsAt)
                .map(DataPoint::new)
                .collect(Collectors.toSet());
    }

    @Override
    public Collection<DataPoint> getData() {
        return dataPoints;
    }
}
