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

package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for {@link AbstractConstraint}.
 *
 * @author Patrick Jungermann
 */
public class AbstractConstraintTest {

    AbstractConstraint constraint;

    @Before
    public void setUp() {
        constraint = new FakeConstraint("fake-key", 1234, new SourceLine(new File("fake"), -1));
    }

    @Test
    public void definedAt_always_returnsProvidedSourceLine() {
        assertEquals(new SourceLine(new File("fake"), -1), constraint.definedAt());
    }

    @Test
    public void getKey_always_returnsTheConfigKey() {
        assertEquals("fake-key", constraint.getKey());
    }

    @Test
    public void validate_invalidExpectation_returnError() {
        ((FakeConstraint) constraint).validExpectation = false;

        ConfigError error = constraint.validate(new Config());

        assertNotNull(error);
        assertTrue(error instanceof InvalidConstraintConfigError);
        assertEquals("Illegal config for constraint fake for config key fake-key: 1234", error.toString());
    }

    @Test
    public void skipNullValues_always_defaultReturnsTrue() {
        assertTrue(constraint.skipNullValues());
    }

    @Test
    public void validate_nullValueAndSkipNull_returnNull() {
        ConfigError error = constraint.validate(new Config());

        assertNull(error);
    }

    @Test
    public void validate_nullValueAndNotSkipNull_executeDoValidateForValue() {
        constraint = new FakeConstraint("fake-key", 1234, new SourceLine(new File("fake"), -1)) {
            @Override
            protected boolean skipNullValues() {
                return false;
            }
        };
        ConfigError error = constraint.validate(new Config());

        assertNotNull(error);
        assertEquals("<null>", error.getMessage().getCodes()[0]);
    }

    @Test
    public void skipBlankValues_always_defaultReturnsTrue() {
        assertTrue(constraint.skipBlankValues());
    }

    @Test
    public void validate_blankValueAndSkipBlank_returnNull() {
        Config config = new Config();
        config.put("fake-key", "  ");

        ConfigError error = constraint.validate(config);

        assertNull(error);
    }

    @Test
    public void validate_blankValueAndNotSkipBlank_executeDoValidateForValue() {
        constraint = new FakeConstraint("fake-key", 1234, new SourceLine(new File("fake"), -1)) {
            @Override
            protected boolean skipBlankValues() {
                return false;
            }
        };
        Config config = new Config();
        config.put("fake-key", "  ");

        ConfigError error = constraint.validate(config);

        assertNotNull(error);
        assertEquals("  ", error.getMessage().getCodes()[0]);
    }

    @Test
    public void getMessageCode_always_containsConstraintName() {
        assertEquals("constraints.invalid.fake.message", constraint.getMessageCode());
    }

    @Test
    public void getMessage_always_resolvableWithCorrectCodesAndArguments() {
        MessageSourceResolvable resolvable = constraint.getMessage("fake value");

        assertArrayEquals(new String[]{
                "constraints.invalid.fake.message",
                "constraints.invalid.default.message"
        }, resolvable.getCodes());
        assertArrayEquals(new Object[]{
                constraint.definedAt(), constraint.getKey(), "fake value", constraint.expectation, constraint.getName()
        }, resolvable.getArguments());
        assertEquals("constraints.invalid.fake.message", resolvable.getDefaultMessage());
    }

    @Test
    public void toString_always_containsBaseMetaData() {
        String expected = "{constraintClassSimple}: {key}({constraint name}: {expectation}) [{sourceLine}]"
                .replace("{constraintClassSimple}", Constraint.class.getSimpleName())
                .replace("{key}", constraint.getKey())
                .replace("{constraint name}", constraint.getName())
                .replace("{expectation}", "" + constraint.expectation)
                .replace("{sourceLine}", constraint.definedAt().toString());

        assertEquals(expected, constraint.toString());
    }

    @Test
    public void compareTo_same_returnZero() {
        assertEquals(0, constraint.compareTo(constraint));
    }

    @Test
    public void compareTo_differentKey_returnCompareToOfKey() {
        AbstractConstraint other = new FakeConstraint("fake-key2", 1234, new SourceLine(new File("fake"), -1));

        assertTrue(constraint.compareTo(other) < 0);
        assertTrue(other.compareTo(constraint) > 0);
    }

    @Test
    public void compareTo_differentName_returnCompareToOfName() {
        AbstractConstraint other = new OtherFakeConstraint("fake-key", 1234, new SourceLine(new File("fake"), -1));

        assertTrue(constraint.compareTo(other) < 0);
        assertTrue(other.compareTo(constraint) > 0);
    }

    @Test
    public void compareTo_differentSourceLine_returnCompareToOfKey() {
        AbstractConstraint other = new FakeConstraint("fake-key", 1234, new SourceLine(new File("fake2"), -1));

        assertTrue(constraint.compareTo(other) < 0);
        assertTrue(other.compareTo(constraint) > 0);
    }

    @Test
    public void violatedBy_always_createConfigConstraintErrorForTheValue() {
        ConfigError error = constraint.violatedBy(null);

        assertTrue(error instanceof ConfigConstraintError);
        assertEquals("fake failed for key fake-key", error.toString());
        // check the value argument of message
        assertNull(error.getMessage().getArguments()[2]);
        assertEquals(123, constraint.violatedBy(123).getMessage().getArguments()[2]);
    }

    static class OtherFakeConstraint extends FakeConstraint {
        public OtherFakeConstraint(@NotNull String key, @NotNull Object expectation, @NotNull SourceLine sourceLine) {
            super(key, expectation, sourceLine);
        }
    }

    static class FakeConstraint extends AbstractConstraint {

        boolean validExpectation = true;

        public FakeConstraint(@NotNull String key, @NotNull Object expectation, @NotNull SourceLine sourceLine) {
            super(key, expectation, sourceLine);
        }

        @Override
        protected boolean isValidExpectation() {
            return validExpectation;
        }

        @Override
        protected ConfigError doValidate(Object value) {
            return () -> new DefaultMessageSourceResolvable(value == null ? "<null>" : value.toString());
        }

        @Override
        public boolean supports(Class type) {
            return false;
        }
    }
}
