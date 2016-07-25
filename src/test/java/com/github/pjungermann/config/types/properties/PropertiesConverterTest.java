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
import org.junit.Test;

import java.io.IOException;
import java.util.Properties;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link PropertiesConverter}.
 *
 * @author Patrick Jungermann
 */
public class PropertiesConverterTest {

    @Test
    public void from_always_convertTheGivenPropertiesObjectIntoAConfigObject() {
        Properties properties = new Properties();
        properties.put("key", "value");
        properties.put("my.hierarchical.key", "another very,\nvery long value");
        properties.put("my.hierarchical.key2", "another long key");
        properties.put("empty.string", "");

        Config config = new PropertiesConverter().from(properties);

        assertEquals("value", config.get("key"));
        assertEquals("another very,\nvery long value", config.get("my.hierarchical.key"));
        assertEquals("another long key", config.get("my.hierarchical.key2"));
        assertEquals("", config.get("empty.string"));
    }

    @Test
    public void to_always_convertTheGivenConfigIntoAPropertiesObject() throws IOException {
        Config config = new Config();
        config.put("int", 123);
        config.put("long", 123L);
        config.put("double", 12.3D);
        config.put("hierarchical.key", "value");
        config.put("empty.string", "");

        Properties properties = new PropertiesConverter().to(config);

        assertEquals("123", properties.get("int"));
        assertEquals("123", properties.get("long"));
        assertEquals("12.3", properties.get("double"));
        assertEquals("value", properties.get("hierarchical.key"));
        assertEquals("", properties.get("empty.string"));
    }

    @Test
    public void to_nullValue_storeAsEmptyString() {
        Config config = new Config();
        config.put("null.value", null);

        Properties properties = new PropertiesConverter().to(config);

        assertEquals("", properties.get("null.value"));
    }

}
