/*
 * Copyright 2015 Patrick Jungermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pjungermann.config.types.json;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import com.github.pjungermann.config.types.BaseConfigFactoryTest;
import com.github.pjungermann.config.types.ConfigConversionException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Tests for {@link JsonConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class JsonConfigFactoryTest extends BaseConfigFactoryTest<JsonConfigFactory> {

    final File sourceFileWithSingleQuotes = new File(configResourceFolder, "config_singleQuotes.json");

    @Override
    public String[] getSupportedTypes() {
        return new String[]{"json"};
    }

    @Override
    public JsonConfigFactory createFactory() throws Exception {
        JsonConverter converter = new JsonConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());

        JsonConfigFactory factory = new JsonConfigFactory();
        factory.setConverter(converter);

        return factory;
    }

    @Override
    public String getConfigFile() {
        return "config.json";
    }

    @Override
    public void validateConfig(Config config, String profile, Config context) throws IOException {
        assertEquals(6, config.size());
        assertEquals("level", config.get("first"));
        assertEquals("string", config.get("level1.level2.string-type"));
        assertEquals(false, config.get("level1.level2.boolean_false"));
        assertEquals(true, config.get("level1.level2.boolean_true"));
        assertEquals(123, config.get("level1.level2.int-type"));
        assertEquals(34.567D, config.get("level1.level2.double-type"));
    }

    @Test
    public void create_singleQuotes_allowInvalidSingleQuotes() throws IOException, ConfigConversionException {
        Config config = factory.create(sourceFile, null, new Config());
        Config configFromSingleQuotes = factory.create(sourceFileWithSingleQuotes, null, new Config());

        assertEquals(config, configFromSingleQuotes);
    }

}
