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
package com.github.pjungermann.config.utils;

import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Utility methods for {@link File}-based tasks.
 *
 * @author Patrick Jungermann
 */
public class FileUtils {

    @NotNull
    public static String getType(@NotNull final File file) {
        return getType(file.getName());
    }

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

    public static boolean isOfType(@NotNull final File file, @NotNull final String... types) {
        final String fileType = getType(file);

        for (final String type : types) {
            if (fileType.equals(type.toLowerCase())) {
                return true;
            }
        }

        return false;
    }

}
