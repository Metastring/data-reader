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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ListUtils <T> {
    public static <T> List<List<T>> transpose(List<List<T>> listToTranspose) {
        List<List<T>> transposedList = new ArrayList<>();
        for (List<T> currentRow : listToTranspose) {
            for (int column = 0; column < currentRow.size(); column++) {
                T currentValue = currentRow.get(column);
                List<T> targetRow;
                try {
                    targetRow = transposedList.get(column);
                    targetRow.add(currentValue);
                } catch (IndexOutOfBoundsException e) {
                    targetRow = new ArrayList<>();
                    targetRow.add(currentValue);
                    transposedList.add(targetRow);
                }
            }
        }

        return transposedList;
    }

    public static List<String> splitStringToArray(String input, String delimiter) {
        if (input.contains(delimiter)) {
            return Arrays.asList(input.split(delimiter));
        } else {
            return Collections.singletonList(input);
        }
    }

    public static List<Long> stringArrayToLong(List<String> input) {
        return input.stream()
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }

}
