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

import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.ConfigSpecification;
import com.github.pjungermann.config.utils.NameUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Factory to create specific {@link Constraint} instances for
 * a specification and the specified config key's values
 * within it.
 *
 * {@link #getName()} defines the name used within the
 * specification DSL.
 *
 * @author Patrick Jungermann
 */
public interface ConstraintFactory<ProducedConstraint extends Constraint> {

    /**
     * As a convention, {@link Constraint} classes follow the class name pattern
     * {TheName}ConstraintFactory similar to {TheName}Constraint which then
     * results in "theName" as returned value.
     *
     * @return the name of the constraint, as used at a {@link ConfigSpecification}.
     */
    @NotNull
    default String getName() {
        return NameUtils.getPropertyName(getClass(), ConstraintFactory.class.getSimpleName());
    }

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     * @return the produced {@link Constraint}.
     */
    @NotNull
    ProducedConstraint create(@NotNull String key, @Nullable Object expectation, @NotNull SourceLine sourceLine);
}
