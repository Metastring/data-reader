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

package org.metastringfoundation.healthheatmap.dataset.entities;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.metastringfoundation.healthheatmap.helpers.PatternParsingAssistants.quotedDimension;

public class UnmatchedGender {
    private String type;

    public static Map<Integer, UnmatchedGender> getGender(Map<Integer, Map<String, String>> dimensionsMap) {
        Map<Integer, UnmatchedGender> result = new HashMap<>();
        for (Map.Entry<Integer, Map<String, String>> row: dimensionsMap.entrySet()) {
            String genderType = row.getValue().get(quotedDimension("gender"));
            if (!(genderType == null)) {
                UnmatchedGender gender = new UnmatchedGender();
                gender.setType(genderType);
                result.put(row.getKey(), gender);
            }
        }
        return result;
    }

    public UnmatchedGender() {}

    public UnmatchedGender(String type) {
        setType(type);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public boolean equals(Object obj) {
        if (super.equals(obj)) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UnmatchedGender other = (UnmatchedGender) obj;
        return other.getType().equals(this.getType());
    }

    @Override
    public String toString() {
        return super.toString() + "\n" +
                "type: " + getType();
    }

    @Override
    public int hashCode() {
        return Objects.hash(type);
    }
}