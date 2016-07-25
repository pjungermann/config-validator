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

import com.github.pjungermann.config.ConfigError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;

/**
 * An error which gets used in case a value
 * was not valid for a certain {@link Constraint}.
 *
 * @author Patrick Jungermann
 */
public class ConfigConstraintError implements ConfigError {

    protected final Constraint constraint;
    protected final Object value;

    /**
     * @param constraint The {@link Constraint} which got checked.
     * @param value      The invalid value.
     */
    public ConfigConstraintError(@NotNull final Constraint constraint, @Nullable final Object value) {
        this.constraint = constraint;
        this.value = value;
    }

    @NotNull
    @Override
    public String toString() {
        return constraint.getName() + " failed for key " + constraint.getKey();
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        return constraint.getMessage(value);
    }
}
