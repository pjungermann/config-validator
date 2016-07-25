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
package com.github.pjungermann.config.specification;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.specification.types.TypeConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 * Tests for {@link ConfigSpecificationLoader}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class ConfigSpecificationLoaderTest {

    FakeConfigSpecificationLoader loader;

    @Before
    public void setUp() {
        loader = new FakeConfigSpecificationLoader();
    }

    @Test
    public void load_withFileStream_useRecursiveModeAsDefault() {
        ConfigSpecification specification = loader.load(
                Stream.of(new File("test.file.1"), new File("test.file.2"))
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertTrue(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    @Test
    public void load_withFileVarargs_convertToFileStreamAndUseRecursiveModeAsDefault() {
        ConfigSpecification specification = loader.load(
                new File("test.file.1"), new File("test.file.2")
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertTrue(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    @Test
    public void load_withPathVarargs_convertToFileStreamAndUseRecursiveModeAsDefault() {
        ConfigSpecification specification = loader.load(
                "test.file.1", "test.file.2"
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertTrue(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    @Test
    public void load_withFileVarargsAndRecursiveFalse_convertToFileStream() {
        ConfigSpecification specification = loader.load(
                false,
                new File("test.file.1"), new File("test.file.2")
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertFalse(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    @Test
    public void load_withFileVarargsAndRecursiveTrue_convertToFileStream() {
        ConfigSpecification specification = loader.load(
                true,
                new File("test.file.1"), new File("test.file.2")
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertTrue(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    @Test
    public void load_withPathVarargsAndRecursiveFalse_convertToFileStream() {
        ConfigSpecification specification = loader.load(
                false,
                "test.file.1", "test.file.2"
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertFalse(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    @Test
    public void load_withPathVarargsAndRecursiveTrue_convertToFileStream() {
        ConfigSpecification specification = loader.load(
                true,
                "test.file.1", "test.file.2"
        );

        assertNotNull(specification);
        assertEquals(1, loader.records.size());
        assertTrue(loader.records.get(0).recursive);
        assertEquals(2, loader.records.get(0).files.size());
        assertTrue(loader.records.get(0).files.contains(new File("test.file.1")));
        assertTrue(loader.records.get(0).files.contains(new File("test.file.2")));
    }

    static class FakeConfigSpecificationLoader implements ConfigSpecificationLoader {

        public List<Record> records = new ArrayList<>();

        @NotNull
        @Override
        public ConfigSpecification load(boolean recursive, @NotNull Stream<File> sourceStream) {
            Record record = new Record();
            record.recursive = recursive;
            record.files = sourceStream
                    .collect(toList());

            records.add(record);

            return new ConfigSpecification(new FakeTypeConverter(), emptyList(), emptyList());
        }
    }

    static class Record {
        public boolean recursive;
        public List<File> files;
    }

    static class FakeTypeConverter implements TypeConverter {

        @NotNull
        @Override
        public Set<String> getKeys() {
            return Collections.emptySet();
        }

        @Override
        public void register(@NotNull String key, @NotNull Class asType) {

        }

        @Override
        public boolean isConversionCommand(@NotNull String name) {
            return "fakeConvert".equals(name);
        }

        @Override
        public boolean isValidConversionConfig(@Nullable Object conversionConfig) {
            return false;
        }

        @Override
        public void convert(@NotNull Config config) {

        }
    }
}
