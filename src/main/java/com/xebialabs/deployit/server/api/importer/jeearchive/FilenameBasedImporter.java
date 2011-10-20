/*
 * @(#)EarImporter.java     19 Oct 2011
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

import static com.google.common.collect.ImmutableList.copyOf;

import java.io.File;
import java.io.FilenameFilter;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ImmutableList;
import com.xebialabs.deployit.server.api.importer.ImportSource;
import com.xebialabs.deployit.server.api.importer.ListableImporter;

abstract class FilenameBasedImporter implements ListableImporter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilenameBasedImporter.class);
    
    @Override
    public List<String> list(File directory) {
        ImmutableList<String> supportedFiles = copyOf(directory.list(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String name) {
                        return isSupportedFile(name);
                    }
                }));
        LOGGER.debug("Found supported files in package directory: {}", supportedFiles);
        return supportedFiles;
    }
    
    protected abstract boolean isSupportedFile(String filename);

    @Override
    public boolean canHandle(ImportSource source) {
        return isSupportedFile(source.getFile().getName());
    }
}
