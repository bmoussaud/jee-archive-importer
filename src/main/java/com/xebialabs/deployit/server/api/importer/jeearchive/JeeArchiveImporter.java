/*
 * @(#)EarImporter.java     19 Oct 2011
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
package com.xebialabs.deployit.server.api.importer.jeearchive;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableList.copyOf;
import static java.lang.String.format;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.server.api.importer.jeearchive.config.ConfigParser;
import com.xebialabs.deployit.server.api.importer.jeearchive.config.PrefixStripper;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageMetadataScanner;
import com.xebialabs.deployit.server.api.importer.singlefile.ExtensionBasedImporter;

abstract class JeeArchiveImporter extends ExtensionBasedImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JeeArchiveImporter.class);
    
    private static final String CONFIG_FILE_NAME = "jee-archive-importer.properties";
    private static final String CONFIG_PROPERTY_PREFIX = "jee-archive-importer.";
    private static final Map<String, String> CONFIG;
    
    static {
        Properties configProperties = new Properties();
        try {
            configProperties.load(checkNotNull(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE_NAME), CONFIG_FILE_NAME));
        } catch (Exception exception) {
            LOGGER.error(format("Unable to load configuration file '%s' from classpath", 
                    CONFIG_FILE_NAME), exception);
        }
        CONFIG = new PrefixStripper(CONFIG_PROPERTY_PREFIX).apply(configProperties);
    }
    
    protected final List<PackageMetadataScanner> scanners;
    
    protected JeeArchiveImporter(String extension, Type type) {
        this(extension, type, new ConfigParser(CONFIG).get());
    }
    
    @VisibleForTesting
    protected JeeArchiveImporter(String extension, Type type, 
            List<PackageMetadataScanner> scanners) {
        super(extension, type);
        // defensive copy
        this.scanners = copyOf(scanners);
    }
    
    @Override
    protected PackageMetadata getPackageMetadata(File file) {
        // first non-null result wins
        for (PackageMetadataScanner scanner : scanners) {
            PackageMetadata result = scanner.scan(file);
            if (result != null) {
                LOGGER.info("Returning package metadata for application '{}', version {}", 
                        result.appName, result.appVersion);
                return result;
            }
        }
        throw new IllegalArgumentException(format("Unable to import JEE archive (?) '%s'", file));
    }
}
