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
package com.github.pjungermann.config.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

/**
 * Utility methods for {@link File}-based tasks.
 *
 * @author Patrick Jungermann
 */
public class FileUtils {

    /**
     * @param file    {@link File} for which the type is need
     * @return the {@link File File's} type.
     */
    @NotNull
    public static String getType(@NotNull final File file) {
        return getType(file.getName());
    }

    /**
     * @param name    file name.
     * @return the file's type.
     */
    @NotNull
    public static String getType(@NotNull final String name) {
        int lastIndex = name.lastIndexOf('.');
        if (lastIndex == name.length() - 1) {
            lastIndex = name.substring(0, lastIndex).indexOf('.');

            if (lastIndex == -1) {
                return name.toLowerCase();
            }
        }

        return name.substring(lastIndex + 1).toLowerCase();
    }

    /**
     * Checks whether a {@link File} is of one of the specified types.
     *
     * @param file     the {@link File} to check.
     * @param types    the types to check against.
     * @return whether it is of one of the types or not.
     */
    public static boolean isOfType(@NotNull final File file, @NotNull final String... types) {
        final String fileType = getType(file);

        for (final String type : types) {
            if (fileType.equals(type.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Returns all {@link File files} from directory, retrieved recursively.
     *
     * @param dir    the directory for which the {@link File files} are requested.
     * @return all {@link File files} (recursively).
     */
    @NotNull
    public static Stream<Path> filesInDir(@NotNull final Path dir) {
        return listFiles(dir)
                .flatMap(path ->
                        path.toFile().isDirectory()
                                ? filesInDir(path)
                                : Stream.of(path));
    }

    /**
     * Wrapper for {@link Files#lines(Path)} which uplifts the
     * possible {@link IOException} to an {@link UncheckedIOException}.
     * (non-recursive)
     *
     * @param dir    the directory to retrieve the files from.
     * @return the {@link Stream} of {@link Path} for all contained files.
     */
    @NotNull
    public static Stream<Path> listFiles(@NotNull final Path dir) {
        try {
            return Files.list(dir);

        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
