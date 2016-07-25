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
package com.github.pjungermann.config.specification.constraint.blank;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.nio.CharBuffer;

/**
 * Tests for {@link BlankConstraint}.
 *
 * @author Patrick Jungermann
 */
public class BlankConstraintTest extends GenericConstraintTest<BlankConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<BlankConstraint> getConstraintClass() {
        return BlankConstraint.class;
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
        return false;
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
                StringBuilder.class,
                StringBuffer.class
        };
    }

    /**
     * @return types not supported for the values.
     */
    @Override
    protected Class[] unsupportedTypes() {
        return new Class[]{
                Object.class,
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
                "invalid",
                1234
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(true)
                .valid("", "non-empty string")
                .invalid(123, new Object())
                .buildAndAdd();

        with(false)
                .valid("non-empty string")
                .invalid("", 123, new Object())
                .buildAndAdd();
    }
}
