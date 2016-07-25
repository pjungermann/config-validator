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
package com.github.pjungermann.config.specification.types;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

/**
 * Allows to apply a transformation or conversion to a {@link Config}.
 * This conversion will be applied before any {@link Constraint constraint}
 * gets checked. Therefore, this can be used to mutate config values read to
 * handle differences in how those values get treated by different
 * frameworks or application (e.g. implicit type conversion, etc.)
 * or to apply conversions which would get applied within the application,
 * but cannot get provided as that type upfront (explicit conversion).
 * The conversion will usually create a state which is usable by the constraints.
 *
 * @author Patrick Jungermann
 */
public interface TypeConverter {

    @NotNull
    Set<String> getKeys();

    void register(@NotNull String key, @NotNull Class asType);

    boolean isConversionCommand(@NotNull String name);

    boolean isValidConversionConfig(@Nullable Object conversionConfig);

    void convert(@NotNull Config config);
}
