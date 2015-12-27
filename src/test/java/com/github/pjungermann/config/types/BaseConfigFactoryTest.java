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

import com.github.pjungermann.config.CollectedAssertions;
import com.github.pjungermann.config.Config;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

/**
 * Basic reusable setup for testing {@link ConfigFactory} implementations.
 *
 * @author Patrick Jungermann
 */
public abstract class BaseConfigFactoryTest<FactoryClass extends ConfigFactory> extends CollectedAssertions {

    public final File configResourceFolder = new File("src/test/resources/configs");

    public File sourceFile;

    public FactoryClass factory;

    public String[] supportedTypes;

    public abstract FactoryClass createFactory() throws Exception;

    public abstract String getConfigFile();

    public abstract void validateConfig(Config config, String profile, Config context) throws IOException;

    public abstract String[] getSupportedTypes();

    public File createSourceFile() {
        return new File(configResourceFolder, getConfigFile());
    }

    @Before
    public void setUp() throws Exception {
        supportedTypes = getSupportedTypes();
        Assert.assertTrue(supportedTypes != null && supportedTypes.length > 0);

        sourceFile = createSourceFile();
        factory = createFactory();
    }

    @Test
    public void supports_always_trueForSupportedTypes() {
        for (String type : supportedTypes) {
            assertTrue(factory.supports(new File("file." + type.toLowerCase())));
            assertTrue(factory.supports(new File("file." + type.toUpperCase())));
        }

        assertFalse(factory.supports(new File("file." + supportedTypes[0] + "_")));
    }

    @Test
    public void create_noProfileAndEmptyContext_validConfig() throws IOException, ConfigConversionException {
        create_args_validConfig(null, new Config());
    }

    @Test
    public void create_profileAndEmptyContext_validConfig() throws IOException, ConfigConversionException {
        create_args_validConfig("my_profile", new Config());
    }

    @Test
    public void create_noProfileAndContext_validConfig() throws IOException, ConfigConversionException {
        Config context = new Config();
        context.put("context.key", "context.value");

        create_args_validConfig(null, context);
    }

    @Test
    public void create_profileAndContext_validConfig() throws IOException, ConfigConversionException {
        Config context = new Config();
        context.put("context.key", "context.value");

        create_args_validConfig("my_profile", context);
    }

    public void create_args_validConfig(String profile, Config context) throws IOException, ConfigConversionException {
        Config config = factory.create(sourceFile, profile, context);

        validateConfig(config, profile, context);
    }

}
