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
package com.github.pjungermann.config.types.yaml;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.KeyBuilder;
import com.github.pjungermann.config.types.ConfigConverter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.Collections.synchronizedMap;

/**
 * Converter from Yaml to {@link Config} and vise versa.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class YamlConverter implements ConfigConverter<LinkedHashMap<String, Object>> {

    private KeyBuilder keyBuilder;

    @Inject
    public void setKeyBuilder(@NotNull final KeyBuilder keyBuilder) {
        this.keyBuilder = keyBuilder;
    }

    @NotNull
    @Override
    public Config from(@NotNull final LinkedHashMap<String, Object> convertible) {
        final Config config = new Config();

        final Map<String, Object> syncConfig = synchronizedMap(config);
        populate(syncConfig, convertible, "");

        return config;
    }

    protected void populate(@NotNull final Map<String, Object> config,
                            @NotNull final Map<String, Object> other,
                            @NotNull final String keyPrefix) {
        other.forEach((entryKey, entryValue) -> {
            final String key = keyPrefix + entryKey;
            populate(config, key, entryValue);
        });
    }

    @SuppressWarnings("unchecked")
    protected void populate(@NotNull final Map<String, Object> config,
                            @NotNull final String key,
                            final Object value) {
        if (value instanceof Map) {
            populate(config, (Map<String, Object>) value, keyBuilder.toPrefix(key));
            return;
        }

        if (value instanceof Collection) {
            int i = 0;
            for (Object entry : (Collection) value) {
                populate(config, keyBuilder.toPrefix(key) + i, entry);
                i++;
            }
            return;
        }

        config.put(key, value);
    }

    @NotNull
    @Override
    public LinkedHashMap<String, Object> to(@NotNull final Config config) {
        return new LinkedHashMap<>(config);
    }

}
