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
package com.github.pjungermann.config.specification.types;

import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link TypeConversionFailedError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class TypeConversionFailedErrorTest {

    @Test
    public void toString_valueNotNull_provideToStringWithFields() {
        TypeConversionFailedError error = new TypeConversionFailedError(
                "fake-key", "fake-value", Long.class, new ClassCastException());

        assertEquals("TypeConversionFailedError(key=fake-key, value=fake-value, value.class=class java.lang.String, type=class java.lang.Long)", error.toString());
    }

    @Test
    public void toString_valueNull_provideToStringWithFields() {
        TypeConversionFailedError error = new TypeConversionFailedError(
                "fake-key", null, Long.class, new ClassCastException());

        assertEquals("TypeConversionFailedError(key=fake-key, value=null, value.class=null, type=class java.lang.Long)", error.toString());
    }

    @Test
    public void getMessage_always_returnMessageResolvableWithCorrectCodes() {
        TypeConversionFailedError error = new TypeConversionFailedError(
                "fake-key", "fake-value", Long.class, new ClassCastException());

        MessageSourceResolvable resolvable = error.getMessage();

        assertArrayEquals(new String[]{
                TypeConversionFailedError.MESSAGE_CODE
        }, resolvable.getCodes());
        assertArrayEquals(new Object[]{
                error.key,
                error.value,
                error.type,
                error.cause
        }, resolvable.getArguments());
        assertEquals(TypeConversionFailedError.MESSAGE_CODE, resolvable.getDefaultMessage());
    }
}
