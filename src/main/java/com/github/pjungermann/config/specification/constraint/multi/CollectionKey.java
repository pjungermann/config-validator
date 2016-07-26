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

import groovy.lang.IntRange;

/**
 * Structure providing access to the parts
 * of a "collection key".
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class CollectionKey {

    public final String key;
    public final String collectionKey;
    public final IntRange entrySelection;
    public final String propertyKey;

    public CollectionKey(final String key,
                         final String collectionKey,
                         final IntRange entrySelection,
                         final String propertyKey) {
        this.key = key;
        this.collectionKey = collectionKey;
        this.entrySelection = entrySelection;
        this.propertyKey = propertyKey;
    }
}
