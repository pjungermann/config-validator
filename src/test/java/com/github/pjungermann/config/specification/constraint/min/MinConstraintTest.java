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
package com.github.pjungermann.config.specification.constraint.min;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericNumericalConstraintTest;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * Tests for {@link MinConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class MinConstraintTest extends GenericNumericalConstraintTest<MinConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<MinConstraint> getConstraintClass() {
        return MinConstraint.class;
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(59)
                .invalid(Integer.MIN_VALUE, -100, 0, 13, 41, 58)
                .invalid(13.31D)
                .invalid(41.14F)
                .invalid(new AtomicDouble(9.34D))
                .valid(59, 60, Integer.MAX_VALUE)
                .valid(new AtomicDouble(59.34D))
                .buildAndAdd();

        with(77.77D)
                .invalid(13.31D, -100D, 0D, 77.769D)
                .invalid(Integer.MIN_VALUE, -100, 0, 13, 41, 59)
                .invalid(41.14F)
                .valid(77.77D, 77.771D, Integer.MAX_VALUE, Double.MAX_VALUE)
                .buildAndAdd();
    }
}
