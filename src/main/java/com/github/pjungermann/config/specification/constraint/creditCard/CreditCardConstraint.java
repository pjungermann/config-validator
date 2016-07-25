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
package com.github.pjungermann.config.specification.constraint.creditCard;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import static java.util.Locale.ENGLISH;

/**
 * Used to validate that a given text is a valid credit card.
 * The expectation is the list of supported types or
 * just the one supported type.
 *
 * @author Patrick Jungermann
 * @see CreditCardValidator
 */
public class CreditCardConstraint extends AbstractConstraint {

    private static final HashMap<String, Long> TYPES;
    static {
        TYPES = new HashMap<>(5);
        TYPES.put("amex", CreditCardValidator.AMEX);
        TYPES.put("visa", CreditCardValidator.VISA);
        TYPES.put("mastercard", CreditCardValidator.MASTERCARD);
        TYPES.put("discover", CreditCardValidator.DISCOVER);
        TYPES.put("diners", CreditCardValidator.DINERS);
    }

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public CreditCardConstraint(@NotNull final String key,
                                @Nullable final Object expectation,
                                @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        if (expectation instanceof  Boolean && !((Boolean) expectation)) {
            return null;
        }

        if (!getValidator().isValid(value.toString())) {
            return violatedBy(value);
        }

        return null;
    }

    @SuppressWarnings("unchecked")
    protected CreditCardValidator getValidator() {
        assert expectation != null;
        if (expectation instanceof CharSequence) {
            return new CreditCardValidator(TYPES.get(expectation.toString().toLowerCase(ENGLISH)));
        }

        final Collection<String> selection;
        if (expectation instanceof Boolean) {
            selection = (Boolean) expectation ? TYPES.keySet() : Collections.<String>emptyList();

        } else {
            selection = (Collection<String>) expectation;
        }

        long options = 0L;
        for (final String type : selection) {
            options += TYPES.get(type.toLowerCase(ENGLISH));
        }

        return new CreditCardValidator(options);
    }

    @Override
    protected boolean isValidExpectation() {
        return expectation != null
                &&
                (
                        expectation instanceof Boolean
                                || isValidType(expectation)
                                || isValidTypeList(expectation)
                );
    }

    protected boolean isValidType(final Object type) {
        return type instanceof CharSequence
                && TYPES.containsKey(type.toString().toLowerCase(ENGLISH));

    }

    protected boolean isValidTypeList(final Object value) {
        if (!(value instanceof Collection)) {
            return false;
        }

        @SuppressWarnings("unchecked")
        final Collection<String> types = (Collection<String>) value;

        if (types.isEmpty()) {
            return false;
        }

        for (final String type : types) {
            if (!isValidType(type)) {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }
}
