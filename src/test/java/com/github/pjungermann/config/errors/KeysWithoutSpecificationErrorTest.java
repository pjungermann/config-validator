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

import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.*;

/**
 * Tests for {@link KeysWithoutSpecificationError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class KeysWithoutSpecificationErrorTest {

    @Test
    public void getMessageCode_always_returnCorrectCode() {
        String code = new KeysWithoutSpecificationError(Collections.<String>emptyList()).getMessageCode();

        assertEquals("errors.keys_without_specification", code);
    }

    @Test
    public void getMessage_always_returnCorrectMessage() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        KeysWithoutSpecificationError error = new KeysWithoutSpecificationError(keys);

        MessageSourceResolvable resolvable = error.getMessage();

        assertNotNull(resolvable);
        assertArrayEquals(new String[]{error.getMessageCode()}, resolvable.getCodes());
        assertArrayEquals(new Object[]{new TreeSet<>(keys)}, resolvable.getArguments());
        assertEquals(error.getMessageCode(), resolvable.getDefaultMessage());
    }

    @Test
    public void toString_always_containsSimpleClassNameAndKeys() {
        List<String> keys = Arrays.asList("key1", "key2", "key3");
        KeysWithoutSpecificationError error = new KeysWithoutSpecificationError(keys);

        assertEquals(
                "KeysWithoutSpecificationError(keys=[key1, key2, key3])",
                error.toString());
    }
}
