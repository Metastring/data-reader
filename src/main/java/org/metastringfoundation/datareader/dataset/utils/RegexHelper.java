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

package org.metastringfoundation.datareader.dataset.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexHelper {
    public static String getFirstMatchOrAll(String raw, Pattern regex) {
        String match = getFirstMatchOrNull(raw, regex);
        if (match == null) {
            return raw;
        } else {
            return match;
        }
    }
    public static String getFirstMatchOrNull(String raw, Pattern regex) {
        Matcher matcher = regex.matcher(raw);

        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}
