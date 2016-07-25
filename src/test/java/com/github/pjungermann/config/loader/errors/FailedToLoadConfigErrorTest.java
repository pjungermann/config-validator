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
package com.github.pjungermann.config.loader.errors;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.errors.AbstractConfigFileErrorTest;
import com.github.pjungermann.config.types.ConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link FailedToLoadConfigError}.
 *
 * @author Patrick Jungermann
 */
public class FailedToLoadConfigErrorTest extends AbstractConfigFileErrorTest<FailedToLoadConfigError> {

    @Override
    public FailedToLoadConfigError getError(File file) {
        return new FailedToLoadConfigError(file, new FakeConfigFactory(), new FakeException());
    }

    @Override
    public String getExpectedNameArgument() {
        return "failed to load config";
    }

    @Override
    public void getMessageCode_always_returnsExpectedCode() {
        assertEquals("errors.config_file.failed_to_load", error.getMessageCode());
    }

    @Override
    public void getMessageArguments_always_expectedArguments() throws IOException {
        Object[] arguments = error.getMessageArguments();

        super.getMessageArguments_always_expectedArguments();
        assertEquals(FakeConfigFactory.class.getName(), arguments[2]);
        assertEquals(error.cause.toString(), arguments[3]);
    }

    @Override
    public String getExpectedToStringValue(FailedToLoadConfigError error) throws IOException {
        return super.getExpectedToStringValue(error)
                + " via "
                + error.factory.toString();
    }

    static class FakeConfigFactory implements ConfigFactory {

        @Override
        public boolean supports(@NotNull File source) {
            return false;
        }

        @NotNull
        @Override
        public Config create(@NotNull File source,
                             @Nullable String profile,
                             @NotNull Config context) throws IOException {
            return new Config();
        }

    }

    static class FakeException extends Exception {}

}
