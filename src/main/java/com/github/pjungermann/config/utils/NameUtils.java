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
import org.jetbrains.annotations.Nullable;

import static org.apache.commons.lang3.StringUtils.splitByCharacterTypeCamelCase;

/**
 * Utility methods related to names.
 *
 * @author Patrick Jungermann
 */
public class NameUtils {

    /**
     * Returns the natural name representation for the given class by splitting
     * the camel case name into multiple words, i.e.
     * {@code my.package.MyClass} gets {@code "My Class"}.
     *
     * @param clazz    the class to create the natural name for.
     * @return the natural name.
     */
    public static String getNaturalName(@NotNull final Class clazz) {
        return getNaturalName(clazz, null);
    }

    /**
     * Returns the natural name representation for the given class by splitting
     * the camel case name into multiple words, i.e.
     * {@code my.package.MyClass} gets {@code "My Class"}.
     *
     * @param clazz           the class to create the natural name for.
     * @param trailingName    the trailing name to be removed
     * @return the natural name.
     */
    public static String getNaturalName(@NotNull final Class clazz, @Nullable final String trailingName) {
        final String simpleName = clazz.getSimpleName();
        if (trailingName != null && !trailingName.isEmpty() && simpleName.endsWith(trailingName)) {
            return getNaturalName(simpleName.substring(0, simpleName.length() - trailingName.length()));
        }

        return getNaturalName(simpleName);
    }

    /**
     * Returns the natural name representation for the given name, i.e.
     * {@code "MyName"} gets converted to {@code "My Name"}.
     *
     * @param name    the name to create the natural name for.
     * @return the natural name.
     */
    public static String getNaturalName(@Nullable final String name) {
        if (name == null || name.length() == 0) {
            return name;
        }

        final String[] nameParts = splitByCharacterTypeCamelCase(name);
        final StringBuilder builder = new StringBuilder();
        for (final String part : nameParts) {
            builder.append(part).append(" ");
        }

        builder.setLength(builder.length() - 1);
        return builder.toString();
    }

}
