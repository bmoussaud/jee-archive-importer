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
import static java.lang.String.format;

import java.io.File;
import java.util.Map;
import java.util.Properties;

import javax.annotation.Nonnull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.server.api.importer.jeearchive.config.ConfigParser;
import com.xebialabs.deployit.server.api.importer.jeearchive.config.PrefixStripper;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner;
import com.xebialabs.deployit.server.api.importer.singlefile.ExtensionBasedImporter;
import com.xebialabs.deployit.server.api.importer.singlefile.base.NameAndVersion;
import com.xebialabs.deployit.server.api.importer.singlefile.base.NameAndVersion.NameVersionParser;

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
    
    protected final NameVersionParser nameVersionParser;
    protected final String defaultAppVersion;
    protected final boolean readMetadataFromManifest;
    protected final ManifestScanner manifestScanner;
    
    protected JeeArchiveImporter(@Nonnull String extension, @Nonnull Type type) {
        this(extension, type, ConfigParser.getNameVersionRegex(CONFIG), 
                ConfigParser.getDefaultAppVersion(CONFIG), 
                ConfigParser.isManifestScanningEnabled(CONFIG),
                ConfigParser.getManifestScanner(CONFIG));
    }
    
    @VisibleForTesting 
    protected JeeArchiveImporter(String extension, Type type, 
            String nameVersionRegex, String defaultAppVersion,
            boolean readMetadataFromManifest, ManifestScanner manifestScanner) {
        super(extension, type);
        nameVersionParser = new NameVersionParser(nameVersionRegex);
        this.defaultAppVersion = defaultAppVersion;
        this.readMetadataFromManifest = readMetadataFromManifest;
        this.manifestScanner = manifestScanner;
    }
    
    @Override
    protected PackageMetadata getPackageMetadata(File file) {
        return (readMetadataFromManifest ? manifestScanner.scan(file)
                                         : getMetadataFromFilename(file.getName()));
    }
    
    private PackageMetadata getMetadataFromFilename(String filename) {
        NameAndVersion nameAndVersion = 
            nameVersionParser.parse(filename, defaultAppVersion);
        return new PackageMetadata(nameAndVersion.name, nameAndVersion.version);
    }
}
