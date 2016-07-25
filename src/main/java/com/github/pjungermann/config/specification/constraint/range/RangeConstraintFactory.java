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
package com.github.pjungermann.config.specification.constraint.range;

import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.ConstraintFactory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

/**
 * Creates a {@link RangeConstraint}.
 *
 * @author Patrick Jungermann
 */
@Component
public class RangeConstraintFactory implements ConstraintFactory<RangeConstraint> {

    @NotNull
    @Override
    public RangeConstraint create(@NotNull final String key,
                                  @Nullable final Object expectation,
                                  @NotNull final SourceLine sourceLine) {
        return new RangeConstraint(key, expectation, sourceLine);
    }
}
