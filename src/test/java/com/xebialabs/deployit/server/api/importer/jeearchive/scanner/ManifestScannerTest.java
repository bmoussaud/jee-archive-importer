/*
 * @(#)FilenameScannerTest.java     19 Oct 2011
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
package com.xebialabs.deployit.server.api.importer.jeearchive.scanner;

import static com.xebialabs.deployit.server.api.importer.jeearchive.JeeArchiveImporterTest.ARCHIVE_WITH_MANIFEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.xebialabs.deployit.server.api.importer.singlefile.SingleFileImporter.PackageMetadata;

/**
 * Unit test(s) for the {@link ManifestScanner}
 */
public class ManifestScannerTest {
    private static final String ARCHIVE_WITHOUT_ATTRIBUTES = "src/test/resources/ear-without-manifest-attributes.ear";
    
    private final ManifestScanner scanner = 
        new ManifestScanner("App-Name", "App-Version");

    @Test
    public void returnsNullIfAttributesMissing() throws IOException {
        assertNull(scanner.scan(new File(ARCHIVE_WITHOUT_ATTRIBUTES)));
    }
    
    @Test
    public void scansManifest() throws IOException {
        PackageMetadata result = scanner.scan(new File(ARCHIVE_WITH_MANIFEST));
        assertEquals("myApp", result.appName);
        assertEquals("3", result.appVersion);
    }
}
