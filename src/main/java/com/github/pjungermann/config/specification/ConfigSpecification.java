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
package com.github.pjungermann.config.specification;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.types.TypeConverter;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static java.util.Collections.unmodifiableCollection;

/**
 * Specifies all expectations onto a configuration.
 *
 * @author Patrick Jungermann
 */
public class ConfigSpecification {

    /**
     * Transformations which have to be applied
     * before the constraints get checked.
     */
    public final TypeConverter typeConverter;

    /**
     * All constraints which have to be checked
     * when validating a configuration.
     */
    public final Collection<Constraint> constraints;

    /**
     * All errors identified during a config validation.
     */
    public final Collection<ConfigError> errors;

    public ConfigSpecification(@NotNull final TypeConverter typeConverter,
                               @NotNull final Collection<Constraint> constraints,
                               @NotNull final Collection<ConfigError> errors) {
        this.typeConverter = typeConverter;
        this.constraints = unmodifiableCollection(constraints);
        this.errors = errors;
    }
}
