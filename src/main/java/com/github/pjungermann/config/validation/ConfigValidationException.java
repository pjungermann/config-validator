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
import com.github.pjungermann.config.specification.ConfigSpecification;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;

import java.util.Collection;
import java.util.Locale;

/**
 * Thrown if any requirement from the {@link ConfigSpecification} was violated.
 *
 * The exception message will contain a pretty formatted list of {@link ConfigError} messages.
 *
 * @author Patrick Jungermann
 */
public class ConfigValidationException extends Exception {

    public ConfigValidationException(@NotNull final MessageSource messageSource,
                                     @NotNull final Collection<ConfigError> errors) {
        super(toMessage(messageSource, errors));
    }

    public static String toMessage(@NotNull final MessageSource messageSource,
                                   @NotNull final Collection<ConfigError> errors) {
        final StringBuilder builder = new StringBuilder("Validation errors:");

        for (final ConfigError error : errors) {
            final String errorMessage = error.toMessage(messageSource, Locale.getDefault());
            builder.append("\n- ").append(errorMessage);
        }

        return builder.toString();
    }
}
