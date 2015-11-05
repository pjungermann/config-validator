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
import com.github.pjungermann.config.loader.errors.FailedToLoadConfigError;
import com.github.pjungermann.config.loader.errors.NoSuchFileError;
import com.github.pjungermann.config.loader.errors.NoSuitableConfigFactoryFoundError;
import com.github.pjungermann.config.types.ConfigFactory;
import com.github.pjungermann.config.types.ConfigFactorySelector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;

/**
 * Loads the config from the given sources.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class DefaultConfigLoader implements ConfigLoader {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultConfigLoader.class);

    private ConfigFactorySelector configFactorySelector;

    @Inject
    public void setConfigFactorySelector(@NotNull final ConfigFactorySelector configFactorySelector) {
        this.configFactorySelector = configFactorySelector;
    }

    @NotNull
    @Override
    public Config load(@Nullable final String profile, @NotNull final Config context, final boolean recursive, @NotNull final File source) {
        LOGGER.info("load config from {}", source);

        if (!source.exists()) {
            final Config config = new Config();
            config.errors.add(new NoSuchFileError(source));
            return config;
        }

        if (source.isFile()) {
            return loadFromFile(source, profile, context);
        }

        final Config config = new Config();
        final File[] subSources = source.listFiles();
        if (subSources == null) {
            return config;
        }

        for (final File subSource : subSources) {
            if (subSource.isFile() || recursive) {
                final Config subConfig = load(profile, context, recursive, subSource);
                config.putAll(subConfig);
                context.putAll(subConfig);
            }
        }

        return config;
    }

    @NotNull
    private Config loadFromFile(@NotNull final File source,
                                @Nullable final String profile,
                                @NotNull final Config context) {
        final Config config = new Config();
        final ConfigFactory factory = configFactorySelector.getFactory(source);

        if (factory == null) {
            config.errors.add(new NoSuitableConfigFactoryFoundError(source));
            return config;
        }

        try {
            config.putAll(factory.create(source, profile, context));
            return config;

        } catch (Exception e) {
            config.errors.add(new FailedToLoadConfigError(source, factory, e));
            return config;
        }
    }

}
