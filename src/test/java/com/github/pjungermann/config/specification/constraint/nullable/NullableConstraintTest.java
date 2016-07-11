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
package com.github.pjungermann.config.specification.constraint.nullable;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for {@link NullableConstraint}.
 *
 * @author Patrick Jungermann
 */
public class NullableConstraintTest {

    final SourceLine fakeSourceLine = new SourceLine(new File("fake"), -1);

    @Test
    public void skipNullValues_always_notSkipThem() {
        NullableConstraint nullable = new NullableConstraint("fake-key", false, fakeSourceLine);

        assertFalse(nullable.skipNullValues());
    }

    @Test
    public void supports_always_allTypes() {
        NullableConstraint nullable = new NullableConstraint("fake-key", false, fakeSourceLine);

        assertTrue(nullable.supports(Object.class));
    }

    @Test
    public void isValidExpectation_false_acceptValue() {
        NullableConstraint nullable = new NullableConstraint("fake-key", false, fakeSourceLine);
        assertTrue(nullable.isValidExpectation());
    }

    @Test
    public void isValidExpectation_true_acceptValue() {
        NullableConstraint nullable = new NullableConstraint("fake-key", true, fakeSourceLine);
        assertTrue(nullable.isValidExpectation());
    }

    @Test(expected = IllegalArgumentException.class)
    @SuppressWarnings("ConstantConditions")
    public void isValidExpectation_booleanNull_isNotNullArgument() {
        new NullableConstraint("fake-key", null, fakeSourceLine);
    }

    @Test
    public void isValidExpectation_nonBooleanValue_notAcceptValue() {
        NullableConstraint nullable = new NullableConstraint("fake-key", "non boolean", fakeSourceLine);
        assertFalse(nullable.isValidExpectation());
    }

    @Test
    public void doValidate_nullValuesAllowed_acceptAllValues() {
        NullableConstraint nullable = new NullableConstraint("fake-key", true, fakeSourceLine);

        assertNull(nullable.doValidate(null));
        assertNull(nullable.doValidate("string"));
        assertNull(nullable.doValidate(1234));
        assertNull(nullable.doValidate(new Object()));
    }

    @Test
    public void doValidate_nullValuesNotAllowedAndNotNull_acceptValue() {
        NullableConstraint nullable = new NullableConstraint("fake-key", false, fakeSourceLine);

        assertNull(nullable.doValidate("non-null value"));
    }

    @Test
    public void doValidate_nullValuesNotAllowedAndNull_rejectValue() {
        NullableConstraint nullable = new NullableConstraint("fake-key", false, fakeSourceLine);

        ConfigError error = nullable.doValidate(null);
        assertEquals("nullable failed for key fake-key", error.toString());
    }
}
