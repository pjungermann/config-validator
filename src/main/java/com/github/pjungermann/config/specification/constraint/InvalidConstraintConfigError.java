/*
 * Copyright 2016 Patrick Jungermann
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
package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * An error used when the configuration / specification
 * for a certain {@link Constraint} was wrong.
 *
 * @author Patrick Jungermann
 */
public class InvalidConstraintConfigError implements ConfigError {

    public static final String MESSAGE_CODE = "errors.constraints.config.invalid";

    private final Constraint constraint;
    private final Object expectation;

    /**
     * @param constraint     The {@link Constraint} which got checked.
     * @param expectation    The {@link Constraint} config expressing the expectation onto accepted values.
     */
    public InvalidConstraintConfigError(@NotNull final Constraint constraint, @Nullable final Object expectation) {
        this.constraint = constraint;
        this.expectation = expectation;
    }

    @Override
    public String toString() {
        return "Illegal config for constraint " + constraint.getName()
                + " for config key " + constraint.getKey() + ": "
                + expectation;
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        final SourceLine sourceLine = constraint.definedAt();

        return new DefaultMessageSourceResolvable(
                new String[]{
                        MESSAGE_CODE
                },
                new Object[]{
                        constraint.getKey(),
                        constraint.getName(),
                        expectation,
                        sourceLine
                },
                MESSAGE_CODE
        );
    }
}
