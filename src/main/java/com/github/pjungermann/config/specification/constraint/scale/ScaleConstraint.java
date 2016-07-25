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
package com.github.pjungermann.config.specification.constraint.scale;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;

/**
 * Checks whether the scale of a {@link Number} is
 * not greater than the provided limitation.
 *
 * @author Patrick Jungermann
 */
public class ScaleConstraint extends AbstractConstraint {

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public ScaleConstraint(@NotNull final String key,
                           @Nullable final Object expectation,
                           @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Override
    protected boolean isValidExpectation() {
        return expectation != null
                && expectation instanceof Integer
                && (Integer) expectation >= 0;
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        final int scale = (int) expectation;

        if (hasScale(scale, value)) {
            return null;
        }

        return violatedBy(value);
    }

    @Override
    public boolean supports(final Class type) {
        return Number.class.isAssignableFrom(type);
    }

    protected boolean hasScale(final int scale, final Object object) {
        if (!(object instanceof Number)) {
            return false;
        }

        final BigDecimal decimal = asBigDecimal((Number) object);
        return decimal.scale() <= scale;
    }

    protected BigDecimal asBigDecimal(final Number number) {
        if (number instanceof BigDecimal) {
            return (BigDecimal) number;
        }

        return new BigDecimal(number.toString())
                .stripTrailingZeros();
    }
}
