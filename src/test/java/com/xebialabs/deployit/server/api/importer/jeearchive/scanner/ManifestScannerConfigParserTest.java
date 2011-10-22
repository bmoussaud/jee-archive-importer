/*
 * @(#)ConfigParserTest.java     Jul 31, 2011
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

import static com.qrmedia.commons.reflect.ReflectionUtils.getValue;
import static org.junit.Assert.assertEquals;

import java.util.jar.Attributes.Name;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;
import com.xebialabs.deployit.server.api.importer.jeearchive.scanner.ManifestScanner.ConfigParser;

/**
 * Unit tests for the {@link ManifestScanner.ConfigParser}
 */
public class ManifestScannerConfigParserTest {

    @Test(expected = IllegalArgumentException.class)
    public void failsIfRequiredPropertiesAreMissing() {
        new ConfigParser().apply(ImmutableMap.<String, String>of());
    }
    
    @Test
    public void returnsManifestScanner() throws IllegalAccessException {
        ManifestScanner scanner = new ConfigParser().apply(
                ImmutableMap.of("nameManifestAttribute", "App-Name",
                        "versionManifestAttribute", "App-Version"));
        assertEquals(new Name("App-Name"), getValue(scanner, "nameAttribute"));
        assertEquals(new Name("App-Version"), getValue(scanner, "versionAttribute"));
    }
}