/*
 * @(#)ConfigParser.java     22 Jul 2011
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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.collect.Lists.newLinkedList;
import static com.xebialabs.deployit.server.api.importer.jeearchive.collect.Maps2.transformKeys;
import static java.lang.Boolean.parseBoolean;

import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.google.common.base.Supplier;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Maps;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.FilenameScanner;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageInfoScanner;


public class ConfigParser implements Supplier<List<PackageInfoScanner>>{
    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigParser.class);
    
    // must be a regex
    private static final String CONFIG_PROPERTY_PREFIX = Pattern.quote("ear-importer.");
    
    // TODO: make scanners responsible for their own instantiation
    private static final String SCAN_MANIFEST_PROPERTY = "scanManifest";
    private static final String NAME_MANIFEST_ATTRIBUTE_PROPERTY = "nameManifestAttribute";
    private static final String VERSION_MANIFEST_ATTRIBUTE_PROPERTY = "versionManifestAttribute";
    private static final String NAME_VERSION_REGEX_PROPERTY = "nameVersionRegex";
    private static final String DEFAULT_VERSION_PROPERTY = "defaultVersion";
    
    private final List<PackageInfoScanner> scanners;
    
    public ConfigParser(@Nonnull Properties config) {
        scanners = parseConfig(stripPrefix(config));
    }
    
    private static Map<String, String> stripPrefix(Properties config) {
        return transformKeys(Maps.fromProperties(config), new Function<String, String>() {
                @Override
                public String apply(String input) {
                    return input.replaceFirst(CONFIG_PROPERTY_PREFIX, "");
                }
            });
    }

    private static List<PackageInfoScanner> parseConfig(Map<String, String> config) {
        
        if (config.isEmpty()) {
            return ImmutableList.of();
        }
        
        List<PackageInfoScanner> scanners = newLinkedList();
        
        String scanManifest = config.get(SCAN_MANIFEST_PROPERTY);
        if (parseBoolean(scanManifest)) {
            checkArgument(config.containsKey(NAME_MANIFEST_ATTRIBUTE_PROPERTY)
                    && config.containsKey(VERSION_MANIFEST_ATTRIBUTE_PROPERTY), 
              "Configuration file does not contain the required properties '%s' and '%s'", 
              NAME_MANIFEST_ATTRIBUTE_PROPERTY, VERSION_MANIFEST_ATTRIBUTE_PROPERTY);
            scanners.add(new ManifestScanner(config.get(NAME_MANIFEST_ATTRIBUTE_PROPERTY), 
                    config.get(VERSION_MANIFEST_ATTRIBUTE_PROPERTY)));
            LOGGER.debug("Added manifest scanning for attributes '{}' and '{}'",
                    config.get(NAME_MANIFEST_ATTRIBUTE_PROPERTY), 
                    config.get(VERSION_MANIFEST_ATTRIBUTE_PROPERTY));
        }

        // FileNameScanner is the fallback
        checkArgument(config.containsKey(NAME_VERSION_REGEX_PROPERTY)
                      && config.containsKey(DEFAULT_VERSION_PROPERTY), 
                "Configuration file does not contain the required properties '%s' and '%s'", 
                NAME_VERSION_REGEX_PROPERTY, DEFAULT_VERSION_PROPERTY);
        scanners.add(new FilenameScanner(config.get(NAME_VERSION_REGEX_PROPERTY),
                config.get(DEFAULT_VERSION_PROPERTY)));
        LOGGER.debug("Added filename scanning for using '{}' with default version '{}'",
                config.get(NAME_VERSION_REGEX_PROPERTY), config.get(DEFAULT_VERSION_PROPERTY));        
        return scanners;
    }
    
    @Override
    public @Nonnull List<PackageInfoScanner> get() {
        return scanners;
    }
}