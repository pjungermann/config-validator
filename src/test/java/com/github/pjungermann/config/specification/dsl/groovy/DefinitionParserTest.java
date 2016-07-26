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
package com.github.pjungermann.config.specification.dsl.groovy;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.*;
import com.github.pjungermann.config.specification.types.AsTypeConverter;
import com.github.pjungermann.config.specification.types.TypeConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

/**
 * Tests for {@link DefinitionParser}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class DefinitionParserTest {

    private static final SourceLine FAKE_SOURCE_LINE = new SourceLine(new File("fake"), -1);

    TypeConverter typeConverter;
    ConstraintRegistry constraintRegistry;
    ArrayList<ConfigError> errors;
    DefinitionParser parser;

    @Before
    public void setUp() {
        typeConverter = new AsTypeConverter();
        constraintRegistry = new ConstraintRegistry(singletonList(new FakeConstraintFactory()));
        errors = new ArrayList<>(1);
        parser = new DefinitionParser("fake.key", FAKE_SOURCE_LINE, typeConverter, constraintRegistry, errors);
    }

    @Test
    public void apply_asStatement_setUpTypeConversion() {
        Map.Entry<String, Object> entry = Collections.<String, Object>singletonMap("as", Double.class)
                .entrySet()
                .iterator()
                .next();

        Constraint constraint = parser.apply(entry);

        assertNull(constraint);
        assertEquals(1, typeConverter.getKeys().size());
        assertTrue(typeConverter.getKeys().contains("fake.key"));
        assertTrue(errors.isEmpty());
    }

    @Test
    public void apply_fakeStatement_setUpConstraint() {
        Map.Entry<String, Object> entry = Collections.<String, Object>singletonMap("fake", true)
                .entrySet()
                .iterator()
                .next();

        Constraint constraint = parser.apply(entry);

        assertNotNull(constraint);
        assertTrue(constraint instanceof FakeConstraint);
        assertEquals("fake.key", constraint.getKey());
        assertTrue(typeConverter.getKeys().isEmpty());
        assertTrue(errors.isEmpty());
    }

    @Test
    public void apply_nonExistentConstraint_addError() {
        Map.Entry<String, Object> entry = Collections.<String, Object>singletonMap("doesNotExist", true)
                .entrySet()
                .iterator()
                .next();

        Constraint constraint = parser.apply(entry);

        assertNull(constraint);
        assertTrue(typeConverter.getKeys().isEmpty());
        assertEquals(1, errors.size());
        assertTrue(errors.get(0) instanceof NoSuchConstraintError);
        assertEquals("fake.key", ((NoSuchConstraintError) errors.get(0)).key);
        assertEquals("doesNotExist", ((NoSuchConstraintError) errors.get(0)).name);
    }

    static class FakeConstraint extends AbstractConstraint {

        public FakeConstraint(@NotNull String key, @Nullable Object expectation, @NotNull SourceLine sourceLine) {
            super(key, expectation, sourceLine);
        }

        /**
         * @return whether the expectation is a valid one.
         */
        @Override
        protected boolean isValidExpectation() {
            return true;
        }

        /**
         * Validates the value against the {@link #expectation}.
         * Prior to this, the expectation got validated itself
         * and some base checks are already done.
         *
         * @param value The value which has to be validated against the expectation.
         * @return a {@link ConfigError} if the value was invalid, {@code null} otherwise.
         * @see #validate(Config)
         * @see #isValidExpectation()
         * @see #skipNullValues()
         * @see #skipBlankValues()
         */
        @Nullable
        @Override
        protected ConfigError doValidate(Object value) {
            return null;
        }

        /**
         * @param type type of a config value.
         * @return whether the type is supported or not.
         */
        @Override
        public boolean supports(Class type) {
            return true;
        }
    }

    static class FakeConstraintFactory implements ConstraintFactory<FakeConstraint> {

        @NotNull
        @Override
        public FakeConstraint create(@NotNull String key, @Nullable Object expectation, @NotNull SourceLine sourceLine) {
            return new FakeConstraint(key, expectation, sourceLine);
        }
    }
}
