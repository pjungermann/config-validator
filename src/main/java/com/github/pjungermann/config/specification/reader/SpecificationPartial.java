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
package com.github.pjungermann.config.specification.reader;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Represents the specification part of e.g. one source.
 *
 * @author Patrick Jungermann
 */
public class SpecificationPartial {

    public final Collection<Constraint> constraints;
    public final Collection<ConfigError> errors;

    public SpecificationPartial(@NotNull final Collection<Constraint> constraints,
                                @NotNull final Collection<ConfigError> errors) {
        this.constraints = constraints;
        this.errors = errors;
    }
}
