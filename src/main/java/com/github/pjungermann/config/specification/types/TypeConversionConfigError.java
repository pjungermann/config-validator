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

package com.github.pjungermann.config.specification.types;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Used when the config for a {@link TypeConverter type conversion}
 * have been invalid.
 *
 * @author Patrick Jungermann
 */
public class TypeConversionConfigError implements ConfigError {

    public static final String MESSAGE_CODE = "errors.type_conversion_settings";

    public final String key;
    public final Object config;
    public final SourceLine sourceLine;

    public TypeConversionConfigError(@NotNull final String key,
                                     @Nullable final Object config,
                                     @NotNull final SourceLine sourceLine) {
        this.key = key;
        this.config = config;
        this.sourceLine = sourceLine;
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        return new DefaultMessageSourceResolvable(
                new String[]{
                        MESSAGE_CODE
                },
                new Object[]{
                        key,
                        sourceLine,
                        config
                },
                MESSAGE_CODE
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "(key=" + key +
                ", config=" + config +
                ", sourceLine=" + sourceLine +
                ")";
    }
}
