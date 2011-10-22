/*
 * @(#)ManifestScanner.java     19 Oct 2011
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

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.server.api.importer.jeearchive.io.JeeArchives;
import com.xebialabs.deployit.server.api.importer.singlefile.SingleFileImporter.PackageMetadata;

public class ManifestScanner implements PackageMetadataScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManifestScanner.class);
    
    private final Name nameAttribute;
    private final Name versionAttribute;
    
    public ManifestScanner(String nameAttribute, String versionAttribute) {
        this.nameAttribute = new Name(nameAttribute);
        this.versionAttribute = new Name(versionAttribute);
    }

    @Override
    public PackageMetadata scan(File jeeArchive) {
        Attributes mainAttributes;
        try {
            mainAttributes = JeeArchives.getManifest(jeeArchive).getMainAttributes();
        } catch (IOException exception) {
            LOGGER.warn("Unable to read manifest for JEE archive '{}' due to: {}", 
                    jeeArchive.getName(), exception);
            return null;
        }
        
        if (!(mainAttributes.containsKey(nameAttribute) 
                && mainAttributes.containsKey(versionAttribute))) {
            LOGGER.warn("Manifest for JEE archive '{}' does not contain expected attributes '{}' and '{}'", 
                    new Object[] { jeeArchive.getName(), nameAttribute, versionAttribute });
            return null;
        }
        
        return new PackageMetadata(mainAttributes.getValue(nameAttribute),
                mainAttributes.getValue(versionAttribute));
    }
}
