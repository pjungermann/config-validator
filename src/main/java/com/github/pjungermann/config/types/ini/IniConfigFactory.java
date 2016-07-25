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
package com.github.pjungermann.config.types.ini;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.types.ConfigConverter;
import com.github.pjungermann.config.types.FileTypeConfigFactory;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;

/**
 * {@link com.github.pjungermann.config.types.ConfigFactory}
 * to load {@code .ini} files.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class IniConfigFactory extends FileTypeConfigFactory<Ini> {

    private IniConverter converter;

    public IniConfigFactory() {
        super("ini");
    }

    @Inject
    public void setConverter(@NotNull final IniConverter converter) {
        this.converter = converter;
    }

    @NotNull
    @Override
    protected ConfigConverter<Ini> getConverter() {
        return converter;
    }

    @NotNull
    @Override
    protected Ini doCreate(@NotNull final File source, final String profile, @NotNull final Config context) throws IOException {
        return new Ini(source);
    }

}
