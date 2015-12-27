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
package com.github.pjungermann.config.types.groovy;

import com.github.pjungermann.config.CollectedAssertions;
import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import com.github.pjungermann.config.types.ConfigConversionException;
import groovy.lang.Closure;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Tests for {@link ConfigObjectConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class ConfigObjectConfigFactoryTest extends CollectedAssertions {

    final File configResourceFolder = new File("src/test/resources/configs");
    final File sourceFile = new File(configResourceFolder, "config.groovy");
    final File sourceFileWithContextReference = new File(configResourceFolder, "config_with_context_reference.groovy");

    ConfigObjectConfigFactory factory;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        ConfigObjectConverter converter = new ConfigObjectConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());

        factory = new ConfigObjectConfigFactory();
        factory.setConverter(converter);
    }

    @Test(expected = FileNotFoundException.class)
    public void create_nonExistentSourceFile_configIsCorrect() throws IOException, ConfigConversionException {
        create_args_configIsCorrect(new File("does/not/exist.groovy"), null, new Config(), "default", "second");
    }

    @Test
    public void create_emptyContextAndNoProfile_configIsCorrect() throws IOException, ConfigConversionException {
        create_args_configIsCorrect(sourceFile, null, new Config(), "default", "second");
    }

    @Test
    public void create_emptyContextAndExistingProfile_configIsCorrect() throws IOException, ConfigConversionException {
        create_args_configIsCorrect(sourceFile, "environment1", new Config(), "by-environment1", "by-environment1");
    }

    @Test
    public void create_emptyContextAndNonExistingProfile_configIsCorrect() throws IOException, ConfigConversionException {
        create_args_configIsCorrect(sourceFile, "noSuchProfile", new Config(), "default", "second");
    }

    @Test
    public void create_nonEmptyContextWithoutReferenceAndNoProfile_configIsCorrect() throws IOException, ConfigConversionException {
        Config context = new Config();
        context.put("context.key", "context.value");

        create_args_configIsCorrect(sourceFile, null, context, "default", "second");
    }

    @Test
    public void create_nonEmptyContextWithoutReferenceAndExistingProfile_configIsCorrect() throws IOException, ConfigConversionException {
        Config context = new Config();
        context.put("context.key", "context.value");

        create_args_configIsCorrect(sourceFile, "environment1", context, "by-environment1", "by-environment1");
    }

    @Test
    public void create_nonEmptyContextWithoutReferenceAndNonExistingProfile_configIsCorrect() throws IOException, ConfigConversionException {
        Config context = new Config();
        context.put("context.key", "context.value");

        create_args_configIsCorrect(sourceFile, "noSuchProfile", context, "default", "second");
    }

    @Test
    public void create_nonEmptyContextWithReferenceAndNoProfile_configIsCorrect() throws IOException, ConfigConversionException {
        create_withContext_configIsCorrect(sourceFileWithContextReference, null, "default");
    }

    @Test
    public void create_nonEmptyContextWithReferenceAndExistingProfile_configIsCorrect() throws IOException, ConfigConversionException {
        create_withContext_configIsCorrect(sourceFileWithContextReference, "environment1", "by-environment1");
    }

    @Test
    public void create_nonEmptyContextWithReferenceAndNonExistingProfile_configIsCorrect() throws IOException, ConfigConversionException {
        create_withContext_configIsCorrect(sourceFileWithContextReference, "noSuchProfile", "default");
    }

    void create_withContext_configIsCorrect(File file, String profile, String envAware) throws IOException, ConfigConversionException {
        Config context = factory.create(sourceFile, profile, new Config());
        Config config = factory.create(file, profile, context);

        String expectation = "value " + envAware + " via dependency";
        assertEquals(expectation, config.get("with_flat_key_dependency"));
        assertEquals(expectation, config.get("with_hierarchical_key_dependency"));
    }

    void create_args_configIsCorrect(File file,
                                     String profile,
                                     Config context,
                                     String envAware,
                                     String envOverride) throws IOException, ConfigConversionException {
        Config config = factory.create(file, profile, context);

        assertEquals(envAware, config.get("env.aware"));
        assertEquals(envOverride, config.get("env.override"));
        assertEquals(2147483647, config.get("int_value"));
        assertEquals(3.4028235E38F, config.get("float_value"));
        assertEquals(1.7976931348623157E308D, config.get("double_value"));
        assertTrue(config.get("list") instanceof List);
        assertEquals(1, ((List) config.get("list")).get(0));
        assertEquals(2, ((List) config.get("list")).get(1));
        assertEquals(3, ((List) config.get("list")).get(2));
        assertTrue(config.get("closure") instanceof Closure);

        // context / binding is not included into the config itself
        assertFalse(config.containsKey("context.key"));
    }

}
