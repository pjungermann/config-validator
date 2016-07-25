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
package com.github.pjungermann.config.specification.constraint.range;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;
import groovy.lang.EmptyRange;
import groovy.lang.IntRange;
import groovy.lang.ObjectRange;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;

/**
 * Tests for {@link RangeConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class RangeConstraintTest extends GenericConstraintTest<RangeConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<RangeConstraint> getConstraintClass() {
        return RangeConstraint.class;
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
                Comparable.class,
                Integer.class,
                BigDecimal.class,
                String.class
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
                ArrayList.class,
                HashSet.class
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
                123,
                Arrays.asList(1, 2, 3, 4)
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(new IntRange(true, 1, 3))
                .valid(1, 2, 3)
                .invalid(0, 4)
                .invalid(new Object())
                .invalid("invalid")
                .buildAndAdd();

        with(new ObjectRange("a", "z"))
                .valid("a", "b", "c", "m", "n", "x", "y", "z")
                .invalid("aa", "abc", "za")
                .buildAndAdd();

        with(new EmptyRange(0))
                .invalid(0, 1, 2, 3)
                .invalid("a", "b", "c")
                .buildAndAdd();

        with(new EmptyRange(null))
                .invalid("a", "b")
                .invalid(1, 2, 3)
                .buildAndAdd();
    }
}
