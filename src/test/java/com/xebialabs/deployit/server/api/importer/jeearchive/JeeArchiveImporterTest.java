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

import com.google.common.collect.ImmutableList;
import com.google.common.io.Files;
import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.jee.artifact.Ear;
import com.xebialabs.deployit.server.api.importer.ImportingContext;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.FileSource;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageMetadataScanner;
import com.xebialabs.overthere.local.LocalFile;


/**
 * Unit tests for the {@link JeeArchiveImporter}
 */
public class JeeArchiveImporterTest extends TestBase {
    public static final String ARCHIVE_WITH_MANIFEST = "src/test/resources/ear-with-manifest.ear";
    public static final String ARCHIVE_WITHOUT_ATTRIBUTES = "src/test/resources/ear-without-manifest-attributes.ear";
    
    private static final ImportingContext DUMMY_IMPORT_CTX = new DummyImportingContext();
    
    private static class StubJeeArchiveImporter extends JeeArchiveImporter {
        
        private StubJeeArchiveImporter() {
            super("ear", Type.valueOf(Ear.class), ImmutableList.<PackageMetadataScanner>of());
        }

        // parameter value doesn't matter, just triggers a different constructor
        private StubJeeArchiveImporter(boolean loadConfig) {
            super("ear", Type.valueOf(Ear.class));
        }

        @Override
        protected boolean isSupportedFile(File file) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }
    }
    
    @Test
    public void triesScannersInOrder() {
        // manifest scanner comes first
        PackageInfo result = new StubJeeArchiveImporter(true).preparePackage(
                new FileSource(ARCHIVE_WITH_MANIFEST), DUMMY_IMPORT_CTX);
        assertEquals("myApp", result.getApplicationName());
        assertEquals("3", result.getApplicationVersion());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void failsIfNoScannerSucceeds() {
        new StubJeeArchiveImporter().preparePackage(
                new FileSource(ARCHIVE_WITH_MANIFEST), DUMMY_IMPORT_CTX);
    }
    
    @Test
    public void addsEarToPackage() throws IOException {
        File earFile = new File(ARCHIVE_WITH_MANIFEST);
        PackageInfo packageInfo = new PackageInfo(new FileSource(earFile));
        packageInfo.setApplicationName("myApp");
        packageInfo.setApplicationVersion("2.0");
        List<Deployable> deployables = new StubJeeArchiveImporter()
            .importEntities(packageInfo, DUMMY_IMPORT_CTX).getDeployables();
        assertEquals(1, deployables.size());
        assertTrue(format("Expected instance of %s", Ear.class),
                deployables.get(0) instanceof Ear);
        Ear ear = (Ear) deployables.get(0);
        assertTrue("Expected the files to contain the same bytes",
                Files.equal(earFile, ((LocalFile) ear.getFile()).getFile()));
        assertEquals("Applications/myApp/2.0/myApp", ear.getId());
        assertEquals("myApp", ear.getName());
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
}
