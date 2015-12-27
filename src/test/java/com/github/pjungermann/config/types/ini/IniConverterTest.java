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
package com.github.pjungermann.config.types.ini;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import org.ini4j.BasicMultiMap;
import org.ini4j.Ini;
import org.ini4j.Profile;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link IniConverter}.
 *
 * @author Patrick Jungermann
 */
public class IniConverterTest {

    IniConverter converter;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        converter = new IniConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());
    }

    @Test
    public void from_to_always_isSame() throws IOException, NoSuchFieldException, IllegalAccessException {
        Ini ini = new Ini();
        ini.load(new File("src/test/resources/configs/config.ini"));
        ini.setComment(null);

        Ini out = converter.to(converter.from(ini));

        // order within the file is important as it is an LinkedHashMap inside of the library!
        assertEquals(toString(ini), toString(out));
    }

    @Test
    public void from_always_returnCorrectConfigWithAllEntries() throws IOException {
        Ini ini = new Ini();
        ini.load(new File("src/test/resources/configs/config.ini"));
        ini.setComment(null);

        Config config = converter.from(ini);

        assertEquals("value1", config.get("ini_section.key1"));
        assertEquals("value2", config.get("ini_section.key2"));
        assertEquals("123", config.get("ini_section.foo"));
        assertEquals("sub_value1", config.get("ini_section.sub_section.sub1"));
        assertEquals("value", config.get("another_section.another"));
    }

    @Test
    public void from_always_alsoProvideIniHierarchyNameFormattedKey() throws IOException {
        Ini ini = new Ini();
        ini.load(new File("src/test/resources/configs/config.ini"));
        ini.setComment(null);

        Config config = converter.from(ini);

        assertEquals("sub_value1", config.get("ini_section.sub_section.sub1"));
        // not really needed, but some might want to use the "ini"-way
        // therefore, let's provide both
        assertEquals("sub_value1", config.get("ini_section/sub_section.sub1"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void to_flatKeyWithoutSectionName_throwsException() {
        Config config = new Config();
        config.put("flat_key", "fails");

        converter.to(config);
    }

    @Test
    public void to_onlyKeyWithIniPathSeparator_ignoreThatKeyAsThose() {
        Config config = new Config();
        config.put("section/key", "ignore");

        Ini ini = converter.to(config);

        assertTrue(ini.isEmpty());
    }

    @Test
    public void to_keyWithIniPathSeparator_ignoreThatKeyAsThose() {
        Config config = new Config();
        config.put("section/key", "ignore");
        config.put("section.key", "used");

        Ini ini = converter.to(config);

        assertEquals(1, ini.size());
        assertEquals("used", ini.get("section").get("key"));
    }

    @Test
    public void to_validConfig_returnIniWithAllConfigEntriesAsStringsOnly() {
        Config config = new Config();
        config.put("section1.key1", "value1");
        config.put("section1.key2", "value2");
        config.put("section2.key3", "value3");
        config.put("section2.integer", 123);

        Ini ini = converter.to(config);

        assertEquals(2, ini.size());
        assertEquals(2, ini.get("section1").size());
        assertEquals("value1", ini.get("section1").get("key1"));
        assertEquals("value2", ini.get("section1").get("key2"));
        assertEquals(2, ini.get("section2").size());
        assertEquals("value3", ini.get("section2").get("key3"));
        assertEquals("123", ini.get("section2").get("integer"));
    }

    String toString(@NotNull final Ini ini) throws IOException, NoSuchFieldException, IllegalAccessException {
        enforceSortOrderByKey(ini);

        final StringWriter writer = new StringWriter();
        ini.store(writer);

        return writer.toString();
    }

    @SuppressWarnings("unchecked")
    void enforceSortOrderByKey(@NotNull final Ini ini) throws NoSuchFieldException, IllegalAccessException {
        final Field internalMapField = BasicMultiMap.class.getDeclaredField("_impl");
        internalMapField.setAccessible(true);

        final Map<String, List<Profile.Section>> internalMap =
                (Map<String, List<Profile.Section>>) internalMapField.get(ini);

        final TreeMap<String, List<Profile.Section>> replacement = new TreeMap<>();
        for (final Map.Entry<String, List<Profile.Section>> entry : internalMap.entrySet()) {

            for (final Profile.Section section : entry.getValue()) {
                final Map internalSectionMap = (Map) internalMapField.get(section);
                internalMapField.set(section, new TreeMap(internalSectionMap));
            }

            replacement.put(entry.getKey(), entry.getValue());
        }

        internalMapField.set(ini, replacement);
    }

}
