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

import static java.lang.Boolean.parseBoolean;

import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner;

public class ConfigParser {
    private static final String SCAN_MANIFEST_PROPERTY = "scanManifest";
    private static final String NAME_VERSION_REGEX_PROPERTY = "nameVersionRegex";
    private static final String DEFAULT_APP_VERSION_PROPERTY = "defaultAppVersion";
    
    private static final ManifestScanner.ConfigParser MANIFEST_SCANNER_FACTORY =
        new ManifestScanner.ConfigParser();
    
    public static boolean isManifestScanningEnabled(@Nonnull Map<String, String> config) {
        return parseBoolean(config.get(SCAN_MANIFEST_PROPERTY));
    }
    
    public static @Nullable ManifestScanner getManifestScanner(@Nonnull Map<String, String> config) {
        return (isManifestScanningEnabled(config) ? MANIFEST_SCANNER_FACTORY.apply(config)
                                                  : null);
    }

    public static @Nonnull String getNameVersionRegex(@Nonnull Map<String, String> config) {
        return config.get(NAME_VERSION_REGEX_PROPERTY);
    }

    public static @Nonnull String getDefaultAppVersion(@Nonnull Map<String, String> config) {
        return config.get(DEFAULT_APP_VERSION_PROPERTY);
    }
}