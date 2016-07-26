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
package com.github.pjungermann.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.inject.Singleton;

/**
 * Default {@link KeyBuilder} implementation.
 *
 * @author Patrick Jungermann
 */
@Component
@Singleton
public class DefaultKeyBuilder implements KeyBuilder {

    /**
     * Separator used between different key levels.
     */
    public static final String SEPARATOR = ".";

    @NotNull
    @Override
    public String getSeparator() {
        return SEPARATOR;
    }

    @NotNull
    @Override
    public String toPrefix(@NotNull final String key) {
        return key + getSeparator();
    }

}
