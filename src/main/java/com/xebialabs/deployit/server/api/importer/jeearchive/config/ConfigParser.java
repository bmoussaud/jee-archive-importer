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

import static com.google.common.collect.Maps.filterKeys;
import static java.lang.Boolean.parseBoolean;
import static java.lang.String.format;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Predicate;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner;

public class ConfigParser {
    private static final String SCAN_MANIFEST_PROPERTY = "scanManifest";
    private static final String NAME_VERSION_REGEX_PROPERTY = "nameVersionRegex";
    private static final String DEFAULT_APP_VERSION_PROPERTY = "defaultAppVersion";
    private static final ManifestScanner.ConfigParser MANIFEST_SCANNER_FACTORY =
        new ManifestScanner.ConfigParser();
    
    private final String propertyPrefix;
    private final Map<String, String> configForExtension;
    
    public ConfigParser(@Nonnull Map<String, String> config, 
            @Nonnull String supportedExtension) {
        propertyPrefix = format("%s.", supportedExtension);
        configForExtension = new PrefixStripper(propertyPrefix).apply(
                filterKeys(config, new Predicate<String>() {
                    @Override
                    public boolean apply(String input) {
                        return input.startsWith(propertyPrefix);
                    }
                }));
    }
    
    public boolean isManifestScanningEnabled() {
        return parseBoolean(configForExtension.get(SCAN_MANIFEST_PROPERTY));
    }
    
    public @Nullable ManifestScanner getManifestScanner() {
        return (isManifestScanningEnabled() ? MANIFEST_SCANNER_FACTORY.apply(configForExtension)
                                            : null);
    }

    public @Nonnull String getNameVersionRegex() {
        return configForExtension.get(NAME_VERSION_REGEX_PROPERTY);
    }

    public @Nonnull String getDefaultAppVersion() {
        return configForExtension.get(DEFAULT_APP_VERSION_PROPERTY);
    }
}