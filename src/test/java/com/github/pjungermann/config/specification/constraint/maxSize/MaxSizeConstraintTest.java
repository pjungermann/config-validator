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
package com.github.pjungermann.config.specification.constraint.maxSize;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.nio.CharBuffer;
import java.util.*;

/**
 * Tests for {@link MaxSizeConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class MaxSizeConstraintTest extends GenericConstraintTest<MaxSizeConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<MaxSizeConstraint> getConstraintClass() {
        return MaxSizeConstraint.class;
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
        with(15)
                .valid("a short text")
                .valid(Arrays.asList(1, 2, 3, 4, 5))
                .valid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15))
                .valid(Collections.emptyList(), Collections.emptySet())
                .invalid("a much longer text than max. accepted")
                .invalid(Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16))
                .buildAndAdd();
    }
}
