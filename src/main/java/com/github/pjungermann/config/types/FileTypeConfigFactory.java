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

import org.jetbrains.annotations.NotNull;

import java.io.File;

import static com.github.pjungermann.config.utils.FileUtils.isOfType;

/**
 * Base {@link ConfigFactory} implementation for factories
 * which support config files with certain file extensions.
 *
 * @author Patrick Jungermann
 */
public abstract class FileTypeConfigFactory<OtherConfigType> extends AbstractConfigFactory<OtherConfigType> {

    protected final String[] types;

    public FileTypeConfigFactory(@NotNull final String fileType, @NotNull final String... moreFileTypes) {
        types = new String[moreFileTypes.length + 1];
        types[0] = fileType.toLowerCase();
        for (int i = 0; i < moreFileTypes.length; i++) {
            types[i + 1] = moreFileTypes[i].toLowerCase();
        }
    }

    @Override
    public boolean supports(@NotNull final File source) {
        return isOfType(source, types);
    }

}
