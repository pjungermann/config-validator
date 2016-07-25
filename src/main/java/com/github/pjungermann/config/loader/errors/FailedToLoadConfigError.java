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
package com.github.pjungermann.config.loader.errors;

import com.github.pjungermann.config.errors.ConfigFileError;
import com.github.pjungermann.config.types.ConfigFactory;
import org.jetbrains.annotations.NotNull;

import java.io.File;

/**
 * Error when the loading of a config files failed due to an exception.
 *
 * @author Patrick Jungermann
 */
public class FailedToLoadConfigError extends ConfigFileError {

    public final ConfigFactory factory;
    public final Throwable cause;

    public FailedToLoadConfigError(final File file, final ConfigFactory factory, final Exception cause) {
        super(file);
        this.factory = factory;
        this.cause = cause;
    }

    @Override
    public String toString() {
        return super.toString() + " via " + factory.toString();
    }

    @NotNull
    @Override
    public String getMessageCode() {
        return "errors.config_file.failed_to_load";
    }

    @NotNull
    @Override
    public Object[] getMessageArguments() {
        final Object[] baseArguments = super.getMessageArguments();

        final Object[] arguments = new Object[baseArguments.length + 2];
        System.arraycopy(baseArguments, 0, arguments, 0, baseArguments.length);

        arguments[arguments.length - 2] = factory.getClass().getName();
        arguments[arguments.length - 1] = cause.toString();

        return arguments;
    }

}
