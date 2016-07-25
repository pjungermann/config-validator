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

import java.math.BigDecimal;

/**
 * Provides helper for {@link #compare(Number, Number) comparison} of {@link Number Numbers}
 * and validation support.
 *
 * @author Patrick Jungermann
 */
public abstract class AbstractNumericalConstraint extends AbstractConstraint {

    public AbstractNumericalConstraint(@NotNull final String key,
                                       @Nullable final Object expectation,
                                       @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    protected abstract ConfigError doValidate(final Number value, final Number expectation) throws NumberFormatException;

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        if (!(value instanceof Number)) {
            return violatedBy(value);
        }

        Number numValue = (Number) value;

        try {
            return doValidate(numValue, (Number) expectation);

        } catch (NumberFormatException e) {
            // custom Number implementation did not provide a proper toString implementation
            return new InvalidConfigValueNumberTypeError(this, numValue);
        }
    }

    @Override
    protected boolean isValidExpectation() {
        if (expectation == null || !(expectation instanceof Number)) {
            return false;
        }

        try {
            // needs to be able to get parsed
            new BigDecimal(expectation.toString());
            return true;

        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Compares two arbitrary {@link Number} instances.
     *
     * @param base     the basis for the comparison. Compared against {@code value}.
     * @param value    to which {@code base} is to be compared.
     * @return -1, 0, or 1 as {@code base} is numerically
     *          less than, equal to, or greater than "b".
     * @throws NumberFormatException
     *          if one of the {@link Number Numbers} does not implement
     *          {@link Number#toString()} properly.
     */
    protected int compare(Number base, Number value) throws NumberFormatException {
        // most likely not the most beautiful solution
        // but quite simple and even supports Number
        // sub-classes which do not inherit from Comparable
        // BUT: requires proper toString() implementation
        // which might be forgotten at custom Number implementations.
        // This gets handled later on and will get taken as
        // invalid type with provided hint
        return new BigDecimal(base.toString()).compareTo(new BigDecimal(value.toString()));
    }

    @Override
    public boolean supports(final Class type) {
        return Number.class.isAssignableFrom(type);
    }
}
