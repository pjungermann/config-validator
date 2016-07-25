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
package com.github.pjungermann.config.specification.constraint.size;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;
import groovy.lang.IntRange;

import java.nio.CharBuffer;
import java.util.*;

/**
 * Tests for {@link SizeConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class SizeConstraintTest extends GenericConstraintTest<SizeConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<SizeConstraint> getConstraintClass() {
        return SizeConstraint.class;
    }

    /**
     * @return whether the {@link Constraint} is supposed to skip / ignore null values
     */
    @Override
    protected boolean skipsNullValues() {
        return true;
    }

    /**
     * @return whether the {@link Constraint} is supposed to skip / ignore blank values
     */
    @Override
    protected boolean skipsBlankValues() {
        return true;
    }

    /**
     * @return types supported for the values.
     */
    @Override
    protected Class[] supportedTypes() {
        return new Class[]{
                CharSequence.class,
                String.class,
                CharBuffer.class,
                Collection.class,
                Set.class,
                List.class,
                HashSet.class,
                ArrayList.class
        };
    }

    /**
     * @return types not supported for the values.
     */
    @Override
    protected Class[] unsupportedTypes() {
        return new Class[]{
                Object.class,
                Number.class,
                Integer.class,
                Long.class
        };
    }

    /**
     * @return expectation configs which are not valid for this {@link Constraint}.
     */
    @Override
    protected Object[] getInvalidExpectationConfigs() {
        return new Object[]{
                null,
                new Object(),
                "invalid"
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(10)
                .valid("1234567890")
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .invalid("123456789")
                .invalid("1234567890-")
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                .buildAndAdd();

        // exclusive "to" range (from..<to)
        with(new IntRange(false, 7, 10))
                .valid("1234567")
                .valid("12345678")
                .valid("123456789")
                .invalid("123456")
                .invalid("1234567890")
                .invalid("1234567890-")
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                .buildAndAdd();

        // inclusive range (from..to)
        with(new IntRange(true, 7, 10))
                .valid("1234567")
                .valid("12345678")
                .valid("123456789")
                .valid("1234567890")
                .invalid("123456")
                .invalid("1234567890-")
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                .buildAndAdd();

        // inclusive range (from..to)
        // (even though documentation of IntRange says non-inclusive..
        with(new IntRange(7, 10))
                .valid("1234567")
                .valid("12345678")
                .valid("123456789")
                .valid("1234567890")
                .invalid("123456")
                .invalid("1234567890-")
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6))
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11))
                .buildAndAdd();
    }
}
