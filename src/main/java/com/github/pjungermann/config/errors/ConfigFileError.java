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
package com.github.pjungermann.config.errors;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.utils.NameUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.io.File;
import java.io.IOException;

import static java.util.Locale.ENGLISH;

/**
 * Base implementation for any config file related errors.
 *
 * @author Patrick Jungermann
 */
public abstract class ConfigFileError implements ConfigError {

    public static final String DEFAULT_MESSAGE_CODE = "errors.config_file.default";

    public final File file;

    public ConfigFileError(final File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return getClass().getName() + ": " + getFilePath();
    }

    @NotNull
    public String getFilePath() {
        if (file == null) {
            return "null";
        }

        try {
            return file.getCanonicalPath();

        } catch (IOException e) {
            return file.getPath();
        }
    }

    @NotNull
    public String getMessageCode() {
        return DEFAULT_MESSAGE_CODE;
    }

    @NotNull
    public Object[] getMessageArguments() {
        return new Object[]{
                NameUtils.getNaturalName(getClass(), "Error").toLowerCase(ENGLISH),
                getFilePath()
        };
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        final String code = getMessageCode();

        return new DefaultMessageSourceResolvable(
                new String[]{
                        code,
                        DEFAULT_MESSAGE_CODE
                },
                getMessageArguments(),
                code
        );
    }

}
