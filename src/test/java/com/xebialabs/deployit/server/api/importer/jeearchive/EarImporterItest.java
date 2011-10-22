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

import static java.lang.String.format;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Files;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.jee.artifact.Ear;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.overthere.local.LocalFile;


/**
 * Integration tests for the {@link EarImporter}
 */
public class EarImporterItest extends TestBase {
    public static final String ARCHIVE_WITH_MANIFEST = "src/test/resources/ear-with-manifest.ear";
    public static final String ARCHIVE_WITHOUT_ATTRIBUTES = "src/test/resources/ear-without-manifest-attributes.ear";
    
    private static final ImportingContext DUMMY_IMPORT_CTX = new DummyImportingContext();
    
    @Test
    public void createsPackageWithEarFromManifest() throws IOException {
        EarImporter importer = new EarImporter();
        FileSource source = new FileSource(ARCHIVE_WITH_MANIFEST);
        PackageInfo packageInfo = importer.preparePackage(source, DUMMY_IMPORT_CTX);
        // from the manifest
        assertEquals("myApp", packageInfo.getApplicationName());
        assertEquals("3", packageInfo.getApplicationVersion());
        List<Deployable> deployables = 
            importer.importEntities(packageInfo, DUMMY_IMPORT_CTX).getDeployables();
        assertEquals(1, deployables.size());
        assertTrue(format("Expected instance of %s", Ear.class),
                deployables.get(0) instanceof Ear);
        Ear ear = (Ear) deployables.get(0);
        assertTrue("Expected the files to contain the same bytes",
                Files.equal(source.getFile(), ((LocalFile) ear.getFile()).getFile()));
        assertEquals("Applications/myApp/3/myApp", ear.getId());
        assertEquals("myApp", ear.getName());
    }

    @Test
    public void createsPackageWithEarFromFilename() throws IOException {
        EarImporter importer = new EarImporter("(.+)", "1.0", false, null);
        FileSource source = new FileSource(ARCHIVE_WITH_MANIFEST);
        PackageInfo packageInfo = importer.preparePackage(source, DUMMY_IMPORT_CTX);
        // from the filename
        assertEquals("ear-with-manifest.ear", packageInfo.getApplicationName());
        assertEquals("1.0", packageInfo.getApplicationVersion());
        List<Deployable> deployables = 
            importer.importEntities(packageInfo, DUMMY_IMPORT_CTX).getDeployables();
        assertEquals(1, deployables.size());
        assertTrue(format("Expected instance of %s", Ear.class),
                deployables.get(0) instanceof Ear);
        Ear ear = (Ear) deployables.get(0);
        assertTrue("Expected the files to contain the same bytes",
                Files.equal(source.getFile(), ((LocalFile) ear.getFile()).getFile()));
        assertEquals("Applications/ear-with-manifest.ear/1.0/ear-with-manifest.ear", ear.getId());
        assertEquals("ear-with-manifest.ear", ear.getName());
    }
    
    private static class DummyImportingContext implements ImportingContext {
        @Override
        public <T> T getAttribute(String name) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }

        @Override
        public <T> void setAttribute(String name, T value) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }
    }
    
    private static class FileSource implements ImportSource {
        private final File file;
        
        public FileSource(String path) {
            file = new File(path);
        }
        
        @Override
        public File getFile() {
            return file;
        }

        @Override
        public void cleanUp() {}
    }
}
