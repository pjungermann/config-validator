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
package com.github.pjungermann.config;

import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;

/**
 * Any kind of error related to the {@link Config}
 * including its loading and validation.
 *
 * @author Patrick Jungermann
 */
public interface ConfigError {

    /**
     * The error message to be rendered.
     *
     * @return the error message to be rendered.
     */
    @NotNull
    MessageSourceResolvable getMessage();

}