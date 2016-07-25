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

import com.github.pjungermann.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Factory to create a {@link Config} from a source {@link File file}.
 *
 * @author Patrick Jungermann
 */
public interface ConfigFactory {

    /**
     * Returns whether the {@link File} is supported by it or not.
     *
     * @param source    the source {@link File}.
     * @return whether the source is supported or not.
     */
    boolean supports(@NotNull File source);

    /**
     * Returns a {@link Config} for the source.
     *
     * @param source     the source {@link File}.
     * @param profile    the profile to be applied to at the loading. This might not supported by all formats.
     * @param context    the context to be applied to; i.e. for references to other values if that is supported.
     * @return the {@link Config} for the source.
     * @throws IOException if there was any issue loading the source's data.
     * @throws ConfigConversionException if a conversion from one config type to another failed.
     */
    @NotNull
    Config create(@NotNull File source, @Nullable String profile, @NotNull Config context) throws IOException, ConfigConversionException;

}
