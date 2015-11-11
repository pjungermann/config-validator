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
package com.github.pjungermann.config.types;

import com.github.pjungermann.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link FileTypeConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class FileTypeConfigFactoryTest {

    @Test
    public void supports_oneTypeAndNotSupportedFile_returnFalse() {
        FileTypeConfigFactory factory = new TestConfigFactory("ext");

        assertFalse(factory.supports(new File("not.supported")));
    }

    @Test
    public void supports_multipleTypesAndNotSupportedFile_returnFalse() {
        FileTypeConfigFactory factory = new TestConfigFactory("ext", "ext2", "ext3");

        assertFalse(factory.supports(new File("not.supported")));
    }

    @Test
    public void supports_oneTypeAndSupportedFile_returnTrue() {
        FileTypeConfigFactory factory = new TestConfigFactory("ext");

        assertTrue(factory.supports(new File("supported.file.ext")));
    }

    @Test
    public void supports_multipleTypesAndSupportedFileForFirst_returnTrue() {
        FileTypeConfigFactory factory = new TestConfigFactory("ext", "ext2", "ext3");

        assertTrue(factory.supports(new File("supported.file.ext")));
    }

    @Test
    public void supports_multipleTypesAndSupportedFileForAdditionalType_returnTrue() {
        FileTypeConfigFactory factory = new TestConfigFactory("ext", "ext2", "ext3");

        assertTrue(factory.supports(new File("supported.file.ext3")));
    }

    static class TestConfigFactory extends FileTypeConfigFactory {

        public TestConfigFactory(@NotNull String fileType, @NotNull String... moreFileTypes) {
            super(fileType, moreFileTypes);
        }

        @NotNull
        @Override
        protected ConfigConverter<Object> getConverter() {
            throw new UnsupportedOperationException();
        }

        @NotNull
        @Override
        protected Object doCreate(@NotNull File source, @Nullable String profile, @NotNull Config context) throws IOException {
            throw new UnsupportedOperationException();
        }
    }

}
