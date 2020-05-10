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

import java.util.List;

public class ListToMap {
    public static <T> void load(List<T> list, Puttable<T> maplike) {
        if (list.size() %2 != 0) {
            throw new IllegalArgumentException("Can't load to map like without keys and values");
        }
        for (int key = 0; key < list.size(); key += 2) {
            maplike.put(list.get(key), list.get(key + 1));
        }
    }
}
