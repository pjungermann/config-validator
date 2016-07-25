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
package com.github.pjungermann.config.types.yaml;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.types.ConfigConverter;
import com.github.pjungermann.config.types.FileTypeConfigFactory;
import org.jetbrains.annotations.NotNull;
import org.yaml.snakeyaml.Yaml;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link com.github.pjungermann.config.types.ConfigFactory}
 * for Yaml files ({@code .yaml}, {@code .yml}).
 *
 * @author Patrick Jungermann
 */
@Singleton
public class YamlConfigFactory extends FileTypeConfigFactory<LinkedHashMap<String, Object>> {

    private YamlConverter converter;

    public YamlConfigFactory() {
        super("yaml", "yml");
    }

    @Inject
    public void setConverter(@NotNull final YamlConverter converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    protected ConfigConverter<LinkedHashMap<String, Object>> getConverter() {
        return converter;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    protected LinkedHashMap<String, Object> doCreate(@NotNull final File source,
                                                     final String profile,
                                                     @NotNull final Config context) throws IOException {
        try (
                FileInputStream stream = new FileInputStream(source);
                InputStreamReader reader = new InputStreamReader(stream, UTF_8)
        ) {
            return (LinkedHashMap<String, Object>) new Yaml().load(reader);
        }
    }

}
