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
package com.github.pjungermann.config.types.properties;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.types.BaseConfigFactoryTest;

import java.io.IOException;

/**
 * Tests for {@link PropertiesConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class PropertiesConfigFactoryTest extends BaseConfigFactoryTest<PropertiesConfigFactory> {

    @Override
    public String[] getSupportedTypes() {
        return new String[]{"properties"};
    }

    @Override
    public PropertiesConfigFactory createFactory() throws Exception {
        PropertiesConverter converter = new PropertiesConverter();

        PropertiesConfigFactory factory = new PropertiesConfigFactory();
        factory.setConverter(converter);

        return factory;
    }

    @Override
    public String getConfigFile() {
        return "config.properties";
    }

    @Override
    public void validateConfig(Config config, String profile, Config context) throws IOException {
        assertEquals(3, config.size());
        assertEquals("config value", config.get("my.config.key"));
        assertEquals("another value with line break", config.get("another.entry"));
        assertEquals("0.123", config.get("double.string"));
    }

}
