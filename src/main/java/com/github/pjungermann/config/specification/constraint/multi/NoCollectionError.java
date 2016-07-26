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

import com.github.pjungermann.config.ConfigError;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Collection type specification definitions
 * define a "collection" and a sub-selection
 * of its entries as target for the actual
 * specification.
 *
 * This error is used in case that the object
 * retrieved via the collection's key is not
 * a {@link java.util.Collection} and therefore
 * cannot be checked.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class NoCollectionError implements ConfigError {

    private final CollectionKey key;
    private final Object value;

    public NoCollectionError(@NotNull final CollectionKey key, @Nullable final Object value) {
        this.key = key;
        this.value = value;
    }

    @NotNull
    protected String getMessageCode() {
        return "errors.collection.no_collection";
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
                new String[]{
                        code
                },
                new Object[]{
                        key.key,
                        key.collectionKey,
                        value
                },
                code
        );
    }
}
