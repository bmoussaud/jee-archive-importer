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

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.Test;

import com.xebialabs.deployit.plugin.api.reflect.Type;
import com.xebialabs.deployit.plugin.jee.artifact.Ear;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner;
import com.xebialabs.deployit.server.api.importer.singlefile.SingleFileImporter.PackageMetadata;


/**
 * Unit tests for the {@link JeeArchiveImporter}
 */
public class JeeArchiveImporterTest extends TestBase {
    public static final String ARCHIVE_WITH_MANIFEST = "src/test/resources/ear-with-manifest.ear";
    
    private static class StubJeeArchiveImporter extends JeeArchiveImporter {
        
        private StubJeeArchiveImporter(String nameVersionRegex, String defaultAppVersion,
                boolean readMetadataFromManifest) {
            super("ear", Type.valueOf(Ear.class), nameVersionRegex, defaultAppVersion,
                    readMetadataFromManifest, new ManifestScanner("App-Name", "App-Version"));
        }

        @Override
        protected boolean isSupportedFile(File file) {
            throw new UnsupportedOperationException("TODO Auto-generated method stub");
        }
    }
    
    @Test
    public void getsMetadataFromManifest() {
        PackageMetadata metadata = new StubJeeArchiveImporter("(.*+)", "", true)
            .getPackageMetadata(new File(ARCHIVE_WITH_MANIFEST));
        assertEquals("myApp", metadata.appName);
        assertEquals("3", metadata.appVersion);
    }
    
    @Test
    public void usesSuppliedNameVersionRegex() {
        PackageMetadata metadata = new StubJeeArchiveImporter("(\\w+)-with-(\\w+).ear", "", false)
            .getPackageMetadata(new File(ARCHIVE_WITH_MANIFEST));
        assertEquals("ear", metadata.appName);
        assertEquals("manifest", metadata.appVersion);
    }
    
    @Test
    public void usesSuppliedDefaultAppVersion() {
        PackageMetadata metadata = new StubJeeArchiveImporter("(.+)", "1.1", false)
            .getPackageMetadata(new File(ARCHIVE_WITH_MANIFEST));
        assertEquals("ear-with-manifest.ear", metadata.appName);
        assertEquals("1.1", metadata.appVersion);
    }
}
