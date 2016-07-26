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
import org.springframework.context.support.StaticMessageSource;

import java.util.Arrays;
import java.util.Locale;

import static java.util.Collections.singleton;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link MultiConfigError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-27
 */
public class MultiConfigErrorTest {

    @Test(expected = UnsupportedOperationException.class)
    public void getMessage_always_correctDataUsed() {
        CollectionKey key = new CollectionKey(
                "fake-key",
                "fake-collection-key",
                new IntRange(true, 1, 100),
                "fake-property"
        );

        new MultiConfigError(key, singleton(new NoCollectionError(key, "invalid")))
                .getMessage();
    }

    @Test
    public void toMessage_always_renderMessageSummary() {
        StaticMessageSource messageSource = new StaticMessageSource();
        CollectionKey key = new CollectionKey(
                "fake-key",
                "fake-collection-key",
                new IntRange(true, 1, 100),
                "fake-property"
        );

        MultiConfigError error = new MultiConfigError(
                key,
                Arrays.asList(
                        new NoCollectionError(key, "invalid"),
                        new UnsupportedCollectionEntryPropertyError(key, "invalid")
                )
        );

        assertEquals(
                "validation errors for collection with key \"fake-collection-key\":\n" +
                        "  - errors.collection.no_collection\n" +
                        "  - error.collection.property.unsupported",
                error.toMessage(messageSource, Locale.getDefault()));
    }
}
