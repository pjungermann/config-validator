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
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.*;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConfigLoader}.
 *
 * @author Patrick Jungermann
 */
@RunWith(Parameterized.class)
public class ConfigLoaderTest {

    @Parameterized.Parameters(name = "{0},{1}")
    public static Object[] getRecursiveValues() {
        Object[] recursiveValues = new Object[]{false, true};
        Object[] sourcesValues = new Object[]{
                // lists for a better readability inside of the test names
                Collections.singletonList("single-source"),
                Arrays.asList("source1", "source2")
        };

        Object[][] data = new Object[recursiveValues.length * sourcesValues.length][];
        for (int r = 0; r < recursiveValues.length; r++) {
            for (int s = 0; s < sourcesValues.length; s++) {
                data[r * sourcesValues.length + s] = new Object[]{
                        recursiveValues[r],
                        sourcesValues[s]
                };
            }
        }

        return data;
    }

    private boolean recursive;
    private String[] sources;

    ConfigLoader loader;

    public ConfigLoaderTest(boolean recursive, Collection<String> sources) {
        this.recursive = recursive;
        this.sources = sources.toArray(new String[sources.size()]);
    }

    @Before
    public void setUp() {
        loader = (profile, context, recursive, source) -> {
            Config sourceConfig = new Config();
            sourceConfig.put("recursive", recursive);
            sourceConfig.put("profile", profile);
            sourceConfig.put("context.keys", context.keySet());

            Config config = new Config();
            config.put(source.toString(), sourceConfig);

            return config;
        };
    }

    @Test
    public void DEFAULT_PROFILE_always_isNull() {
        assertNull(ConfigLoader.DEFAULT_PROFILE);
    }

    @Test
    public void DEFAULT_RECURSIVE_always_isTrue() {
        assertTrue(ConfigLoader.DEFAULT_RECURSIVE);
    }

    @Test
    public void getDefaultProfile_always_isNull() {
        assertNull(loader.getDefaultProfile());
    }

    @Test
    public void getDefaultRecursive_always_isTrue() {
        assertTrue(loader.getDefaultRecursive());
    }

    @Test
    public void getDefaultContext_always_createNewEmptyConfig() {
        Config context = loader.getDefaultContext();
        assertTrue(context.isEmpty());

        Config another = loader.getDefaultContext();
        assertNotSame(context, another);
    }

    @Test
    public void load_stringSourceOnly_useDefaultsForTheRest() {
        Config result = loader.load("source1");

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                "source1");
    }

    @Test
    public void load_stringSourceAndProfileOnly_useDefaultsForTheRest() {
        Config result = loader.load(
                "fake.profile", "source1"
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                "source1");
    }

    @Test
    public void load_stringSourceAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load(
                recursive, "source1"
        );

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                recursive,
                "source1");
    }

    @Test
    public void load_stringSourceAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                context, "source1"
        );

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                loader.getDefaultRecursive(),
                "source1");
    }

    @Test
    public void load_stringSourceAndProfileAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load(
                "fake.profile", recursive, "source1"
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                recursive,
                "source1");
    }

    @Test
    public void load_stringSourceAndProfileAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                "fake.profile", context, "source1"
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                loader.getDefaultRecursive(),
                "source1");
    }

    @Test
    public void load_stringSourceAndRecursiveAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                context, recursive, "source1"
        );

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                recursive,
                "source1");
    }

    @Test
    public void load_stringSourceAndProfileAndRecursiveAndContextOnly_loadFileSource() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                "fake.profile", context, recursive, "source1"
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                recursive,
                "source1");
    }

    @Test
    public void load_stringSourcesOnly_useDefaultsForTheRest() {
        Config result = loader.load(sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_stringSourcesAndProfileOnly_useDefaultsForTheRest() {
        Config result = loader.load("fake.profile", sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_stringSourcesAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load(recursive, sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                recursive,
                sources);
    }

    @Test
    public void load_stringSourcesAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(context, sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_stringSourcesAndProfileAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load("fake.profile", recursive, sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                recursive,
                sources);
    }

    @Test
    public void load_stringSourcesAndProfileAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load("fake.profile", context, sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_stringSourcesAndRecursiveAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(context, recursive, sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                recursive,
                sources);
    }

    @Test
    public void load_stringSourcesAndProfileAndRecursiveAndContextOnly_loadAllFileSources() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load("fake.profile", context, recursive, sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                recursive,
                sources);
    }

    @Test
    public void load_fileSourceOnly_useDefaultsForTheRest() {
        Config result = loader.load(new File("source1"));

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                new File("source1"));
    }

    @Test
    public void load_fileSourceAndProfileOnly_useDefaultsForTheRest() {
        Config result = loader.load(
                "fake.profile", new File("source1")
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                new File("source1"));
    }

    @Test
    public void load_fileSourceAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load(
                recursive, new File("source1")
        );

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                recursive,
                new File("source1"));
    }

    @Test
    public void load_fileSourceAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                context, new File("source1")
        );

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                loader.getDefaultRecursive(),
                new File("source1"));
    }

    @Test
    public void load_fileSourceAndProfileAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load(
                "fake.profile", recursive, new File("source1")
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                recursive,
                new File("source1"));
    }

    @Test
    public void load_fileSourceAndProfileAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                "fake.profile", context, new File("source1")
        );

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                loader.getDefaultRecursive(),
                new File("source1"));
    }

    @Test
    public void load_fileSourceAndRecursiveAndContextOnly_useDefaultsForTheRest() {
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(
                context, recursive, new File("source1")
        );

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                recursive,
                new File("source1"));
    }

    @Test
    public void load_fileSourcesOnly_useDefaultsForTheRest() {
        File[] sources = fileSources();
        Config result = loader.load(sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_fileSourcesAndProfileOnly_useDefaultsForTheRest() {
        File[] sources = fileSources();
        Config result = loader.load("fake.profile", sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_fileSourcesAndRecursiveOnly_useDefaultsForTheRest() {
        Config result = loader.load(recursive, fileSources());

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                loader.getDefaultContext(),
                recursive,
                sources);
    }

    @Test
    public void load_fileSourcesAndContextOnly_useDefaultsForTheRest() {
        File[] sources = fileSources();
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(context, sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_fileSourcesAndProfileAndRecursiveOnly_useDefaultsForTheRest() {
        File[] sources = fileSources();
        Config result = loader.load("fake.profile", recursive, sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                loader.getDefaultContext(),
                recursive,
                sources);
    }

    @Test
    public void load_fileSourcesAndProfileAndContextOnly_useDefaultsForTheRest() {
        File[] sources = fileSources();
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load("fake.profile", context, sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                loader.getDefaultRecursive(),
                sources);
    }

    @Test
    public void load_fileSourcesAndRecursiveAndContextOnly_useDefaultsForTheRest() {
        File[] sources = fileSources();
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load(context, recursive, sources);

        assertLoaderArgs(
                result,
                loader.getDefaultProfile(),
                context,
                recursive,
                sources);
    }

    @Test
    public void load_fileSourcesAndProfileAndRecursiveAndContextOnly_loadAllFileSources() {
        File[] sources = fileSources();
        Config context = new Config();
        context.put("fake", "context");

        Config result = loader.load("fake.profile", context, recursive, sources);

        assertLoaderArgs(
                result,
                "fake.profile",
                context,
                recursive,
                sources);
    }

    File[] fileSources() {
        File[] files = new File[sources.length];

        for (int i = 0; i < sources.length; i++) {
            files[i] = new File(sources[i]);
        }

        return files;
    }

    static void assertLoaderArgs(Config result, String profile, Config context, boolean recursive, String source) {
        assertLoaderArgs(result, profile, recursive, context.keySet(), source);
    }

    static void assertLoaderArgs(Config result, String profile, Config context, boolean recursive, String... sources) {
        Set<String> expectedContextKeys = new HashSet<>();
        expectedContextKeys.addAll(Arrays.asList(sources));
        expectedContextKeys.addAll(context.keySet());

        for (String source : sources) {
            assertLoaderArgs(result, profile, recursive, expectedContextKeys, source);
        }
    }

    @SuppressWarnings("unchecked")
    static void assertLoaderArgs(Config result, String profile, boolean recursive, Set<String> expectedContextKeys, String source) {
        Config sourceResult = (Config) result.get(source);

        assertEquals(profile, sourceResult.get("profile"));
        assertEquals(recursive, sourceResult.get("recursive"));

        Set<String> contextKeys = (Set<String>) sourceResult.get("context.keys");
        assertEquals(expectedContextKeys, contextKeys);
    }

    static void assertLoaderArgs(Config result, String profile, Config context, boolean recursive, File source) {
        assertLoaderArgs(result, profile, recursive, context.keySet(), source);
    }

    static void assertLoaderArgs(Config result, String profile, Config context, boolean recursive, File... sources) {
        Set<String> expectedContextKeys = new HashSet<>();
        for (File source : sources) {
            expectedContextKeys.add(source.toString());
        }
        expectedContextKeys.addAll(context.keySet());

        for (File source : sources) {
            assertLoaderArgs(result, profile, recursive, expectedContextKeys, source);
        }
    }

    @SuppressWarnings("unchecked")
    static void assertLoaderArgs(Config result, String profile, boolean recursive, Set<String> expectedContextKeys, File source) {
        Config sourceResult = (Config) result.get(source.toString());

        assertEquals(profile, sourceResult.get("profile"));
        assertEquals(recursive, sourceResult.get("recursive"));

        Set<String> contextKeys = (Set<String>) sourceResult.get("context.keys");
        assertEquals(expectedContextKeys, contextKeys);
    }

}
