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
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.util.Collection;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 * Config error for config key which don't have a specification.
 *
 * @author Patrick Jungermann
 */
public class KeysWithoutSpecificationError implements ConfigError {

    private final SortedSet<String> keys;

    public KeysWithoutSpecificationError(@NotNull final Collection<String> keys) {
        this.keys = new TreeSet<>(keys);
    }

    @NotNull
    protected String getMessageCode() {
        return "errors.keys_without_specification";
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        final String code = getMessageCode();

        return new DefaultMessageSourceResolvable(
                new String[]{code},
                new Object[]{keys},
                code
        );
    }

    @Override
    public String toString() {
        return getClass().getSimpleName() + "(keys=" + keys + ")";
    }
}
