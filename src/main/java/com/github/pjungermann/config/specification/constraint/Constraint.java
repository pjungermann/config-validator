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
import com.github.pjungermann.config.specification.ConfigSpecification;
import com.github.pjungermann.config.utils.NameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;

/**
 * Constraints can be applied to a configuration value
 * to express the expectations to this value, used for
 * a configuration key.
 *
 * @author Patrick Jungermann
 */
public interface Constraint extends Comparable<Constraint> {

    /**
     * As a convention, {@link Constraint} classes follow the class name pattern
     * {TheName}Constraint which then results in "theName" as returned value.
     *
     * @return the name of the constraint, as used at a {@link ConfigSpecification}.
     */
    @NotNull
    default String getName() {
        return NameUtils.getPropertyName(getClass(), Constraint.class.getSimpleName());
    }

    /**
     * @return where it got defined at (e.g. within which {@link ConfigSpecification} file).
     */
    @NotNull
    SourceLine definedAt();

    /**
     * @return the config key to which it has to be applied to.
     */
    @NotNull
    String getKey();

    /**
     * @param type    type of a config value.
     * @return whether the type is supported or not.
     */
    boolean supports(final Class type);

    /**
     * @param config    the config to be validated.
     * @return any validation errors or {@code null}.
     */
    @Nullable
    ConfigError validate(@NotNull final Config config);

    /**
     * @param value    the rejected config value.
     * @return the error message for the rejected value.
     */
    @NotNull
    MessageSourceResolvable getMessage(@Nullable final Object value);
}
