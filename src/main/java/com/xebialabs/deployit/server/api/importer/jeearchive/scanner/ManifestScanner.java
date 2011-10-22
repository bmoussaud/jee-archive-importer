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

import static com.google.common.base.Preconditions.checkArgument;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.Attributes.Name;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Function;
import com.xebialabs.deployit.server.api.importer.jeearchive.io.JeeArchives;
import com.xebialabs.deployit.server.api.importer.singlefile.SingleFileImporter.PackageMetadata;

public class ManifestScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManifestScanner.class);
    
    private final Name nameAttribute;
    private final Name versionAttribute;
    
    public ManifestScanner(String nameAttribute, String versionAttribute) {
        this.nameAttribute = new Name(nameAttribute);
        this.versionAttribute = new Name(versionAttribute);
    }

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
    
    public static class ConfigParser implements Function<Map<String, String>, ManifestScanner> {
        private static final String NAME_MANIFEST_ATTRIBUTE_PROPERTY = "nameManifestAttribute";
        private static final String VERSION_MANIFEST_ATTRIBUTE_PROPERTY = "versionManifestAttribute";
        
        @Override
        public ManifestScanner apply(Map<String, String> input) {
            checkArgument(input.containsKey(NAME_MANIFEST_ATTRIBUTE_PROPERTY)
                    && input.containsKey(VERSION_MANIFEST_ATTRIBUTE_PROPERTY), 
              "Configuration file does not contain the required properties '%s' and '%s'", 
              NAME_MANIFEST_ATTRIBUTE_PROPERTY, VERSION_MANIFEST_ATTRIBUTE_PROPERTY);
            LOGGER.debug("Added manifest scanning for attributes '{}' and '{}'",
                    input.get(NAME_MANIFEST_ATTRIBUTE_PROPERTY), 
                    input.get(VERSION_MANIFEST_ATTRIBUTE_PROPERTY));
            return new ManifestScanner(input.get(NAME_MANIFEST_ATTRIBUTE_PROPERTY), 
                    input.get(VERSION_MANIFEST_ATTRIBUTE_PROPERTY));
        }
    }
}
