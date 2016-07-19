/*
 * Copyright 2016 Patrick Jungermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pjungermann.config.specification.constraint.blank;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.junit.Test;

import java.io.File;
import java.nio.CharBuffer;

import static org.junit.Assert.*;

/**
 * Tests for {@link BlankConstraint}.
 *
 * @author Patrick Jungermann
 */
public class BlankConstraintTest {

    final SourceLine fakeSourceLine = new SourceLine(new File("fake"), -1);

    @Test
    public void skipBlankValues_always_notSkipThem() {
        BlankConstraint blank = new BlankConstraint("fake-key", false, fakeSourceLine);

        assertFalse(blank.skipBlankValues());
    }

    @Test
    public void supports_always_returnTrueForCharSequenceTypesOnly() {
        BlankConstraint blank = new BlankConstraint("fake-key", false, fakeSourceLine);

        assertTrue(blank.supports(CharSequence.class));
        assertTrue(blank.supports(String.class));
        assertTrue(blank.supports(StringBuilder.class));
        assertTrue(blank.supports(StringBuffer.class));
        assertTrue(blank.supports(CharBuffer.class));

        assertFalse(blank.supports(Object.class));
        assertFalse(blank.supports(Integer.class));
        assertFalse(blank.supports(Double.class));
    }

    @Test
    public void isValidExpectation_false_acceptValue() {
        BlankConstraint blank = new BlankConstraint("fake-key", false, fakeSourceLine);
        assertTrue(blank.isValidExpectation());
    }

    @Test
    public void isValidExpectation_true_acceptValue() {
        BlankConstraint blank = new BlankConstraint("fake-key", true, fakeSourceLine);
        assertTrue(blank.isValidExpectation());
    }

    @Test
    public void isValidExpectation_booleanNull_isNotNullArgument() {
        BlankConstraint blank = new BlankConstraint("fake-key", null, fakeSourceLine);
        assertFalse(blank.isValidExpectation());
    }

    @Test
    public void isValidExpectation_nonBooleanValue_notAcceptValue() {
        BlankConstraint blank = new BlankConstraint("fake-key", "non boolean", fakeSourceLine);
        assertFalse(blank.isValidExpectation());
    }

    /**
     * {@code null} values got skipped, and only
     * {@link BlankConstraint#supports(Class) supported types} get checked.
     *
     * Therefore, only blank or non-blank non-null character sequences
     * are expected here.
     */
    @Test
    public void doValidate_blankAllowed_acceptAnyValue() {
        BlankConstraint blank = new BlankConstraint("fake-key", true, fakeSourceLine);

        assertNull(blank.doValidate(""));
        assertNull(blank.doValidate("non blank"));
    }

    @Test
    public void doValidate_blankNotAllowedAndBlank_rejectValue() {
        BlankConstraint blank = new BlankConstraint("fake-key", false, fakeSourceLine);

        ConfigError error = blank.doValidate("");

        assertNotNull(error);
        assertEquals("blank failed for key fake-key", error.toString());
    }

    @Test
    public void doValidate_blankNotAllowedAndNotBlank_acceptValue() {
        BlankConstraint blank = new BlankConstraint("fake-key", false, fakeSourceLine);

        assertNull(blank.doValidate("not blank"));
    }
}
