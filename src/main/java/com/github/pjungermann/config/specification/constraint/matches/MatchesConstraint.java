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
package com.github.pjungermann.config.specification.constraint.matches;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.regex.Pattern;

/**
 * Checks whether the config value matches to
 * the provided regular expression.
 *
 * @author Patrick Jungermann
 */
public class MatchesConstraint extends AbstractConstraint {

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public MatchesConstraint(@NotNull final String key,
                             @Nullable final Object expectation,
                             @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Override
    protected boolean isValidExpectation() {
        return expectation != null
                && (expectation instanceof Pattern || expectation instanceof CharSequence);
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        assert expectation != null;
        final Pattern pattern = expectation instanceof Pattern
                ? (Pattern) expectation
                : Pattern.compile(expectation.toString());

        if (!pattern.matcher(value.toString()).matches()) {
            return violatedBy(value);
        }

        return null;
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }
}
