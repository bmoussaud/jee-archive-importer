/*
 * @(#)FileNameScanner.java     19 Oct 2011
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.xebialabs.deployit.server.api.importer.singlefile.SingleFileImporter.PackageMetadata;

public class FilenameScanner implements PackageMetadataScanner {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilenameScanner.class);
    
    private final Pattern nameVersionPattern;
    private final String defaultVersion;
    
    public FilenameScanner(@Nonnull String nameVersionRegex, @Nonnull String defaultVersion) {
        this.nameVersionPattern = Pattern.compile(nameVersionRegex);
        this.defaultVersion = defaultVersion;
    }
    
    @Override
    public @Nullable PackageMetadata scan(@Nonnull File jeeArchive) {
        String jeeArchiveName = jeeArchive.getName();
        Matcher nameVersionMatcher = nameVersionPattern.matcher(jeeArchiveName);
        boolean matches = nameVersionMatcher.matches();
        LOGGER.trace("Attempted to match name '{}' against regex '{}': {}",
                new Object[] { jeeArchiveName, nameVersionPattern, (matches ? "succeeded" : "failed") });
        if (!matches) {
            return null;
        }
        
        return new PackageMetadata(nameVersionMatcher.group(1),
                ((nameVersionMatcher.groupCount() > 1) && (nameVersionMatcher.group(2) != null))
                 ? nameVersionMatcher.group(2) 
                 : defaultVersion);
    }
}
