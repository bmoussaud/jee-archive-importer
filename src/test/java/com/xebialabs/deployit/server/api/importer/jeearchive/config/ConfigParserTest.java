/*
 * @(#)ConfigParserTest.java     22 Oct 2011
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
package com.xebialabs.deployit.server.api.importer.jeearchive.config;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.google.common.collect.ImmutableMap;

/**
 * Unit tests for the {@link ConfigParser}
 */
public class ConfigParserTest {

    @Test
    public void usesPropertiesPrefixedByExtension() {
        assertEquals("version", new ConfigParser(ImmutableMap.of(
                "ext.defaultAppVersion", "version"), "ext").getDefaultAppVersion());
    }
}
