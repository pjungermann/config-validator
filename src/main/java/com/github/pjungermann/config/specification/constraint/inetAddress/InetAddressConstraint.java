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
package com.github.pjungermann.config.specification.constraint.inetAddress;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.AbstractConstraint;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.apache.commons.validator.routines.InetAddressValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.EnumSet;
import java.util.Locale;

import static java.util.Collections.singleton;

/**
 * Checks whether the text value is a valid {@link java.net.InetAddress Internet address} or not.
 *
 * Allowed configuration for your validation expectation:
 * <table>
 *     <tr>
 *         <th>Expectation</th>
 *         <th>IPv4</th>
 *         <th>IPv6</th>
 *     </tr>
 *     <tr>
 *         <td>{@code false}</td>
 *         <td>skip</td>
 *         <td>skip</td>
 *     </tr>
 *     <tr>
 *         <td>{@code true}</td>
 *         <td>yes</td>
 *         <td>yes</td>
 *     </tr>
 *     <tr>
 *         <td>{@code "IPv4}</td>
 *         <td>yes</td>
 *         <td>no</td>
 *     </tr>
 *     <tr>
 *         <td>{@code "IPv6"}</td>
 *         <td>no</td>
 *         <td>yes</td>
 *     </tr>
 *     <tr>
 *         <td>{@code ["IPv4", "IPv6"]}</td>
 *         <td>yes</td>
 *         <td>yes</td>
 *     </tr>
 *     <tr>
 *         <td>{@code ["IPv4"]}</td>
 *         <td>yes</td>
 *         <td>no</td>
 *     </tr>
 *     <tr>
 *         <td>{@code ["IPv6"]}</td>
 *         <td>no</td>
 *         <td>yes</td>
 *     </tr>
 * </table>
 *
 * @author Patrick Jungermann
 */
public class InetAddressConstraint extends AbstractConstraint {

    private final EnumSet<InetAddressType> allowedVersions;
    private final boolean validExpectation;

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public InetAddressConstraint(@NotNull final String key,
                                 @Nullable final Object expectation,
                                 @NotNull final SourceLine sourceLine) {
        super(key, expectation, sourceLine);

        allowedVersions = configureAllowedVersions(expectation);
        validExpectation = allowedVersions != null;
    }

    @Nullable
    protected EnumSet<InetAddressType> configureAllowedVersions(@Nullable final Object expectation) {
        if (expectation == null) {
            return null;
        }

        if (expectation instanceof Boolean) {
            boolean allowed = (boolean) expectation;

            return allowed
                    ? EnumSet.allOf(InetAddressType.class)
                    : EnumSet.noneOf(InetAddressType.class);
        }

        if (expectation instanceof CharSequence) {
            return asCheckedEnumSet(singleton(expectation.toString()));
        }

        if (expectation instanceof Collection) {
            return asCheckedEnumSet((Collection) expectation);
        }

        return null;
    }

    @Nullable
    protected EnumSet<InetAddressType> asCheckedEnumSet(Collection collection) {
        EnumSet<InetAddressType> set = EnumSet.noneOf(InetAddressType.class);

        for (Object item: collection) {
            if (item instanceof String) {
                InetAddressType type = InetAddressType.valueOfIgnoreCase((String) item);
                if (type == null) {
                    return null;
                }
                set.add(type);

            } else if (item instanceof InetAddressType) {
                set.add((InetAddressType) item);

            } else {
                return null;
            }
        }

        return set;
    }

    @Override
    protected boolean isValidExpectation() {
        return validExpectation;
    }

    @Nullable
    @Override
    protected ConfigError doValidate(final Object value) {
        if (allowedVersions.isEmpty()) {
            // == false -> skip
            return null;
        }

        if (allowedVersions.stream().anyMatch(type -> type.isValid(value.toString()))) {
            return null;
        }

        return violatedBy(value);
    }

    @Override
    public boolean supports(final Class type) {
        return CharSequence.class.isAssignableFrom(type);
    }

    public enum InetAddressType {
        IPv4, IPv6;

        private InetAddressValidator validator = new InetAddressValidator();

        public boolean isValid(@NotNull String inetAddress) {
            switch (this) {
                case IPv4:
                    return validator.isValidInet4Address(inetAddress);

                case IPv6:
                    return validator.isValidInet6Address(inetAddress);

                default:
                    return false;
            }
        }

        public static InetAddressType valueOfIgnoreCase(String name) {
            for (InetAddressType type: values()) {
                if (type.name().toLowerCase(Locale.ENGLISH)
                        .equals(name.toLowerCase(Locale.ENGLISH))) {
                    return type;
                }
            }

            return null;
        }
    }
}
