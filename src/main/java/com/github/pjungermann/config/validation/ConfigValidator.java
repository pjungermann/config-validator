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
package com.github.pjungermann.config.validation;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.errors.KeysWithoutSpecificationError;
import com.github.pjungermann.config.loader.ConfigLoader;
import com.github.pjungermann.config.specification.ConfigSpecification;
import com.github.pjungermann.config.specification.ConfigSpecificationLoader;
import com.github.pjungermann.config.specification.constraint.Constraint;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Set;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Validates the {@link Config} based on a {@link ConfigSpecification}.
 *
 * @author Patrick Jungermann
 */
@Component
@Singleton
public class ConfigValidator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ConfigValidator.class);

    private MessageSource messageSource;
    private ConfigLoader configLoader;
    private ConfigSpecificationLoader configSpecificationLoader;

    @Inject
    public ConfigValidator(@NotNull final MessageSource messageSource,
                           @NotNull final ConfigLoader configLoader,
                           @NotNull final ConfigSpecificationLoader configSpecificationLoader) {
        this.messageSource = messageSource;
        this.configLoader = configLoader;
        this.configSpecificationLoader = configSpecificationLoader;
    }

    /**
     * Validates the {@link Config} based on a {@link ConfigSpecification}.
     *
     * @param sources       {@link Config} sources.
     * @param specs         {@link ConfigSpecification} sources (specifications).
     * @param recursive     Whether to recursively load sources files.
     * @param profile       Profile to be applied to a {@link Config}. Only supported by some types.
     * @param strictMode    Whether to use strict mode or not. (No config entry without specification allowed).
     * @throws ConfigValidationException
     *          if there was any specification validation or other type of error
     *          while loading the sources.
     */
    public void validate(@NotNull final String[] sources,
                         @NotNull final String[] specs,
                         final boolean recursive,
                         final String profile,
                         final boolean strictMode) throws ConfigValidationException {
        LOGGER.info("load config from sources");
        final Config config = configLoader.load(profile, recursive, sources);

        LOGGER.info("load specification");
        final ConfigSpecification configSpecification = configSpecificationLoader.load(recursive, specs);
        config.errors.addAll(configSpecification.errors);

        LOGGER.info("apply type conversion");
        configSpecification
                .typeConverter
                .convert(config);

        LOGGER.info("validate config against specification");
        config.errors.addAll(
                configSpecification
                        .constraints
                        .stream()
                        .parallel()
                        .map(constraint -> constraint.validate(config))
                        .filter(error -> error != null)
                        .collect(toList())
        );
        LOGGER.info("validation completed");

        if (strictMode) {
            applyStrictMode(config, configSpecification);
        }

        if (!config.errors.isEmpty()) {
            throw new ConfigValidationException(messageSource, config.errors);
        }
    }

    protected void applyStrictMode(@NotNull final Config config,
                                   @NotNull final ConfigSpecification configSpecification) {
        LOGGER.info("strict mode: check for keys without specification");
        final Set<String> keys = new HashSet<>(config.keySet());
        keys.removeAll(
                configSpecification
                        .constraints
                        .stream()
                        .parallel()
                        .map(Constraint::getKey)
                        .collect(toSet())
        );
        keys.removeAll(
                configSpecification
                        .typeConverter
                        .getKeys()
        );

        if (!keys.isEmpty()) {
            config.errors.add(new KeysWithoutSpecificationError(keys));
        }
    }
}
