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
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.apache.commons.validator.routines.EmailValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

/**
 * Used to validate that a given text value is a valid email address.
 *
 * Their different options on how to use this constraint:
 * <dl>
 *   <dl>{@code false}</dl>
 *   <dd>can be anything</dd>
 *
 *   <dl>{@code true}:</dl>
 *   <dd>needs to be a valid email address</dd>
 *
 *   <dl>empty Map</dl>
 *   <dd>same as {@code true}</dd>
 *
 *   <dl>Map with..</dl>
 *   <dd>
 *       <dt>
 *           <dl>.. "local"</dl>
 *           <dd>{@code true} / {@code false} to consider local email addresses as valid</dd>
 *
 *           <dl>.. "customTLDs"</dl>
 *           <dd>
 *               {@code Collection} of custom TLDs you need to consider as valid (e.g. ".local", ".lan", ...)
 *           </dd>
 *       </dt>
 *   </dd>
 *
 * @author Patrick Jungermann
 */
public class EmailConstraint extends AbstractConstraint {

    public static final String LOCAL_KEY = "local";
    public static final String CUSTOM_TLDS_KEY = "customTLDs";
    public static final Set<String> ALLOWED_CONFIG_KEYS = unmodifiableSet(new HashSet<>(asList(LOCAL_KEY, CUSTOM_TLDS_KEY)));

    /**
     * Should local addresses be considered valid?
     */
    protected final boolean allowLocal;

    /**
     * Are their any additional (maybe custom) TLDs
     * which need to be considered valid?
     * (e.g. ".local", ".lan", ...)
     */
    protected final Set<String> allowedCustomTLDs;

    protected final Map<String, Object> config;

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
        super(key, expectation instanceof Map ? true : expectation, sourceLine);

        this.config = expectation instanceof Map ? (Map<String, Object>) expectation : emptyMap();
        this.allowedCustomTLDs = configuredAllowedCustomTLDs(config);
        this.allowLocal = configuredAllowLocal(config);
    }

    protected boolean configuredAllowLocal(@NotNull final Map<String, Object> config) {
        final Object localConfig = config.get(LOCAL_KEY);
        return localConfig != null && localConfig instanceof Boolean && (boolean) localConfig;
    }

    @NotNull
    protected Set<String> configuredAllowedCustomTLDs(@NotNull final Map<String, Object> config) {
        final Object customTLDsConfig = config.get(CUSTOM_TLDS_KEY);
        if (customTLDsConfig instanceof Collection) {
            @SuppressWarnings("unchecked")
            final Collection<String> customTLDs = checkedCollection((Collection<String>) customTLDsConfig, String.class);

            return new HashSet<>(customTLDs);
        }

        return emptySet();
    }

    @Override
    protected boolean isValidExpectation() {
        for (final String key: config.keySet()) {
            if (!ALLOWED_CONFIG_KEYS.contains(key)) {
                return false;
            }
        }

        return expectation != null && expectation instanceof Boolean;
    }

    @Override
    protected ConfigError doValidate(final Object value) {
        final boolean requireValid = (boolean) expectation;
        if (!requireValid) {
            return null;
        }

        final String rawEmail = value.toString();
        final String email = removeCustomTLD(rawEmail);

        final boolean allowLocal = this.allowLocal || !email.equals(rawEmail);
        if (!EmailValidator.getInstance(allowLocal).isValid(email)) {
            return violatedBy(value);
        }

        return null;
    }

    @NotNull
    protected String removeCustomTLD(@NotNull final String email) {
        for (final String tld: allowedCustomTLDs) {
            if (email.toLowerCase().endsWith("." + tld)) {
                return email.substring(0, email.length() - tld.length() - 1);
            }
        }

        return email;
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }
}
