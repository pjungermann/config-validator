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
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.Set;

import static java.util.Collections.emptySet;

/**
 * Selects a suitable {@link ConfigFactory} for the
 * provided {@link File config source file} based on
 * all available {@link ConfigFactory} beans.
 *
 * @author Patrick Jungermann
 * @see ConfigFactory#supports(File)
 */
@Component
@Singleton
public class DefaultConfigFactorySelector implements ConfigFactorySelector {

    private Set<ConfigFactory> configFactories = emptySet();

    @Inject
    public void setConfigFactories(@Nullable Set<ConfigFactory> configFactories) {
        this.configFactories = configFactories == null
                ? emptySet()
                : configFactories;
    }

    @Nullable
    @Override
    public ConfigFactory getFactory(@NotNull final File source) {
        for (final ConfigFactory factory : configFactories) {
            if (factory.supports(source)) {
                return factory;
            }
        }

        return null;
    }

}
