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
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import static org.junit.Assert.*;

/**
 * Tests for {@link PropertyFieldAccessFailedError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-27
 */
public class PropertyFieldAccessFailedErrorTest {

    @Test
    public void getMessageCode_always_correctCode() {
        CollectionKey key = new CollectionKey(
                "fake-key",
                "fake-collection-key",
                new IntRange(true, 1, 100),
                "fake-property"
        );

        PropertyFieldAccessFailedError error = new PropertyFieldAccessFailedError(key, "invalid value", "fake-field");

        assertEquals("errors.collection.entry.property.field.access_failed", error.getMessageCode());
    }

    @Test
    public void getMessage_always_correctDataUsed() {
        CollectionKey key = new CollectionKey(
                "fake-key",
                "fake-collection-key",
                new IntRange(true, 1, 100),
                "fake-property"
        );

        PropertyFieldAccessFailedError error = new PropertyFieldAccessFailedError(key, "invalid value", "fake-field");
        MessageSourceResolvable resolvable = error.getMessage();

        assertNotNull(resolvable);
        assertArrayEquals(new String[]{error.getMessageCode()}, resolvable.getCodes());
        assertArrayEquals(new Object[]{
                key.key,
                key.collectionKey,
                "invalid value",
                "fake-field"
        }, resolvable.getArguments());
        assertEquals(error.getMessageCode(), resolvable.getDefaultMessage());
    }
}
