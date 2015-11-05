/*
 * Copyright 2015 Patrick Jungermann
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
package com.github.pjungermann.config.loader;

import com.github.pjungermann.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Loads the {@link Config} from the given sources.
 *
 * @author Patrick Jungermann
 */
public interface ConfigLoader {

    String DEFAULT_PROFILE = null;

    boolean DEFAULT_RECURSIVE = true;

    default boolean getDefaultRecursive() {
        return DEFAULT_RECURSIVE;
    }

    @Nullable
    default String getDefaultProfile() {
        return DEFAULT_PROFILE;
    }

    @NotNull
    default Config getDefaultContext() {
        return new Config();
    }

    @NotNull
    default Config load(@NotNull final String source) {
        return load(getDefaultProfile(), getDefaultContext(), getDefaultRecursive(), source);
    }

    @NotNull
    default Config load(@NotNull final String[] sources) {
        return load(getDefaultProfile(), getDefaultContext(), getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(final String profile, @NotNull final String source) {
        return load(profile, getDefaultContext(), getDefaultRecursive(), source);
    }

    @NotNull
    default Config load(final String profile, @NotNull final String[] sources) {
        return load(profile, getDefaultContext(), getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(boolean recursive, @NotNull final String source) {
        return load(getDefaultProfile(), getDefaultContext(), recursive, source);
    }

    @NotNull
    default Config load(boolean recursive, @NotNull final String... sources) {
        return load(getDefaultProfile(), getDefaultContext(), recursive, sources);
    }

    @NotNull
    default Config load(@NotNull final Config context, @NotNull final String... sources) {
        return load(getDefaultProfile(), context, getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(@NotNull final Config context, final boolean recursive, @NotNull final String... sources) {
        return load(getDefaultProfile(), context, recursive, sources);
    }

    @NotNull
    default Config load(final String profile, final boolean recursive, @NotNull final String source) {
        return load(profile, getDefaultContext(), recursive, source);
    }

    @NotNull
    default Config load(final String profile, final boolean recursive, @NotNull final String... sources) {
        return load(profile, getDefaultContext(), recursive, sources);
    }

    @NotNull
    default Config load(final String profile, @NotNull final Config context, @NotNull final String... sources) {
        return load(profile, context, getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(final String profile,
                        @NotNull final Config context,
                        final boolean recursive,
                        @NotNull final String source) {
        return load(profile, context, recursive, new File(source));
    }

    @NotNull
    default Config load(final String profile,
                        @NotNull final Config context,
                        final boolean recursive,
                        @NotNull final String... sources) {
        final Config config = new Config();

        for (final String source : sources) {
            final Config sourceConfig = load(profile, context, recursive, source);
            config.putAll(sourceConfig);

            // references in following files might need access to it
            context.putAll(sourceConfig);
        }

        return config;
    }

    @NotNull
    default Config load(@NotNull final File source) {
        return load(getDefaultProfile(), getDefaultContext(), getDefaultRecursive(), source);
    }

    @NotNull
    default Config load(@NotNull final File... sources) {
        return load(getDefaultProfile(), getDefaultContext(), getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(final boolean recursive, @NotNull final File source) {
        return load(getDefaultProfile(), getDefaultContext(), recursive, source);
    }

    @NotNull
    default Config load(final boolean recursive, @NotNull final File... sources) {
        return load(getDefaultProfile(), getDefaultContext(), recursive, sources);
    }

    @NotNull
    default Config load(final String profile, final boolean recursive, @NotNull final File source) {
        return load(profile, getDefaultContext(), recursive, source);
    }

    @NotNull
    default Config load(final String profile, final boolean recursive, @NotNull final File... sources) {
        return load(profile, getDefaultContext(), recursive, sources);
    }

    @NotNull
    default Config load(@NotNull final Config context, final boolean recursive, @NotNull final File... sources) {
        return load(getDefaultProfile(), context, recursive, sources);
    }

    @NotNull
    default Config load(final String profile, @NotNull final File source) {
        return load(profile, getDefaultContext(), getDefaultRecursive(), source);
    }

    @NotNull
    default Config load(final String profile, @NotNull final File... sources) {
        return load(profile, getDefaultContext(), getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(final String profile, @NotNull final Config context, @NotNull final File source) {
        return load(profile, context, getDefaultRecursive(), source);
    }

    @NotNull
    default Config load(final String profile, @NotNull final Config context, @NotNull final File... sources) {
        return load(profile, context, getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(@NotNull final Config context, @NotNull final File... sources) {
        return load(getDefaultProfile(), context, getDefaultRecursive(), sources);
    }

    @NotNull
    default Config load(@Nullable final String profile,
                        @NotNull final Config context,
                        final boolean recursive,
                        @NotNull final File... sources) {
        final Config config = new Config();

        for (final File source : sources) {
            final Config sourceConfig = load(profile, context, recursive, source);
            config.putAll(sourceConfig);

            // references in following files might need access to it
            context.putAll(sourceConfig);
        }

        return config;
    }

    @NotNull
    Config load(@Nullable String profile, @NotNull Config context, boolean recursive, @NotNull File source);

}
