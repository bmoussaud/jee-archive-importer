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
import static com.xebialabs.deployit.plugin.api.reflect.DescriptorRegistry.getDescriptor;
import static java.lang.String.format;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.jee.artifact.Ear;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ImportedPackage;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.ListableImporter;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.deployit.server.api.importer.jeearchive.config.ConfigParser;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageInfoScanner;
import com.xebialabs.overthere.local.LocalFile;

abstract class JeeArchiveImporter implements ListableImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(JeeArchiveImporter.class);
    
    private static final String CONFIG_FILE_NAME = "jee-archive-importer.properties";
    private static final Properties CONFIG = new Properties();
    
    static {
        try {
            CONFIG.load(checkNotNull(Thread.currentThread().getContextClassLoader()
                    .getResourceAsStream(CONFIG_FILE_NAME), CONFIG_FILE_NAME));
        } catch (Exception exception) {
            LOGGER.error(format("Unable to load configuration file '%s' from classpath", 
                    CONFIG_FILE_NAME), exception);
        }
    }
    
    protected final List<PackageInfoScanner> scanners;
    
    public JeeArchiveImporter() {
        this(new ConfigParser(CONFIG).get());
    }

    @VisibleForTesting
    protected JeeArchiveImporter(List<PackageInfoScanner> scanners) {
        this.scanners = copyOf(scanners);
    }
    
    @Override
    public List<String> list(File directory) {
        ImmutableList<String> supportedFiles = copyOf(directory.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return isSupportedJeeArchive(name);
                    }
                }));
        LOGGER.debug("Found supported JEE archives in package directory: {}", supportedFiles);
        return supportedFiles;
    }
    
    protected abstract boolean isSupportedJeeArchive(String filename);

    @Override
    public boolean canHandle(ImportSource source) {
        return isSupportedJeeArchive(source.getFile().getName());
    }

    @Override
    public PackageInfo preparePackage(ImportSource source, ImportingContext context) {
        // first non-null result wins
        for (PackageInfoScanner scanner : scanners) {
            PackageInfo result = scanner.scan(source);
            if (result != null) {
                LOGGER.info("Returning package for application '{}', version {}", 
                        result.getApplicationName(), result.getApplicationVersion());
                return result;
            }
        }
        throw new IllegalArgumentException(format("Unable to import JEE archive source '%s'", source));
    }

    @Override
    public ImportedPackage importEntities(PackageInfo packageInfo, ImportingContext context) {
        ImportedPackage importedPackage = new ImportedPackage(packageInfo);
        Ear ear = getDescriptor(Type.valueOf(Ear.class)).newInstance();
        ear.setId(format("%s/%s", importedPackage.getDeploymentPackage().getId(),
                importedPackage.getApplication().getName()));
        ear.setFile(LocalFile.valueOf(packageInfo.getSource().getFile()));
        LOGGER.debug("Created EAR with ID '{}'", ear.getId());
        importedPackage.addDeployable(ear);
        return importedPackage;
    }

    @Override
    public void cleanUp(PackageInfo packageInfo, ImportingContext context) {
        LOGGER.debug("Nothing to clean up");
    }
}
