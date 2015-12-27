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
package com.github.pjungermann.config.types.properties;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.types.ConfigConverter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Singleton;
import java.util.Map;
import java.util.Properties;

import static java.util.Collections.synchronizedMap;

/**
 * Converter from {@link Properties} to {@link Config} and vise versa.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class PropertiesConverter implements ConfigConverter<Properties> {

    @NotNull
    @Override
    public Config from(@NotNull final Properties convertible) {
        final Config config = new Config();

        final Map<String, Object> syncConfig = synchronizedMap(config);
        convertible.forEach((key, value) -> syncConfig.put(key.toString(), value));

        return config;
    }

    @NotNull
    @Override
    public Properties to(@NotNull final Config config) {
        final Properties properties = new Properties();

        config.forEach((key, value) -> {
            if (value == null) {
                value = "";
            }

            properties.put(key, value.toString());
        });

        return properties;
    }

}
