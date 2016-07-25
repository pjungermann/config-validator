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

import org.jetbrains.annotations.NotNull;

/**
 * Thrown in case that a {@link com.github.pjungermann.config.Config} value
 * was a (custom) {@link Number} type without parsable {@link Number#toString()}
 * implementation (or no override).
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class InvalidConfigValueNumberTypeError extends InvalidConfigValueTypeError {

    /**
     * @param constraint The {@link Constraint} which got checked.
     * @param value      The invalid value.
     */
    public InvalidConfigValueNumberTypeError(@NotNull final Constraint constraint, @NotNull final Number value) {
        super(constraint, value);
    }

    @NotNull
    @Override
    public String toString() {
        return super.toString() + "; HINT: provide a toString implementation parsable by e.g. BigDecimal";
    }
}
