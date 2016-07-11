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
package com.github.pjungermann.config.specification.constraint.blank;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to define whether a {@link CharSequence} based config value
 * is allowed to be blank or not.
 *
 * {@code null} values will not get checked.
 * If blank values are not allowed, it does not imply that
 * {@code null} values are not allowed as well.
 * This needs to be expressed separately.
 *
 * @author Patrick Jungermann
 */
public class BlankConstraint extends AbstractConstraint {

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public BlankConstraint(@NotNull final String key,
                           @Nullable final Object expectation,
                           @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }

    @Override
    protected boolean isValidExpectation() {
        return expectation != null && expectation instanceof Boolean;
    }

    @Override
    protected ConfigError doValidate(final Object value) {
        final boolean blankAllowed = (boolean) expectation;

        if (!blankAllowed && value.toString().trim().isEmpty()) {
            return violatedBy(value);
        }

        return null;
    }

    @Override
    protected boolean skipBlankValues() {
        return false;
    }
}
