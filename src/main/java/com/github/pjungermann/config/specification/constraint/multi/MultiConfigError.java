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
package com.github.pjungermann.config.specification.constraint.multi;

import com.github.pjungermann.config.ConfigError;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;

import java.util.Collection;
import java.util.Locale;

/**
 * Wraps multiple {@link ConfigError errors} of a collection.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class MultiConfigError implements ConfigError {

    private final CollectionKey collectionKey;
    private final Collection<ConfigError> errors;

    public MultiConfigError(@NotNull final CollectionKey collectionKey,
                            @NotNull final Collection<ConfigError> errors) {
        this.collectionKey = collectionKey;
        this.errors = errors;
    }

    /**
     * The error message to be rendered.
     *
     * @return the error message to be rendered.
     */
    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        throw new UnsupportedOperationException("use #toMessage(...) instead");
    }

    /**
     * Renders the {@link #getMessage() message} using the
     * {@link MessageSource} and {@link Locale}.
     *
     * @param messageSource {@link MessageSource} to render it.
     * @param locale        {@link Locale} needed to render it.
     * @return the message.
     */
    @NotNull
    @Override
    public String toMessage(@NotNull final MessageSource messageSource, @NotNull final Locale locale) {
        final StringBuilder builder = new StringBuilder("validation errors for collection with key \"")
                .append(collectionKey.collectionKey)
                .append("\":");

        for (final ConfigError error : errors) {
            final String errorMessage = error.toMessage(messageSource, Locale.getDefault());
            builder.append("\n  - ").append(errorMessage);
        }

        return builder.toString();
    }
}
