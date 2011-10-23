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

import java.io.IOException;
import java.util.List;

import org.junit.Test;

import com.google.common.io.Files;
import com.xebialabs.deployit.plugin.api.udm.Deployable;
import com.xebialabs.deployit.plugin.jee.artifact.War;
import com.xebialabs.deployit.server.api.importer.PackageInfo;
import com.xebialabs.overthere.local.LocalFile;


/**
 * Integration tests for the {@link WarImporter}
 */
public class WarImporterItest extends ItestBase {
    private static final String WAR_WITH_MANIFEST = "src/test/resources/war-with-manifest.war";
    
    @Test
    public void createsPackageWithWarFromManifest() throws IOException {
        WarImporter importer = new WarImporter();
        FileSource source = new FileSource(WAR_WITH_MANIFEST);
        PackageInfo packageInfo = importer.preparePackage(source, DUMMY_IMPORT_CTX);
        // from the manifest
        assertEquals("myApp", packageInfo.getApplicationName());
        assertEquals("3", packageInfo.getApplicationVersion());
        List<Deployable> deployables = 
            importer.importEntities(packageInfo, DUMMY_IMPORT_CTX).getDeployables();
        assertEquals(1, deployables.size());
        assertTrue(format("Expected instance of %s", War.class),
                deployables.get(0) instanceof War);
        War War = (War) deployables.get(0);
        assertTrue("Expected the files to contain the same bytes",
                Files.equal(source.getFile(), ((LocalFile) War.getFile()).getFile()));
        assertEquals("Applications/myApp/3/myApp", War.getId());
        assertEquals("myApp", War.getName());
    }

    @Test
    public void createsPackageWithWarFromFilename() throws IOException {
        WarImporter importer = new WarImporter("(.+)", "1.0", false, null);
        FileSource source = new FileSource(WAR_WITH_MANIFEST);
        PackageInfo packageInfo = importer.preparePackage(source, DUMMY_IMPORT_CTX);
        // from the filename
        assertEquals("war-with-manifest.war", packageInfo.getApplicationName());
        assertEquals("1.0", packageInfo.getApplicationVersion());
        List<Deployable> deployables = 
            importer.importEntities(packageInfo, DUMMY_IMPORT_CTX).getDeployables();
        assertEquals(1, deployables.size());
        assertTrue(format("Expected instance of %s", War.class),
                deployables.get(0) instanceof War);
        War War = (War) deployables.get(0);
        assertTrue("Expected the files to contain the same bytes",
                Files.equal(source.getFile(), ((LocalFile) War.getFile()).getFile()));
        assertEquals("Applications/war-with-manifest.war/1.0/war-with-manifest.war", War.getId());
        assertEquals("war-with-manifest.war", War.getName());
    }
}
