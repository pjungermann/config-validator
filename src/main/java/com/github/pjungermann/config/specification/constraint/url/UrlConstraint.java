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
package com.github.pjungermann.config.specification.constraint.url;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.DomainAwareConstraint;
import org.apache.commons.validator.routines.UrlValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static java.util.Collections.checkedSet;

/**
 * Checks whether the value is a valid URL or not.
 *
 * There are also several options you can apply as expectation:
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
 *
 *           <dt>{@code "schemes"}</dt>
 *           <dd>
 *               the allowed schemes (default: http, https, ftp).
 *               You can limit the valid URLs i.e. to only HTTPS URLs.
 *               Also, you can add custom schemes.
 *           </dd>
 *       </dl>
 *   </dd>
 * </dl>
 *
 * @author Patrick Jungermann
 */
public class UrlConstraint extends DomainAwareConstraint {

    public static final String SCHEMES_KEY = "schemes";

    private final UrlValidator urlValidator;

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public UrlConstraint(@NotNull final String key,
                         @Nullable final Object expectation,
                         @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);

        String[] allowedSchemes;
        try {
            allowedSchemes = configureAllowedSchemes();

        } catch (ClassCastException e) {
            allowedSchemes = null;
        }

        this.validExpectation = this.validExpectation && allowedSchemes != null;
        if (!this.validExpectation) {
            urlValidator = UrlValidator.getInstance();
            return;
        }

        long options = 0L;
        if (allowLocal) {
            options = UrlValidator.ALLOW_LOCAL_URLS;
        }
        if (allowedSchemes.length != 0) {
            urlValidator = new UrlValidator(allowedSchemes, options);

        } else {
            urlValidator = new UrlValidator(options);
        }
    }

    @NotNull
    @Override
    protected Set<String> getAllowedConfigKeys() {
        final Set<String> keys = new HashSet<>(super.getAllowedConfigKeys());
        keys.add(SCHEMES_KEY);

        return keys;
    }

    @NotNull
    @SuppressWarnings("unchecked")
    protected String[] configureAllowedSchemes() {
        final Collection<String> schemesConfig = (Collection<String>) config.get(SCHEMES_KEY);
        if (schemesConfig == null || schemesConfig.isEmpty()) {
            return new String[0];
        }

        final HashSet<String> schemes = new HashSet<>();
        final Set<String> checked = checkedSet(schemes, String.class);
        checked.addAll(schemesConfig);

        return schemes.toArray(new String[schemes.size()]);
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        if (value instanceof URL) {
            return null;
        }

        return super.doValidate(value);
    }

    @Nullable
    @Override
    protected ConfigError doValidateWithoutCustomTLD(@NotNull final String value) {
        if (urlValidator.isValid(value)) {
            return null;
        }

        return violatedBy(value);
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type) || URL.class.isAssignableFrom(type);
    }
}
