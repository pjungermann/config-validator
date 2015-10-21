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
package com.github.pjungermann.config;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Generic key-value configuration object / container.
 * Collects its {@link #errors} and provide easy access to them.
 *
 * @author Patrick Jungermann
 */
public class Config extends HashMap<String, Object> {

    /**
     * All its collected errors.
     */
    public final ArrayList<ConfigError> errors = new ArrayList<>();

    /**
     * Creates a fresh and empty config.
     */
    public Config() {
        super();
    }

    /**
     * Creates a new config based on the key-value data provided.
     *
     * @param other    the key-value data for config entries.
     */
    public Config(@NotNull final Map<String, Object> other) {
        super(other);
    }

    /**
     * Creates a new config based on another config.
     *
     * @param other    the other config.
     */
    public Config(@NotNull final Config other) {
        super(other);
        this.errors.addAll(other.errors);
    }

    /**
     * Puts all config entries into it as well as the other's config errors.
     *
     * @param other    the other config.
     */
    public void putAll(@NotNull final Config other) {
        super.putAll(other);
        errors.addAll(other.errors);
    }

    @NotNull
    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("{\n");

        forEach((key, value) ->
                builder.append("\t").append(key).append("=").append(value).append("\n")
        );

        return builder.append("}").toString();
    }

}
