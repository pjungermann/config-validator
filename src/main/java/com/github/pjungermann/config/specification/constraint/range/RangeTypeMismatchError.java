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
package com.github.pjungermann.config.specification.constraint.range;

import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.ConfigConstraintError;
import com.github.pjungermann.config.specification.constraint.Constraint;
import groovy.lang.Range;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Thrown if the type of the value does not match the range's type.
 *
 * @author Patrick Jungermann
 */
public class RangeTypeMismatchError extends ConfigConstraintError {

    public static final String MESSAGE_CODE = "constraints.invalid.range.type_mismatch";

    private final Range expectation;

    public RangeTypeMismatchError(@NotNull final Constraint constraint,
                                  @NotNull final Range expectation,
                                  @NotNull final Object value) {
        super(constraint, value);
        this.expectation = expectation;
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
                        sourceLine,
                        constraint.getKey(),
                        value,
                        value == null ? "<null>" : value.getClass().getName(),
                        expectation,
                        expectation.getFrom().getClass().getName()
                },
                MESSAGE_CODE
        );
    }
}
