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
package com.github.pjungermann.config.specification;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.stream.Stream;

/**
 * Loads config specification files
 * as {@link ConfigSpecification}.
 *
 * @author Patrick Jungermann
 */
public interface ConfigSpecificationLoader {

    boolean DEFAULT_RECURSIVE = true;

    default boolean getDefaultRecursive() {
        return DEFAULT_RECURSIVE;
    }

    @NotNull
    ConfigSpecification load(boolean recursive, @NotNull Stream<File> sourceStream);

    @NotNull
    default ConfigSpecification load(final Stream<File> sourceStream) {
        return load(getDefaultRecursive(), sourceStream);
    }

    @NotNull
    default ConfigSpecification load(final boolean recursive, @NotNull final String... sources) {
        return load(recursive, Stream.of(sources).map(File::new));
    }

    @NotNull
    default ConfigSpecification load(boolean recursive, @NotNull final File... sources) {
        return load(recursive, Stream.of(sources));
    }

    @NotNull
    default ConfigSpecification load(@NotNull final String... sources) {
        return load(getDefaultRecursive(), sources);
    }

    @NotNull
    default ConfigSpecification load(@NotNull final File... sources) {
        return load(getDefaultRecursive(), sources);
    }
}
