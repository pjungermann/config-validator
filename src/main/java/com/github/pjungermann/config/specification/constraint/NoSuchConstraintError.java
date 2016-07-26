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
package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Error in case no {@link Constraint} could be found
 * for the name using within a specification definition.
 *
 * @author Patrick Jungermann
 */
public class NoSuchConstraintError implements ConfigError {

    public static final String MESSAGE_CODE = "errors.no_such_constraint";

    public final String name;
    public final String key;
    public final SourceLine sourceLine;

    public NoSuchConstraintError(@NotNull final String name,
                                 @NotNull final String key,
                                 @NotNull final SourceLine sourceLine) {
        this.name = name;
        this.key = key;
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
                        name,
                        key,
                        sourceLine
                },
                MESSAGE_CODE
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() +
                "(name=" + name +
                ", key=" + key +
                ", sourceLine=" + sourceLine +
                ")";
    }
}
