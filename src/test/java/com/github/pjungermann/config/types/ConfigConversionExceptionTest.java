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
package com.github.pjungermann.config.types;

import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link ConfigConversionException}.
 *
 * @author Patrick Jungermann
 */
public class ConfigConversionExceptionTest {

    @Test
    public void instanceWithIOExceptionAsCause() {
        IOException cause = new IOException("io exception");
        ConfigConversionException e = new ConfigConversionException(
                "fake detailed message", cause);

        assertEquals("fake detailed message", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    @Test
    public void instanceWithExceptionAsCause() {
        Exception cause = new Exception("generic exception");
        ConfigConversionException e = new ConfigConversionException(
                "fake detailed message", cause);

        assertEquals("fake detailed message", e.getMessage());
        assertEquals(cause, e.getCause());
    }

    @Test
    public void instanceWithThrowableAsCause() {
        Throwable cause = new Throwable("generic throwable");
        ConfigConversionException e = new ConfigConversionException(
                "fake detailed message", cause);

        assertEquals("fake detailed message", e.getMessage());
        assertEquals(cause, e.getCause());
    }
}
