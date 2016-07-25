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
package com.github.pjungermann.config.types;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Selects a suitable {@link ConfigFactory} for the
 * provided {@link File config source file}.
 *
 * @author Patrick Jungermann
 */
public interface ConfigFactorySelector {

    /**
     * Selects and returns a suitable {@link ConfigFactory}
     * for the {@link File config source}.
     *
     * @param source    the source file.
     * @return a suitable {@link ConfigFactory}, if there is any.
     */
    @Nullable
    ConfigFactory getFactory(@NotNull File source);

}
