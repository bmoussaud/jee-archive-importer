/*
 * @(#)Ears.java     19 Oct 2011
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
package com.xebialabs.deployit.server.api.importer.jeearchive.io;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;

import java.io.File;
import java.io.IOException;
import java.util.jar.Manifest;

import javax.annotation.Nonnull;

import com.xebialabs.deployit.plugin.jee.artifact.Ear;

import de.schlichtherle.truezip.file.TFile;
import de.schlichtherle.truezip.file.TFileInputStream;

public class JeeArchives {
    public static final String MANIFEST_PATH = "META-INF/MANIFEST.MF";
    
    // must be lowercase
    private static final String EAR_FILENAME_SUFFIX = format(".%s", Ear.ARCHIVE_EXTENSION);
    
    public static boolean isEar(@Nonnull File file) {
        return isEar(file.getName());
    }
    
    public static boolean isEar(@Nonnull String filename) {
        return filename.toLowerCase().endsWith(EAR_FILENAME_SUFFIX);
    }
    
    public static @Nonnull Manifest getManifest(@Nonnull File ear) throws IOException {
        TFileInputStream manifestEntryStream = 
            new TFileInputStream(getEntry(ear, MANIFEST_PATH));
        try {
            return new Manifest(manifestEntryStream);
        } finally {
            manifestEntryStream.close();
        }
    }
    
    public static @Nonnull TFile getEntry(@Nonnull File ear, @Nonnull String path) {
        checkArgument(new TFile(ear, path).exists(), 
                "EAR '%s' does not contain an entry at '%s'", ear, path);
        return new TFile(ear, path);
    }
}
