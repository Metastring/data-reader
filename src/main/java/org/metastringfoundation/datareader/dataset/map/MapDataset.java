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

package org.metastringfoundation.datareader.dataset.map;

import org.metastringfoundation.data.DataPoint;
import org.metastringfoundation.data.Dataset;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class MapDataset implements Dataset {
    private final Collection<DataPoint> data;

    public MapDataset(Collection<Map<String, String>> dataMaps) {
        this.data = dataMaps.stream()
                .map(DataPoint::new)
                .collect(Collectors.toCollection(HashSet::new));
    }

    @Override
    public Collection<DataPoint> getData() {
        return data;
    }

}
