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
package com.github.pjungermann.config.loader;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.loader.errors.NoSuchFileError;
import com.github.pjungermann.config.types.ConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultConfigLoader}.
 *
 * @author Patrick Jungermann
 */
public class DefaultConfigLoaderTest {

    static String PROFILE = "fake.profile";

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    DefaultConfigLoader loader;
    FakeConfigFactory factory;

    @Before
    public void setUp() {
        factory = new FakeConfigFactory();

        loader = new DefaultConfigLoader(source -> factory);
    }

    @Test
    public void load_fileDoesNotExist_noSuchFileError() {
        Config result = loader.load(null, new Config(), true, new File("does.not.exist"));

        assertTrue(result.isEmpty());
        assertEquals(1, result.errors.size());
        ConfigError error = result.errors.get(0);
        assertTrue(error instanceof NoSuchFileError);
    }

    @Test
    public void load_isFile_loadConfigFromFile() throws IOException {
        Config context = new Config();
        context.put("context.key", "context.value");
        File file = temporaryFolder.newFile();

        Config config = loader.load(PROFILE, context, true, file);

        // nothing added to the context
        assertEquals(1, context.size());
        assertEquals("context.value", context.get("context.key"));
        // file got loaded with correct infos
        assertEquals(1, config.size());
        assertTrue(config.containsKey(file.toString()));
        CreateRequest request = (CreateRequest) config.get(file.toString());
        assertRequest(file, PROFILE, context, request);
    }

    @Test
    public void load_isDirectoryAndRecursive_loadConfigFromAllFilesInItsFileTree() throws IOException {
        Config context = new Config();
        context.put("context.key", "context.value");
        File folder = temporaryFolder.newFolder();
        File file1 = new File(folder, "file1");
        assert file1.createNewFile();
        File subFolder = new File(folder, "sub");
        assert subFolder.mkdir();
        File file2 = new File(subFolder, "file2");
        assert file2.createNewFile();

        Config config = loader.load("fake.profile", context, true, folder);

        // file got loaded with correct infos
        assertEquals(2, config.size());
        assertTrue(config.containsKey(file1.toString()));
        assertTrue(config.containsKey(file2.toString()));
        CreateRequest request1 = (CreateRequest) config.get(file1.toString());
        assertRequest(file1, PROFILE, context, request1);
        CreateRequest request2 = (CreateRequest) config.get(file2.toString());
        assertRequest(file2, PROFILE, context, request2);
        // file inside of the folder got added,
        // therefore also to the context
        assertEquals(3, context.size());
        assertEquals("context.value", context.get("context.key"));
        assertSame(request1, context.get(file1.toString()));
        assertSame(request2, context.get(file2.toString()));
    }

    @Test
    public void load_isDirectoryAndNotRecursive_loadConfigFromAllFilesContained() throws IOException {
        Config context = new Config();
        context.put("context.key", "context.value");
        File folder = temporaryFolder.newFolder();
        File file1 = new File(folder, "file1");
        assert file1.createNewFile();
        File subFolder = new File(folder, "sub");
        assert subFolder.mkdir();
        File file2 = new File(subFolder, "file2");
        assert file2.createNewFile();

        Config config = loader.load("fake.profile", context, false, folder);

        // file got loaded with correct infos
        assertEquals(1, config.size());
        assertTrue(config.containsKey(file1.toString()));
        CreateRequest request = (CreateRequest) config.get(file1.toString());
        assertRequest(file1, PROFILE, context, request);
        // file inside of the folder got added,
        // therefore also to the context
        assertEquals(2, context.size());
        assertEquals("context.value", context.get("context.key"));
        assertSame(request, context.get(file1.toString()));
    }

    static void assertRequest(File source, String profile, Config context, CreateRequest request) {
        assertEquals(source, request.source);
        assertEquals(profile, request.profile);
        assertSame(context, request.context);
    }

    static class FakeConfigFactory implements ConfigFactory {

        @Override
        public boolean supports(@NotNull File source) {
            return true;
        }

        @NotNull
        @Override
        public Config create(@NotNull File source, @Nullable String profile, @NotNull Config context) throws IOException {
            CreateRequest request = new CreateRequest(source, profile, context);

            Config config = new Config();
            config.put(source.toString(), request);

            return config;
        }
    }

    static class CreateRequest {
        File source;
        String profile;
        Config context;

        public CreateRequest(File source, String profile, Config context) {
            this.source = source;
            this.profile = profile;
            this.context = context;
        }
    }
}
