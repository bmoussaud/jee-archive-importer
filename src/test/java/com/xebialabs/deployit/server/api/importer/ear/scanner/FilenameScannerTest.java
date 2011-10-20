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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.xebialabs.deployit.server.api.importer.PackageInfo;

/**
 * Unit test(s) for the {@link FilenameScanner}
 */
public class FilenameScannerTest {
    // <name>.ear or <name>-<version>.ear
    private final FilenameScanner scanner = 
        new FilenameScanner("(\\w+)(?:-(\\d+))?\\.ear", "1.0");
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Test
    public void returnsNullIfNoMatch() throws IOException {
        assertNull(scanner.scan(new FileSource(tempFolder.newFile("no match.ear"))));
    }
    
    @Test
    public void usesNameAndVersionIfPresent() throws IOException {
        PackageInfo result = scanner.scan(
                new FileSource(tempFolder.newFile("myApp-2.ear")));
        assertEquals("myApp", result.getApplicationName());
        assertEquals("2", result.getApplicationVersion());
    }
    
    @Test
    public void fallsBackToDefaultVersion() throws IOException {
        PackageInfo result = scanner.scan(
                new FileSource(tempFolder.newFile("myApp.ear")));
        assertEquals("myApp", result.getApplicationName());
        assertEquals("1.0", result.getApplicationVersion());
    }
}
