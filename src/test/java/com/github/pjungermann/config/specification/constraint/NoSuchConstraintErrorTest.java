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
package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.reference.SourceLine;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for {@link NoSuchConstraintError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class NoSuchConstraintErrorTest {

    NoSuchConstraintError error;

    @Before
    public void setUp() {
        error = new NoSuchConstraintError("doesNotExist", "fake-key", new SourceLine(new File("fake"), -1));
    }

    @Test
    public void toString_always_outputWithFields() {
        assertEquals("NoSuchConstraintError(name=doesNotExist, key=fake-key, sourceLine=fake(fake:-1))", error.toString());
    }

    @Test
    public void getMessage_always_useCorrectCodes() {
        MessageSourceResolvable resolvable = error.getMessage();

        assertNotNull(resolvable);
        assertArrayEquals(new String[]{
                NoSuchConstraintError.MESSAGE_CODE
        }, resolvable.getCodes());
        assertArrayEquals(new Object[]{
                error.name,
                error.key,
                error.sourceLine
        }, resolvable.getArguments());
        assertEquals("errors.no_such_constraint", resolvable.getDefaultMessage());
    }
}
