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
package com.xebialabs.deployit.server.api.importer.ear.scanner;

import static com.xebialabs.deployit.server.api.importer.ear.EarImporterTest.EAR_WITHOUT_ATTRIBUTES;
import static com.xebialabs.deployit.server.api.importer.ear.EarImporterTest.EAR_WITH_MANIFEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Test;

import com.xebialabs.deployit.server.api.importer.PackageInfo;

/**
 * Unit test(s) for the {@link ManifestScanner}
 */
public class ManifestScannerTest {
    
    private final ManifestScanner scanner = 
        new ManifestScanner("App-Name", "App-Version");

    @Test
    public void returnsNullIfAttributesMissing() throws IOException {
        assertNull(scanner.scan(new FileSource(EAR_WITHOUT_ATTRIBUTES)));
    }
    
    @Test
    public void scansManifest() throws IOException {
        PackageInfo result = scanner.scan(new FileSource(EAR_WITH_MANIFEST));
        assertEquals("myApp", result.getApplicationName());
        assertEquals("3", result.getApplicationVersion());
    }
}
