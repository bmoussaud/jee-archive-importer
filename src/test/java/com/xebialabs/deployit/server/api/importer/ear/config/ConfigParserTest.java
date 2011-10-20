/*
 * @(#)ConfigParserTest.java     Jul 31, 2011
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
package com.xebialabs.deployit.server.api.importer.ear.config;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Properties;

import org.junit.Test;

import com.xebialabs.deployit.server.api.importer.ear.scanner.FilenameScanner;
import com.xebialabs.deployit.server.api.importer.ear.scanner.ManifestScanner;
import com.xebialabs.deployit.server.api.importer.ear.scanner.PackageInfoScanner;

/**
 * Unit tests for the {@link ConfigParser}
 */
public class ConfigParserTest {

    @Test
    public void returnsNoScannersForEmptyConfig() {
        assertTrue("Expected no scanners", 
                new ConfigParser(new Properties()).get().isEmpty());
    }
    
    @Test
    public void returnsFilenameScanner() {
        Properties config = new Properties();
        config.put("ear-importer.nameVersionRegex", ".*");
        config.put("ear-importer.defaultVersion", "1.0");
        List<PackageInfoScanner> scanners = new ConfigParser(config).get();
        assertEquals(1, scanners.size());
        assertTrue(format("Expected an instance of %s", FilenameScanner.class), 
                scanners.get(0) instanceof FilenameScanner);
    }
    
    @Test
    public void returnsManifestAndFilenameScannerIfSpecified() {
        Properties config = new Properties();
        config.put("ear-importer.nameVersionRegex", ".*");
        config.put("ear-importer.defaultVersion", "1.0");
        config.put("ear-importer.scanManifest", "true");
        config.put("ear-importer.nameManifestAttribute", "App-Name");
        config.put("ear-importer.versionManifestAttribute", "App-Version");
        List<PackageInfoScanner> scanners = new ConfigParser(config).get();
        assertEquals(2, scanners.size());
        // manifest scanning comes first
        assertTrue(format("Expected an instance of %s", ManifestScanner.class), 
                scanners.get(0) instanceof ManifestScanner);        
        assertTrue(format("Expected an instance of %s", FilenameScanner.class), 
                scanners.get(1) instanceof FilenameScanner);        
    }
}
