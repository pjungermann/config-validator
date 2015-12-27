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
package com.github.pjungermann.config.types.groovy;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.KeyBuilder;
import com.github.pjungermann.config.types.ConfigConverter;
import groovy.util.ConfigObject;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashSet;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

/**
 * Converter from {@link ConfigObject} (Groovy Config) to {@link Config}
 * and vise versa.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class ConfigObjectConverter implements ConfigConverter<ConfigObject> {

    private KeyBuilder keyBuilder;

    @Inject
    public void setKeyBuilder(@NotNull final KeyBuilder keyBuilder) {
        this.keyBuilder = keyBuilder;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public Config from(@NotNull final ConfigObject convertible) {
        return new Config(convertible.flatten());
    }

    @NotNull
    @Override
    public ConfigObject to(@NotNull final Config config) {
        ConfigObject newConfig = new ConfigObject();
        newConfig.putAll(config);

        return unflatten(newConfig);
    }

    @NotNull
    @SuppressWarnings("unchecked")
    protected ConfigObject unflatten(@NotNull final ConfigObject config) {
        final Map<String, Object> syncConfig = synchronizedMap(config);

        new HashSet<>(config.keySet())
                .stream()
                .forEach(key -> addHierarchicalEntry(syncConfig, (String) key, config.get(key)));

        return config;
    }

    @SuppressWarnings("unchecked")
    protected void addHierarchicalEntry(@NotNull final Map config, @NotNull final String key, final Object value) {
        final int index = key.indexOf(keyBuilder.getSeparator());
        if (index == -1) {
            config.put(key, value);
            return;
        }

        final String rootKey = key.substring(0, index);
        config.putIfAbsent(rootKey, new ConfigObject());
        final Map subConfig = synchronizedMap((Map) config.get(rootKey));

        addHierarchicalEntry(subConfig, key.substring(index + 1), value);
    }

}
