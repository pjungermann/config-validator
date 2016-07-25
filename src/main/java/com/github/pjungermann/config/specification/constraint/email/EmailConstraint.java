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
package com.github.pjungermann.config.specification.constraint.email;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.DomainAwareConstraint;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Used to validate that a given text value is a valid email address.
 *
 * The different options on how to use this constraint:
 * <dl>
 *   <dt>{@code false}</dt>
 *   <dd>can be anything</dd>
 *
 *   <dt>{@code true}:</dt>
 *   <dd>needs to be a valid email address</dd>
 *
 *   <dt>empty Map</dt>
 *   <dd>same as {@code true}</dd>
 *
 *   <dt>Map with..</dt>
 *   <dd>
 *       <dl>
 *           <dt>.. "local"</dt>
 *           <dd>{@code true} / {@code false} to consider local email addresses as valid</dd>
 *
 *           <dt>.. "customTLDs"</dt>
 *           <dd>
 *               {@code Collection} of custom TLDs you need to consider as valid (e.g. ".local", ".lan", ...)
 *           </dd>
 *       </dl>
 *   </dd>
 * </dl>
 *
 * @author Patrick Jungermann
 */
public class EmailConstraint extends DomainAwareConstraint {

    /**
     * {@link EmailConstraint} which does not consider local addresses valid.
     *
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    @SuppressWarnings("unchecked")
    public EmailConstraint(@NotNull final String key,
                           @Nullable final Object expectation,
                           @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);
    }

    @Nullable
    @Override
    protected ConfigError doValidateWithoutCustomTLD(@NotNull final String value) {
        if (!EmailValidator.getInstance(allowLocal).isValid(value)) {
            return violatedBy(value);
        }

        return null;
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }
}
