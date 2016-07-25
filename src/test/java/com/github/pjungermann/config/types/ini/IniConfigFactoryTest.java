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
package com.github.pjungermann.config.types.ini;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import com.github.pjungermann.config.types.BaseConfigFactoryTest;

import java.io.IOException;

/**
 * Tests for {@link IniConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class IniConfigFactoryTest extends BaseConfigFactoryTest<IniConfigFactory> {

    @Override
    public String[] getSupportedTypes() {
        return new String[]{"ini"};
    }

    @Override
    public IniConfigFactory createFactory() throws Exception {
        IniConverter converter = new IniConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());

        IniConfigFactory factory = new IniConfigFactory();
        factory.setConverter(converter);

        return factory;
    }

    @Override
    public String getConfigFile() {
        return "config.ini";
    }

    @Override
    public void validateConfig(Config config, String profile, Config context) throws IOException {
        assertEquals(6, config.size());
        assertEquals("value1", config.get("ini_section.key1"));
        assertEquals("value2", config.get("ini_section.key2"));
        assertEquals("123", config.get("ini_section.foo"));
        assertEquals("sub_value1", config.get("ini_section.sub_section.sub1"));
        assertEquals("sub_value1", config.get("ini_section/sub_section.sub1"));
        assertEquals("value", config.get("another_section.another"));
    }

}
