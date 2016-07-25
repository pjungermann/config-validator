/*
 * Copyright 2015-2016 Patrick Jungermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pjungermann.config.types.groovy;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import groovy.util.ConfigObject;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ConfigObjectConverter}.
 *
 * @author Patrick Jungermann
 */
public class ConfigObjectConverterTest {

    ConfigObjectConverter converter;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        converter = new ConfigObjectConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());
    }

    @Test
    public void from_configObjectWithHierarchy_flatConfig() {
        ConfigObject root = new ConfigObject();
        ConfigObject foo = new ConfigObject();
        foo.put("bar", "foobar");
        root.put("foo", foo);

        Config config = converter.from(root);

        assertEquals(1, config.size());
        assertEquals("foobar", config.get("foo.bar"));
    }

    @Test
    public void to_flatConfig_configObjectWithHierarchicalAndFlatVersion() {
        Config config = new Config();
        config.put("foo.bar", "foobar");

        ConfigObject configObject = converter.to(config);

        assertEquals(2, configObject.size());
        // flat version
        assertEquals("foobar", configObject.get("foo.bar"));
        // hierarchical version
        ConfigObject foo = (ConfigObject) configObject.get("foo");
        assertEquals(1, foo.size());
        assertEquals("foobar", foo.get("bar"));
    }

}
