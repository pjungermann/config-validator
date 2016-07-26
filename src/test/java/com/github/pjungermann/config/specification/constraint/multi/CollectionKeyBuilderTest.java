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
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Tests for {@link CollectionKeyBuilder}.
 *
 * @author patrick.jungermann
 * @since 2016-07-27
 */
@RunWith(Parameterized.class)
public class CollectionKeyBuilderTest {

    @Parameterized.Parameter(0)
    public String key;

    @Parameterized.Parameter(1)
    public String collectionKey;

    @Parameterized.Parameter(2)
    public IntRange entrySelection;

    @Parameterized.Parameter(3)
    public String propertyKey;

    @Parameterized.Parameters
    public static Object[][] getParameters() {
        return new Object[][]{
                new Object[]{
                        "collection_key.[*]", "collection_key", new IntRange(true, 0, -1), null
                },
                new Object[]{
                        "collection_key.[1..3]", "collection_key", new IntRange(true, 1, 3), null
                },
                new Object[]{
                        "collection_key.[4]", "collection_key", new IntRange(true, 4, 4), null
                },
                new Object[]{
                        "multi.level.collection_key.[*]", "multi.level.collection_key", new IntRange(true, 0, -1), null
                },
                new Object[]{
                        "multi.level.collection_key.[1..3]", "multi.level.collection_key", new IntRange(true, 1, 3), null
                },
                new Object[]{
                        "multi.level.collection_key.[4]", "multi.level.collection_key", new IntRange(true, 4, 4), null
                },
                new Object[]{
                        "collection_key.[*].property_name", "collection_key", new IntRange(true, 0, -1), "property_name"
                },
                new Object[]{
                        "collection_key.[1..3].property_name", "collection_key", new IntRange(true, 1, 3), "property_name"
                },
                new Object[]{
                        "collection_key.[4].property_name", "collection_key", new IntRange(true, 4, 4), "property_name"
                },
                new Object[]{
                        "multi.level.collection_key.[*].property_name", "multi.level.collection_key", new IntRange(true, 0, -1), "property_name"
                },
                new Object[]{
                        "multi.level.collection_key.[1..3].property_name", "multi.level.collection_key", new IntRange(true, 1, 3), "property_name"
                },
                new Object[]{
                        "multi.level.collection_key.[4].property_name", "multi.level.collection_key", new IntRange(true, 4, 4), "property_name"
                }
        };
    }

    @Test
    public void build_always_valid() {
        CollectionKey struct = CollectionKeyBuilder.build(key);

        assertNotNull(struct);
        assertEquals(collectionKey, struct.collectionKey);
        assertEquals(entrySelection, struct.entrySelection);
        assertEquals(propertyKey, struct.propertyKey);
    }
}
