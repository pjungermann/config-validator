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

import com.github.pjungermann.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;

/**
 * Abstract {@link ConfigFactory} implementation
 * which loads a config source as another config type
 * and then converts that config type by utilizing
 * a {@link ConfigConverter converter}.
 *
 * @author Patrick Jungermann
 */
public abstract class AbstractConfigFactory<OtherConfigType> implements ConfigFactory {

    @NotNull
    protected abstract ConfigConverter<OtherConfigType> getConverter();

    @NotNull
    protected abstract OtherConfigType doCreate(@NotNull File source,
                                                @Nullable String profile,
                                                @NotNull Config context) throws IOException;

    @NotNull
    public Config create(@NotNull final File source,
                         @Nullable final String profile,
                         @NotNull final Config context) throws IOException, ConfigConversionException {
        final OtherConfigType other = doCreate(source, profile, context);

        return getConverter().from(other);
    }

}
