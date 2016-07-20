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

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Base implementation for {@link Constraint constraints}.
 *
 * @author Patrick Jungermann
 */
public abstract class AbstractConstraint implements Constraint {

    public static final String DEFAULT_MESSAGE_CODE = "constraints.invalid.default.message";

    protected final String key;
    protected final Object expectation;
    protected final SourceLine sourceLine;

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public AbstractConstraint(@NotNull final String key,
                              @Nullable final Object expectation,
                              @NotNull final SourceLine sourceLine) {
        this.key = key;
        this.expectation = expectation;
        this.sourceLine = sourceLine;
    }

    /**
     * @return whether the expectation is a valid one.
     */
    protected abstract boolean isValidExpectation();

    /**
     * Validates the value against the {@link #expectation}.
     * Prior to this, the expectation got validated itself
     * and some base checks are already done.
     *
     * @param value    The value which has to be validated against the expectation.
     * @return a {@link ConfigError} if the value was invalid, {@code null} otherwise.
     * @see #validate(Config)
     * @see #isValidExpectation()
     * @see #skipNullValues()
     * @see #skipBlankValues()
     */
    protected abstract ConfigError doValidate(final Object value);

    @NotNull
    @Override
    public SourceLine definedAt() {
        return sourceLine;
    }

    @NotNull
    @Override
    public String getKey() {
        return key;
    }

    @Override
    public ConfigError validate(@NotNull final Config config) {
        if (!isValidExpectation()) {
            return new InvalidConstraintConfigError(this, expectation);
        }

        final Object value = config.get(key);

        if (skipNullValues() && value == null) {
            return null;
        }

        if (skipBlankValues()
                && value instanceof CharSequence
                && value.toString().trim().isEmpty()) {
            return null;
        }

        if (value != null && !supports(value.getClass())) {
            return new InvalidConfigValueTypeError(this, value);
        }

        return doValidate(value);
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage(@Nullable final Object value) {
        final String code = getMessageCode();

        return new DefaultMessageSourceResolvable(
                new String[]{
                        code,
                        DEFAULT_MESSAGE_CODE
                },
                new Object[]{
                        sourceLine,
                        key,
                        value,
                        expectation,
                        getName()
                },
                code
        );
    }

    protected String getMessageCode() {
        return "constraints.invalid." + getName() + ".message";
    }

    protected boolean skipNullValues() {
        // a null is not a value we should even check in most cases
        return true;
    }

    protected boolean skipBlankValues() {
        // most constraints ignore blank values, leaving it to the explicit "blank" constraint.
        return true;
    }

    /**
     * Creates a simple {@link ConfigConstraintError} for the value.
     *
     * @param value    the invalid value.
     * @return a {@link ConfigConstraintError} for the value.
     */
    protected ConfigError violatedBy(final Object value) {
        return new ConfigConstraintError(this, value);
    }

    @Override
    public String toString() {
        return Constraint.class.getSimpleName() + ": "
                + key + "(" + getName() + ": " + expectation + ") "
                + "[" + sourceLine + "]";
    }

    @Override
    public int compareTo(@NotNull final Constraint o) {
        final int byKey = key.compareTo(o.getKey());
        if (byKey != 0) return byKey;

        final int byName = getName().compareTo(o.getName());
        if (byName != 0) return byName;

        return sourceLine.compareTo(o.definedAt());
    }
}
