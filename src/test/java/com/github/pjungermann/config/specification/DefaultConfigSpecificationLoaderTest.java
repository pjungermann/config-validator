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
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.reader.SpecificationPartial;
import com.github.pjungermann.config.specification.reader.SpecificationReader;
import com.github.pjungermann.config.specification.types.TypeConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultConfigSpecificationLoader}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class DefaultConfigSpecificationLoaderTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    FakeTypeConverter converter;
    FakeSpecificationReader reader;
    DefaultConfigSpecificationLoader loader;

    @Before
    public void setUp() {
        converter = new FakeTypeConverter();
        reader = new FakeSpecificationReader();
        loader = new DefaultConfigSpecificationLoader(converter, reader);
    }

    @Test
    public void load_nonRecursiveAndEmptyStream_emptySpecification() {
        ConfigSpecification specification = loader.load(false, Stream.empty());

        assertSame(converter, specification.typeConverter);
        assertTrue(specification.constraints.isEmpty());
        assertTrue(specification.errors.isEmpty());
    }

    @Test
    public void load_recursiveAndEmptyStream_emptySpecification() {
        ConfigSpecification specification = loader.load(true, Stream.empty());

        assertSame(converter, specification.typeConverter);
        assertTrue(specification.constraints.isEmpty());
        assertTrue(specification.errors.isEmpty());
    }

    @Test
    public void load_nonRecursiveAndOneFile_specificationFromThatFile() throws IOException {
        File file = temporaryFolder.newFile();
        ConfigSpecification specification = loader.load(false, Stream.of(file));

        assertSame(converter, specification.typeConverter);
        assertEquals(1, specification.constraints.size());
        assertEquals(file, specification.constraints.iterator().next().definedAt().file);
        assertEquals(1, specification.errors.size());
        assertEquals(file, ((FakeConfigError) specification.errors.iterator().next()).file);
    }

    @Test
    public void load_recursiveAndOneFile_specificationFromThatFile() throws IOException {
        File file = temporaryFolder.newFile();
        ConfigSpecification specification = loader.load(true, Stream.of(file));

        assertSame(converter, specification.typeConverter);
        assertEquals(1, specification.constraints.size());
        assertEquals(file, specification.constraints.iterator().next().definedAt().file);
        assertEquals(1, specification.errors.size());
        assertEquals(file, ((FakeConfigError) specification.errors.iterator().next()).file);
    }

    @Test
    public void load_nonRecursiveAndDirWithFiles_specificationFromAllFiles() throws IOException {
        File dir = temporaryFolder.newFolder();
        File file1 = new File(dir, "file1.suffix");
        File file2 = new File(new File(dir, "sub-dir"), "file2.suffix");
        assertTrue(file1.createNewFile());
        assertTrue(file2.getParentFile().mkdir());
        assertTrue(file2.createNewFile());

        ConfigSpecification specification = loader.load(false, Stream.of(dir));

        assertSame(converter, specification.typeConverter);
        assertEquals(1, specification.constraints.size());
        Iterator<Constraint> constraintIterator = specification.constraints.iterator();
        assertEquals(file1, constraintIterator.next().definedAt().file);
        assertEquals(1, specification.errors.size());
        Iterator<ConfigError> errorIterator = specification.errors.iterator();
        assertEquals(file1, ((FakeConfigError) errorIterator.next()).file);
    }

    @Test
    public void load_recursiveAndDirWithFiles_specificationFromAllFiles() throws IOException {
        File dir = temporaryFolder.newFolder();
        File file1 = new File(dir, "file1.suffix");
        File file2 = new File(new File(dir, "sub-dir"), "file2.suffix");
        assertTrue(file1.createNewFile());
        assertTrue(file2.getParentFile().mkdir());
        assertTrue(file2.createNewFile());

        ConfigSpecification specification = loader.load(true, Stream.of(dir));

        assertSame(converter, specification.typeConverter);
        assertEquals(2, specification.constraints.size());
        List<File> files = specification.constraints.stream()
                .map(constraint -> constraint.definedAt().file)
                .collect(toList());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
        assertEquals(2, specification.errors.size());
        files = specification.errors.stream()
                .map(configError -> ((FakeConfigError) configError).file)
                .collect(toList());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
    }

    @Test
    public void load_nonRecursiveAndFileAndDirWithFiles_specificationFromAllFiles() throws IOException {
        File dir = temporaryFolder.newFolder();
        File file1 = new File(dir, "file1.suffix");
        File file2 = new File(new File(dir, "sub-dir"), "file2.suffix");
        assertTrue(file1.createNewFile());
        assertTrue(file2.getParentFile().mkdir());
        assertTrue(file2.createNewFile());
        File file3 = temporaryFolder.newFile();

        ConfigSpecification specification = loader.load(false, Stream.of(dir, file3));

        assertSame(converter, specification.typeConverter);
        assertEquals(2, specification.constraints.size());
        List<File> files = specification.constraints.stream()
                .map(constraint -> constraint.definedAt().file)
                .collect(toList());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file3));
        assertEquals(2, specification.errors.size());
        files = specification.errors.stream()
                .map(configError -> ((FakeConfigError) configError).file)
                .collect(toList());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file3));
    }

    @Test
    public void load_recursiveAndFileAndDirWithFiles_specificationFromAllFiles() throws IOException {
        File dir = temporaryFolder.newFolder();
        File file1 = new File(dir, "file1.suffix");
        File file2 = new File(new File(dir, "sub-dir"), "file2.suffix");
        assertTrue(file1.createNewFile());
        assertTrue(file2.getParentFile().mkdir());
        assertTrue(file2.createNewFile());
        File file3 = temporaryFolder.newFile();

        ConfigSpecification specification = loader.load(true, Stream.of(dir, file3));

        assertSame(converter, specification.typeConverter);
        assertEquals(3, specification.constraints.size());
        List<File> files = specification.constraints.stream()
                .map(constraint -> constraint.definedAt().file)
                .collect(toList());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
        assertTrue(files.contains(file3));
        assertEquals(3, specification.errors.size());
        files = specification.errors.stream()
                .map(configError -> ((FakeConfigError) configError).file)
                .collect(toList());
        assertTrue(files.contains(file1));
        assertTrue(files.contains(file2));
        assertTrue(files.contains(file3));
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

    static class FakeSpecificationReader implements SpecificationReader {

        @Override
        public SpecificationPartial apply(File file) {
            return new SpecificationPartial(
                    singletonList(new FakeConstraint(file)),
                    singletonList(new FakeConfigError(file))
            );
        }
    }

    static class FakeConstraint implements Constraint {

        private File file;

        public FakeConstraint(File file) {
            this.file = file;
        }

        /**
         * @return where it got defined at (e.g. within which {@link ConfigSpecification} file).
         */
        @NotNull
        @Override
        public SourceLine definedAt() {
            return new SourceLine(file, -1);
        }

        /**
         * @return the config key to which it has to be applied to.
         */
        @NotNull
        @Override
        public String getKey() {
            return "fake";
        }

        /**
         * @param type type of a config value.
         * @return whether the type is supported or not.
         */
        @Override
        public boolean supports(Class type) {
            return false;
        }

        /**
         * @param config the config to be validated.
         * @return any validation errors or {@code null}.
         */
        @Nullable
        @Override
        public ConfigError validate(@NotNull Config config) {
            return null;
        }

        /**
         * @param value the rejected config value.
         * @return the error message for the rejected value.
         */
        @NotNull
        @Override
        public MessageSourceResolvable getMessage(@Nullable Object value) {
            return new DefaultMessageSourceResolvable(value == null ? "<null>" : value.toString());
        }

        @Override
        public int compareTo(@NotNull Constraint o) {
            return this.equals(o) ? 0 : -1;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }

    static class FakeConfigError implements ConfigError {

        public final File file;

        public FakeConfigError(@NotNull final File file) {
            this.file = file;
        }

        @NotNull
        @Override
        public MessageSourceResolvable getMessage() {
            return new DefaultMessageSourceResolvable("fake");
        }
    }
}
