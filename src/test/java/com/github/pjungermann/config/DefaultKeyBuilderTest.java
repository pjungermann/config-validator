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
package com.github.pjungermann.config;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link DefaultKeyBuilder}.
 *
 * @author Patrick Jungermann
 */
public class DefaultKeyBuilderTest {

    @Test
    public void separator_always_isDot() {
        assertEquals(".", DefaultKeyBuilder.SEPARATOR);
    }

    @Test
    public void getSeparator_always_returnsDot() {
        assertEquals(".", new DefaultKeyBuilder().getSeparator());
    }

    @Test
    public void toPrefix_always_returnsKeyWithDot() {
        assertEquals("my.key.", new DefaultKeyBuilder().toPrefix("my.key"));
    }

}
