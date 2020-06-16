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

package org.metastringfoundation.datareader.helpers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.json.JsonReadFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;

import java.io.IOException;
import java.util.List;

public class Jsonizer {
    private static final JsonMapper jsonMapper = JsonMapper.builder()
            .enable(JsonReadFeature.ALLOW_TRAILING_COMMA).build();

    public static <E> String getJSONString(List<E> someList) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(someList);
    }

    public static String asJSON(Object object) throws JsonProcessingException {
        return jsonMapper.writeValueAsString(object);
    }

    public static <T> Object fromJSON(String json, Class<T> classType) throws IOException {
        return jsonMapper.readValue(json, classType);
    }
}
