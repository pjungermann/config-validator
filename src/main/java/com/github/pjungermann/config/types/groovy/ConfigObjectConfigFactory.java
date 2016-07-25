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
package com.github.pjungermann.config.types.groovy;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.types.ConfigConversionException;
import com.github.pjungermann.config.types.ConfigConverter;
import com.github.pjungermann.config.types.FileTypeConfigFactory;
import groovy.util.ConfigObject;
import groovy.util.ConfigSlurper;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

/**
 * {@link com.github.pjungermann.config.types.ConfigFactory}
 * for {@link ConfigObject ConfigObjects} loaded from {@code .groovy}
 * files / scripts.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class ConfigObjectConfigFactory extends FileTypeConfigFactory<ConfigObject> {

    private ConfigObjectConverter converter;

    public ConfigObjectConfigFactory() {
        super("groovy");
    }

    @Inject
    public void setConverter(@NotNull final ConfigObjectConverter converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    protected ConfigConverter<ConfigObject> getConverter() {
        return converter;
    }

    @NotNull
    @Override
    protected ConfigObject doCreate(@NotNull final File source,
                                    @Nullable final String profile,
                                    @NotNull final Config context) throws IOException {
        final ConfigSlurper slurper = profile == null ? new ConfigSlurper() : new ConfigSlurper(profile);
        final ConfigObject binding;
        try {
            binding = getConverter().to(context);

        } catch (ConfigConversionException e) {
            throw new IOException(e);
        }
        slurper.setBinding(binding);

        return slurper.parse(source.toURI().toURL());
    }

}
