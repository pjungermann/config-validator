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
import com.github.pjungermann.config.types.FileTypeConfigFactory;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Properties;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * {@link com.github.pjungermann.config.types.ConfigFactory}
 * for {@link Properties} files.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class PropertiesConfigFactory extends FileTypeConfigFactory<Properties> {

    private PropertiesConverter converter;

    public PropertiesConfigFactory() {
        super("properties");
    }

    @Inject
    public void setConverter(@NotNull final PropertiesConverter converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    protected ConfigConverter<Properties> getConverter() {
        return converter;
    }

    @NotNull
    @Override
    protected Properties doCreate(@NotNull File source, String profile, @NotNull Config context) throws IOException {
        Properties properties = new Properties();
        try (
                FileInputStream stream = new FileInputStream(source);
                InputStreamReader reader = new InputStreamReader(stream, UTF_8)
        ) {
            properties.load(reader);
        }

        return properties;
    }

}
