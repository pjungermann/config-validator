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
package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Arrays.asList;
import static java.util.Collections.*;

/**
 * Provides support for local domains and custom TLDs
 * using a {@link Map} based config for the expectation.
 *
 * The different options on how to use this constraint:
 * <dl>
 *   <dt>{@code false}</dt>
 *   <dd>can be anything</dd>
 *
 *   <dt>{@code true}:</dt>
 *   <dd>needs to be a valid without support for "local" and "customTLDs"</dd>
 *
 *   <dt>empty {@link Map}</dt>
 *   <dd>same as {@code true}</dd>
 *
 *   <dt>{@link Map} with..</dt>
 *   <dd>
 *       <dl>
 *           <dt>{@code "local"}</dt>
 *           <dd>{@code true} / {@code false} whether to consider local domain names as valid</dd>
 *
 *           <dt>{@code "customTLDs"}</dt>
 *           <dd>
 *               {@code Collection} of custom TLDs you need want to consider as valid (e.g. ".local", ".lan", ...)
 *           </dd>
 *       </dl>
 *   </dd>
 * </dl>
 *
 * @author patrick.jungermann
 * @since 2016-07-24
 */
public abstract class DomainAwareConstraint extends AbstractConstraint {

    public static final String LOCAL_KEY = "local";
    public static final String CUSTOM_TLDS_KEY = "customTLDs";
    public static final Set<String> ALLOWED_CONFIG_KEYS = unmodifiableSet(
            new HashSet<>(asList(LOCAL_KEY, CUSTOM_TLDS_KEY))
    );

    protected boolean validExpectation = true;

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
     * @param key         The key for which this {@link Constraint} gets defined for.
     * @param expectation The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine  The {@link SourceLine} at which this expectation got expressed at.
     */
    @SuppressWarnings("unchecked")
    public DomainAwareConstraint(@NotNull final String key,
                                 @Nullable final Object expectation,
                                 @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);

        this.config = expectation instanceof Map ? (Map<String, Object>) expectation : emptyMap();

        Boolean allowLocal;
        try {
            allowLocal = configuredAllowLocal();

        } catch (ClassCastException e) {
            allowLocal = false;
            validExpectation = false;
        }

        Set<String> allowedCustomTLDs;
        try {
            allowedCustomTLDs = configuredAllowedCustomTLDs();

        } catch (ClassCastException e) {
            allowedCustomTLDs = null;
            validExpectation = false;
        }

        this.allowLocal = allowLocal;
        this.allowedCustomTLDs = allowedCustomTLDs;
    }

    @NotNull
    protected Set<String> getAllowedConfigKeys() {
        return ALLOWED_CONFIG_KEYS;
    }

    protected boolean configuredAllowLocal() {
        final Object localConfig = config.get(LOCAL_KEY);
        return localConfig != null && (boolean) localConfig;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    protected Set<String> configuredAllowedCustomTLDs() {
        final Collection<String> customTLDsConfig = (Collection<String>) config.get(CUSTOM_TLDS_KEY);
        if (customTLDsConfig == null) {
            return emptySet();
        }

        final HashSet<String> customTLDs = new HashSet<>();
        final Set<String> checked = checkedSet(customTLDs, String.class);
        checked.addAll(customTLDsConfig);

        return customTLDs;
    }

    @Override
    protected boolean isValidExpectation() {
        if (!validExpectation) {
            return false;
        }

        final Set<String> allowedConfigKeys = getAllowedConfigKeys();
        for (final String key: config.keySet()) {
            if (!allowedConfigKeys.contains(key)) {
                return false;
            }
        }

        return expectation != null
                && (expectation instanceof Boolean || expectation instanceof Map);
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        final boolean requireValid = expectation instanceof Map || (boolean) expectation;
        if (!requireValid) {
            return null;
        }

        final String raw = value.toString();
        final String withoutCustomTLD = replaceCustomTLD(raw);

        return doValidateWithoutCustomTLD(withoutCustomTLD);
    }

    /**
     * Validates the {@link String} config value with any {@link #allowedCustomTLDs custom TLD}
     * replaced with a standard TLD.
     *
     * @param value    the config value with any custom TLD replaced with a standard TLD.
     * @return a {@link ConfigError} if the value was invalid, {@code null} otherwise.
     * @see #validate(Config)
     * @see #doValidate(Object)
     * @see #isValidExpectation()
     * @see #skipNullValues()
     * @see #skipBlankValues()
     */
    @Nullable
    protected abstract ConfigError doValidateWithoutCustomTLD(@NotNull final String value);

    @NotNull
    protected String replaceCustomTLD(@NotNull final String value) {
        for (final String tld: allowedCustomTLDs) {
            Pattern pattern = Pattern.compile("(.*?)" + Pattern.quote("." + tld) + "([:/].*)?");
            Matcher matcher = pattern.matcher(value);

            if (matcher.matches()) {
                String replaced = matcher.group(1) + ".valid.com";
                String suffix = matcher.group(2);
                if (suffix != null) {
                    replaced += suffix;
                }
                return replaced;
            }
        }

        return value;
    }
}
