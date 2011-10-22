/*
 * @(#)ConfigPrefixStripper.java     22 Oct 2011
 *
 * Copyright © 2010 Andrew Phillips.
 *
 * ====================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or 
 * implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ====================================================================
 */
package com.xebialabs.deployit.server.api.importer.jeearchive.config;

import static com.xebialabs.deployit.server.api.importer.jeearchive.collect.Maps2.transformKeys;

import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import com.google.common.base.Function;
import com.google.common.collect.Maps;

public class PrefixStripper implements Function<Properties, Map<String, String>> {
    // must be a regex
    private final String prefixToStripRegex;
    
    public PrefixStripper(String prefixToStrip) {
        prefixToStripRegex = Pattern.quote(prefixToStrip);
    }

    @Override
    public Map<String, String> apply(Properties input) {
        return transformKeys(Maps.fromProperties(input), new Function<String, String>() {
            @Override
            public String apply(String input) {
                return input.replaceFirst(prefixToStripRegex, "");
            }
        });
    }
}
