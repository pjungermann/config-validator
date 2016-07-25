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
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Used when the type conversion using a {@link TypeConverter} failed.
 *
 * @author Patrick Jungermann
 */
public class TypeConversionFailedError implements ConfigError {

    public static final String MESSAGE_CODE = "errors.type_conversion.failed";

    public final String key;
    public final Object value;
    public final Object type;
    public final Exception cause;

    public TypeConversionFailedError(@NotNull final String key,
                                     @Nullable final Object value,
                                     @NotNull final Object type,
                                     @NotNull final Exception cause) {
        this.key = key;
        this.value = value;
        this.type = type;
        this.cause = cause;
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
                        value,
                        type,
                        cause
                },
                MESSAGE_CODE
        );
    }

    @Override
    public String toString() {
        Class valueClass = value == null ? null : value.getClass();

        return getClass().getSimpleName() +
                "(key=" + key +
                ", value=" + value +
                ", value.class=" + valueClass +
                ", type=" + type +
                ")";
    }
}
