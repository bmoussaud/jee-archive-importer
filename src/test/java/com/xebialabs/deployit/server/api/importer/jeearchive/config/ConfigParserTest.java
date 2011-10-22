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
package com.xebialabs.deployit.server.api.importer.jeearchive.config;

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.FilenameScanner;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageMetadataScanner;

/**
 * Unit tests for the {@link ConfigParser}
 */
public class ConfigParserTest {

    @Test
    public void returnsNoScannersForEmptyConfig() {
        assertTrue("Expected no scanners", 
                new ConfigParser(ImmutableMap.<String, String>of()).get().isEmpty());
    }
    
    @Test
    public void returnsFilenameScanner() {
        List<PackageMetadataScanner> scanners = new ConfigParser(
                ImmutableMap.of("nameVersionRegex", ".*", "defaultVersion", "1.0")).get();
        assertEquals(1, scanners.size());
        assertTrue(format("Expected an instance of %s", FilenameScanner.class), 
                scanners.get(0) instanceof FilenameScanner);
    }
    
    @Test
    public void returnsManifestAndFilenameScannerIfSpecified() {
        List<PackageMetadataScanner> scanners = new ConfigParser(
                ImmutableMap.of("nameVersionRegex", ".*", "defaultVersion", "1.0",
                        "scanManifest", "true", "nameManifestAttribute", "App-Name",
                        "versionManifestAttribute", "App-Version")).get();
        assertEquals(2, scanners.size());
        // manifest scanning comes first
        assertTrue(format("Expected an instance of %s", ManifestScanner.class), 
                scanners.get(0) instanceof ManifestScanner);        
        assertTrue(format("Expected an instance of %s", FilenameScanner.class), 
                scanners.get(1) instanceof FilenameScanner);        
    }
}