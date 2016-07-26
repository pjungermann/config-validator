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
package com.github.pjungermann.config.specification.constraint.multi;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Used in case that the collection's entry was neither
 * a {@link Config}, {@link java.util.Map} or its property
 * (via getter or field).
 *
 * @author patrick.jungermann
 * @since 2016-07-27
 */
public class UnsupportedCollectionEntryPropertyError implements ConfigError {

    private final CollectionKey key;
    private final Object entry;

    public UnsupportedCollectionEntryPropertyError(@NotNull final CollectionKey key,
                                                   @NotNull final Object entry) {
        this.key = key;
        this.entry = entry;
    }

    @NotNull
    protected String getMessageCode() {
        return "error.collection.property.unsupported";
    }

    /**
     * The error message to be rendered.
     *
     * @return the error message to be rendered.
     */
    @NotNull
    @Override
    public MessageSourceResolvable getMessage() {
        final String code = getMessageCode();

        return new DefaultMessageSourceResolvable(
                new String[]{code},
                new Object[]{
                        key.key,
                        key.collectionKey,
                        entry
                },
                code
        );
    }
}
