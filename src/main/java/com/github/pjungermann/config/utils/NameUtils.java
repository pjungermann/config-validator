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

import java.util.Locale;

import static java.lang.Character.isUpperCase;
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
    @NotNull
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
    @NotNull
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
    @NotNull
    public static String getNaturalName(@NotNull final String name) {
        if (name.isEmpty()) {
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

    /**
     * Returns the property name representation for the given class,
     * i.e. {@code "myClass"} for {@code my.package.MyClass}.
     *
     * @param clazz    the class for which you need the property name for.
     * @return the property name representation.
     */
    public static String getPropertyName(final Class clazz) {
        return getPropertyName(clazz, null);
    }

    /**
     * Returns the property name representation for the given class
     * without the trailing name / suffix, i.e. {@code "myClass"}
     * for {@code my.package.MyClassDummy} and {@code "Dummy"}.
     *
     * @param clazz           the class for which you need the property name for.
     * @param trailingName    the trailing name to be remove from the property name.
     * @return the property name representation.
     */
    @NotNull
    public static String getPropertyName(@NotNull final Class clazz, @Nullable final String trailingName) {
        final String simpleName = clazz.getSimpleName();
        if (trailingName != null && !trailingName.isEmpty() && simpleName.endsWith(trailingName)) {
            return getPropertyName(simpleName.substring(0, simpleName.length() - trailingName.length()));
        }

        return getPropertyName(simpleName);
    }

    /**
     * Returns the property name representation for the given class, i.e.
     * {@code "myName"} for {@code "MyName"} or
     * {@code "vcsName"} for {@code "VCSName"} or
     * {@code "upper"} for {@code "UPPER"}.
     *
     * @param name    the name to get the property name for.
     * @return the property name.
     */
    @NotNull
    public static String getPropertyName(@NotNull final String name) {
        if (name.isEmpty()) {
            return name;
        }

        int last = 0;
        if (name.length() > 1
                && isUpperCase(name.charAt(0))
                && isUpperCase(name.charAt(1))) {

            for (int i = 0; i < name.length(); i++) {
                if (isUpperCase(name.charAt(i))
                        && (i == name.length() - 1 || isUpperCase(name.charAt(i + 1)))) {
                    last = i;
                }
            }
        }

        if (last == name.length() - 1) {
            // no case change found
            return name.toLowerCase(Locale.ENGLISH);
        }

        return name.substring(0, last + 1).toLowerCase(Locale.ENGLISH) + name.substring(last + 1);
    }
}
