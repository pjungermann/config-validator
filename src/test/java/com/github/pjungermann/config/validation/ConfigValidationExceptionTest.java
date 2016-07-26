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
package com.github.pjungermann.config.validation;

import com.github.pjungermann.config.ConfigError;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.StaticMessageSource;

import java.util.ArrayList;
import java.util.Locale;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ConfigValidationException}.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class ConfigValidationExceptionTest {

    @Test
    public void getMessage_errors_exceptionWithListOfConfigErrorMessages() {
        StaticMessageSource messageSource = new StaticMessageSource();
        messageSource.addMessage("fake.config.error.A", Locale.getDefault(), "Error A happened: {0}");
        messageSource.addMessage("fake.config.error.B", Locale.getDefault(), "Error B happened");

        ArrayList<ConfigError> errors = new ArrayList<>();
        errors.add(new FakeConfigErrorA(123));
        errors.add(new FakeConfigErrorA("2nd"));
        errors.add(new FakeConfigErrorB());

        ConfigValidationException exception = new ConfigValidationException(messageSource, errors);

        assertEquals(
                "Validation errors:\n" +
                "- Error A happened: 123\n" +
                "- Error A happened: 2nd\n" +
                "- Error B happened",
                exception.getMessage());
    }

    @Test
    public void getMessage_noErrors_justErrorHeader() {
        StaticMessageSource messageSource = new StaticMessageSource();
        ArrayList<ConfigError> errors = new ArrayList<>();

        ConfigValidationException exception = new ConfigValidationException(messageSource, errors);

        assertEquals(
                "Validation errors:",
                exception.getMessage());
    }

    static class FakeConfigErrorA implements ConfigError {

        Object arg;

        public FakeConfigErrorA(Object arg) {
            this.arg = arg;
        }

        /**
         * The error message to be rendered.
         *
         * @return the error message to be rendered.
         */
        @NotNull
        @Override
        public MessageSourceResolvable getMessage() {
            return new DefaultMessageSourceResolvable(
                    new String[]{"fake.config.error.A"},
                    new Object[]{arg}
            );
        }
    }

    static class FakeConfigErrorB implements ConfigError {

        /**
         * The error message to be rendered.
         *
         * @return the error message to be rendered.
         */
        @NotNull
        @Override
        public MessageSourceResolvable getMessage() {
            return new DefaultMessageSourceResolvable("fake.config.error.B");
        }
    }
}
