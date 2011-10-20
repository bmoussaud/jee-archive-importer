/*
 * @(#)PlainArchiveConverterTest.java     23 Jul 2011
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

import static com.xebialabs.deployit.server.api.importer.jeearchive.JeeArchiveImporterTest.ARCHIVE_WITH_MANIFEST;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.plugin.api.boot.PluginBooter;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.FileSource;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageInfoScanner;


/**
 * Unit tests for the {@link JeeArchiveImporter}
 */
public class EarImporterTest {
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
  
    @BeforeClass
    public static void boot() {
        PluginBooter.bootWithoutGlobalContext();
    }
    
    @Test
    public void listsEars() {
        assertEquals(ImmutableList.of("ear-with-manifest.ear", "ear-without-manifest-attributes.ear"), 
                newEarImporter().list(new File("src/test/resources")));
    }
    
    @Test
    public void handlesEars() {
        assertTrue("Expected EARs to be handled", newEarImporter()
            .canHandle(new FileSource(ARCHIVE_WITH_MANIFEST)));
    }
    
    @Test
    public void ignoresDars() throws IOException {
        assertFalse("Expected DARs to be ignored", newEarImporter()
            .canHandle(new FileSource(tempFolder.newFile("myApp.dar"))));
    }
    
    private static EarImporter newEarImporter() {
        return new EarImporter(ImmutableList.<PackageInfoScanner>of());
    }
}
