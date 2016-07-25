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
package com.github.pjungermann.config.specification.constraint.scale;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.math.BigDecimal;

/**
 * Tests for {@link ScaleConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class ScaleConstraintTest extends GenericConstraintTest<ScaleConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<ScaleConstraint> getConstraintClass() {
        return ScaleConstraint.class;
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
                Number.class,
                Integer.class,
                Float.class,
                Integer.class,
                BigDecimal.class
        };
    }

    /**
     * @return types not supported for the values.
     */
    @Override
    protected Class[] unsupportedTypes() {
        return new Class[]{
                Object.class,
                CharSequence.class,
                String.class
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
                12.34F,
                56.78D,
                -10,
                -1
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(0)
                .valid(11, 10.0F, 2.0D)
                .valid(10, 1000, 10000)
                .invalid(1.1F, 3.4F)
                .buildAndAdd();

        with(3)
                .valid(1234, 1.234F, 2.34D, new BigDecimal("9.8"))
                .valid(10, 1000, 10000)
                .invalid(5.6789D)
                .buildAndAdd();
    }
}
