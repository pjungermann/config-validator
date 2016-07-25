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
package com.github.pjungermann.config.specification.constraint.size;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import groovy.lang.IntRange;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

/**
 * Checks whether the size of a text or {@link Collection}
 * is valid for the provided expectation. The expectation
 * could get provided as {@link Integer} or {@link IntRange}.
 *
 * @author Patrick Jungermann
 */
public class SizeConstraint extends AbstractConstraint {

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public SizeConstraint(@NotNull final String key,
                          @Nullable final Object expectation,
                          @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Override
    protected boolean isValidExpectation() {
        return expectation != null
                && (isValidIntegerExpectation() || isValidIntRangeExpectation());
    }

    protected boolean isValidIntegerExpectation() {
        return expectation instanceof Integer
                && ((Integer) expectation) >= 0;

    }

    protected boolean isValidIntRangeExpectation() {
        return expectation instanceof IntRange
                && ((IntRange) expectation).getFrom() >= 0
                && ((IntRange) expectation).getTo() >= 0;
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        final IntRange sizeChecker = expectation instanceof Integer
                ? new IntRange(true, (Integer) expectation, (Integer) expectation)
                : (IntRange) expectation;
        assert sizeChecker != null;

        final Integer size = getSize(value);
        if (size != null && sizeChecker.contains(size)) {
            return null;
        }

        return violatedBy(value);
    }

    protected Integer getSize(final Object object) {
        if (object instanceof CharSequence) {
            return ((CharSequence) object).length();
        }

        if (object instanceof Collection) {
            return ((Collection) object).size();
        }

        if (object.getClass().isArray()) {
            return ((Object[]) object).length;
        }

        return null;
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type)
                || Collection.class.isAssignableFrom(type);
    }
}
