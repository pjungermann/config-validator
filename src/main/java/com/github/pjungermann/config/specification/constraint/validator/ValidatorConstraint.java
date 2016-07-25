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
package com.github.pjungermann.config.specification.constraint.validator;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Checks whether the value is valid by executing a provided {@link Closure}.
 * The {@link Closure} has access to the config, the config key and config value,
 * and can execute any validation logic you want.
 * Therefore, it provides an easy way to extend the validation feature set easily.
 *
 * @author Patrick Jungermann
 */
public class ValidatorConstraint extends AbstractConstraint {

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public ValidatorConstraint(@NotNull final String key,
                               @Nullable final Object expectation,
                               @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Override
    protected boolean isValidExpectation() {
        return expectation != null && expectation instanceof Closure;
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Config config, final Object value) {
        assert expectation != null;
        final Object result = ((Closure) expectation).call(
                config, key, value
        );

        if (result instanceof Boolean && (Boolean) result) {
            return null;
        }

        return violatedBy(value);
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        throw new UnsupportedOperationException("use #doValidate(Config, Object) instead");
    }

    @Override
    public boolean supports(final Class type) {
        return true;
    }
}
