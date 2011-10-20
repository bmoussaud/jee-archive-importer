/*
 * @(#)EarImporter.java     20 Oct 2011
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

import static com.xebialabs.deployit.server.api.importer.jeearchive.io.JeeArchives.isEar;

import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.PackageInfoScanner;

public class EarImporter extends JeeArchiveImporter {

    public EarImporter() {
        super();
    }
    
    @VisibleForTesting
    protected EarImporter(List<PackageInfoScanner> scanners) {
        super(scanners);
    }
    
    @Override
    protected boolean isSupportedJeeArchive(String filename) {
        return isEar(filename);
    }

}
