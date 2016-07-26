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
package com.github.pjungermann.config.specification;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

/**
 * Tests for {@link FailedToLoadSpecificationError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class FailedToLoadSpecificationErrorTest {

    FailedToLoadSpecificationError error;

    @Before
    public void setUp() {
        error = new FailedToLoadSpecificationError(new File("fake"), new ClassCastException());
    }

    @Test
    public void getMessageCode_always_correctValue() {
        assertEquals("errors.specification.failed_to_load", error.getMessageCode());
    }

    @Test
    public void getMessageArguments_always_returnCorrectArguments() throws IOException {
        assertArrayEquals(new Object[]{
                "failed to load specification",
                new File("fake").getCanonicalPath(),
                error.cause.toString()
        }, error.getMessageArguments());
    }

    @Test
    public void getMessage_always_returnCorrectMessage() throws IOException {
        MessageSourceResolvable resolvable = error.getMessage();

        assertNotNull(resolvable);
        assertArrayEquals(new String[]{
                "errors.specification.failed_to_load"
        }, resolvable.getCodes());
        assertArrayEquals(new Object[]{
                "failed to load specification",
                new File("fake").getCanonicalPath(),
                error.cause.toString()
        }, resolvable.getArguments());
        assertEquals("errors.specification.failed_to_load", resolvable.getDefaultMessage());
    }

    @Test
    public void getFilePath_nullFile_returnNullAsString() {
        String path = new FailedToLoadSpecificationError(
                null, new IllegalArgumentException()
        ).getFilePath();

        assertEquals("null", path);
    }

    @Test
    public void getFilePath_nonExisting_returnPath() throws IOException {
        assertEquals(new File("fake").getCanonicalPath(), error.getFilePath());
    }

    @Test
    public void toString_always_validString() throws IOException {
        assertEquals(
                error.getClass().getName() + ": " + new File("fake").getCanonicalPath(),
                error.toString()
        );
    }
}
