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

import static com.xebialabs.deployit.server.api.importer.jeearchive.JeeArchiveImporterTest.ARCHIVE_WITHOUT_ATTRIBUTES;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ImportedPackage;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.FileSource;


/**
 * Unit tests for the {@link JeeArchiveImporter}
 */
public class FilenameBasedImporterTest extends TestBase {

    private static class FilesWithOuImporter extends FilenameBasedImporter {
        @Override
        protected boolean isSupportedFile(String filename) {
            return filename.contains("ou");
        }

        @Override
        public PackageInfo preparePackage(ImportSource source, ImportingContext context) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public ImportedPackage importEntities(PackageInfo packageInfo, ImportingContext context) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public void cleanUp(PackageInfo packageInfo, ImportingContext context) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }
        
    }
    
    @Test
    public void listsSupportedFiles() {
        assertEquals(ImmutableList.of("ear-without-manifest-attributes.ear"), 
                new FilesWithOuImporter().list(new File("src/test/resources")));
    }
    
    @Test
    public void handlesSupportedFiles() {
        assertTrue("Expected supported files to be handled", new FilesWithOuImporter()
            .canHandle(new FileSource(ARCHIVE_WITHOUT_ATTRIBUTES)));
    }
}
