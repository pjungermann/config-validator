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

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link AbstractConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class AbstractConfigFactoryTest {

    @Test
    public void create_always_createOtherConfigTypeAndConvertIt() throws IOException {
        TestConfigFactory factory = new TestConfigFactory();
        Config context = new Config();
        context.put("context.key", "context.value");

        Config config = factory.create(new File("fake.source"), "fake.profile", context);

        assertEquals(
                "{convertible.value=doCreate(source=fake.source, profile=fake.profile, context={context.key=context.value})}",
                config.toString().replaceAll("\\n|\\t", ""));
    }

    static class TestConfigFactory extends AbstractConfigFactory<FakeConfigType> {

        @NotNull
        @Override
        protected ConfigConverter<FakeConfigType> getConverter() {
            return new FakeConverter();
        }

        @NotNull
        @Override
        protected FakeConfigType doCreate(@NotNull File source, @Nullable String profile, @NotNull Config context) throws IOException {
            return new FakeConfigType("doCreate(source=" + source + ", profile=" + profile + ", context=" + context + ")");
        }

        @Override
        public boolean supports(@NotNull File source) {
            return true;
        }
    }

    static class FakeConverter implements ConfigConverter<FakeConfigType> {

        @NotNull
        @Override
        public Config from(@NotNull FakeConfigType convertible) {
            Config config = new Config();
            config.put("convertible.value", convertible.value);

            return config;
        }

        @NotNull
        @Override
        public FakeConfigType to(@NotNull Config config) {
            return new FakeConfigType(config.toString());
        }
    }

    static class FakeConfigType {
        String value;

        FakeConfigType(String value) {
            this.value = value;
        }
    }
}
