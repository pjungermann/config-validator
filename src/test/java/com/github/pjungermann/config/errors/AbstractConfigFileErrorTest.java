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

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Basic test setup for classes which come of {@link ConfigFileError}.
 *
 * @author Patrick Jungermann
 */
public abstract class AbstractConfigFileErrorTest<E extends ConfigFileError> {

    public File file = new File("fake.file");
    public E error;
    public E errorWithoutFile;
    public String expectedNameArgument;

    public abstract E getError(File file);

    public abstract String getExpectedNameArgument();

    @Before
    public void setUp() {
        error = getError(file);
        errorWithoutFile = getError(null);
        expectedNameArgument = getExpectedNameArgument();
    }

    public String getExpectedToStringValue(E error) throws IOException {
        if (error.file == null) {
            return error.getClass().getName() + ": null";
        }

        return error.getClass().getName() + ": " + file.getCanonicalPath();
    }

    @Test
    public void toString_withFile_provideCorrectRepresentation() throws IOException {
        assertEquals(
                getExpectedToStringValue(error),
                error.toString());
    }

    @Test
    public void toString_withoutFile_provideCorrectRepresentation() throws IOException {
        assertEquals(
                getExpectedToStringValue(errorWithoutFile),
                errorWithoutFile.toString());
    }

    @Test
    public void getFilePath_withoutFile_returnNullString() {
        assertEquals("null", errorWithoutFile.getFilePath());
    }

    @Test
    public void getFilePath_withFile_returnNullString() throws IOException {
        assertEquals(file.getCanonicalPath(), error.getFilePath());
    }

    @Test
    public void getFilePath_withIllegalFileName_returnNullString() {
        String filename = "file\u0000name";
        ConfigFileError error = getError(new File(filename));

        assertEquals(filename, error.getFilePath());
    }

    @Test
    public void getMessageCode_always_returnsExpectedCode() {
        assertEquals(ConfigFileError.DEFAULT_MESSAGE_CODE, error.getMessageCode());
    }

    @Test
    public void getMessageArguments_always_expectedArguments() throws IOException {
        Object[] arguments = error.getMessageArguments();

        assertEquals(expectedNameArgument, arguments[0]);
        assertEquals(file.getCanonicalPath(), arguments[1]);
    }

    @Test
    public void getMessage_always_returnsResolvableWithCorrectCodesAndArgsAndDefaultMessage() {
        MessageSourceResolvable resolvable = error.getMessage();
        String[] codes = resolvable.getCodes();
        Object[] arguments = resolvable.getArguments();
        String defaultMessage = resolvable.getDefaultMessage();

        assertEquals(error.getMessageCode(), codes[0]);
        assertEquals(ConfigFileError.DEFAULT_MESSAGE_CODE, codes[1]);
        assertArrayEquals(error.getMessageArguments(), arguments);
        assertEquals(error.getMessageCode(), defaultMessage);
    }

}
