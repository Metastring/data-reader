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

package org.metastringfoundation.healthheatmap.storage.bridges;

import org.hibernate.search.mapper.pojo.bridge.ValueBridge;
import org.hibernate.search.mapper.pojo.bridge.runtime.ValueBridgeToIndexedValueContext;
import org.metastringfoundation.healthheatmap.entities.Gender;
import org.metastringfoundation.healthheatmap.entities.Settlement;

public class GenderValueBridge implements ValueBridge<Gender, String> {
    @Override
    public String toIndexedValue(Gender gender, ValueBridgeToIndexedValueContext valueBridgeToIndexedValueContext) {
        return gender == null ? null : gender.getGender().name();
    }
}