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
package com.github.pjungermann.config;

import org.junit.Test;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * Tests for {@link Config}.
 *
 * @author Patrick Jungermann
 */
public class ConfigTest {

    @Test
    public void constructor_always_hasErrorList() {
        assertTrue(new Config().errors.isEmpty());
    }

    @Test
    public void putAndGetConfigEntry() {
        Config config = new Config();
        config.put("config.key", "config.value");

        assertEquals("config.value", config.get("config.key"));
    }

    @Test
    public void constructor_otherMao_copyEntries() {
        Map<String, Object> other = new HashMap<>();
        other.put("other.key", new File("value"));

        Config config = new Config(other);
        assertEquals(new File("value"), other.get("other.key"));
        assertTrue(config.errors.isEmpty());
    }

    @Test
    public void constructor_otherConfig_copyEntriesAndErrors() {
        Config other = new Config();
        other.put("other.key", 123);
        ConfigError error = () -> new DefaultMessageSourceResolvable("my.fake.code");
        other.errors.add(error);

        Config config = new Config(other);
        assertEquals(123, config.get("other.key"));
        assertEquals(1, config.errors.size());
        assertSame(error, config.errors.get(0));
    }

    @Test
    public void constructor_putAllOfOtherConfig_copyEntriesAndErrors() {
        Config other = new Config();
        other.put("other.key", 123);
        ConfigError error = () -> new DefaultMessageSourceResolvable("my.fake.code");
        other.errors.add(error);

        Config config = new Config();
        config.putAll(other);

        assertEquals(123, config.get("other.key"));
        assertEquals(1, config.errors.size());
        assertSame(error, config.errors.get(0));
    }

}
