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
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

/**
 * Used when a collection's entry's getter exists,
 * but was not accessible (e.g., invocation failed).
 *
 * @author patrick.jungermann
 * @since 2016-07-27
 */
public class PropertyGetterAccessFailedError implements ConfigError {

    private final CollectionKey key;
    private final Object entry;
    private final String getterName;

    public PropertyGetterAccessFailedError(@NotNull final CollectionKey key,
                                           @NotNull final Object entry,
                                           @NotNull final String getterName) {
        this.key = key;
        this.entry = entry;
        this.getterName = getterName;
    }

    @NotNull
    protected String getMessageCode() {
        return "errors.collection.entry.property.getter.access_failed";
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
                        entry,
                        getterName
                },
                code
        );
    }
}
