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
package com.github.pjungermann.config.errors;

import org.junit.Test;

import java.io.File;

import static com.github.pjungermann.config.errors.ConfigFileError.DEFAULT_MESSAGE_CODE;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ConfigFileError}.
 *
 * @author Patrick Jungermann
 */
public class ConfigFileErrorTest extends AbstractConfigFileErrorTest<ConfigFileError> {

    @Override
    public ConfigFileError getError(File file) {
        return new DummyError(file);
    }

    @Override
    public String getExpectedNameArgument() {
        return "dummy";
    }

    @Test
    public void DEFAULT_MESSAGE_CODE_always_isExpectedCode() {
        assertEquals("errors.config_file.default", DEFAULT_MESSAGE_CODE);
    }

    static class DummyError extends ConfigFileError {

        public DummyError(File file) {
            super(file);
        }

    }

}
