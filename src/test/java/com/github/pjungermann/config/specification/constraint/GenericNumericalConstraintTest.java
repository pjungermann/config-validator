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
import com.github.pjungermann.config.specification.constraint.max.MaxConstraint;
import com.google.common.util.concurrent.AtomicDouble;
import org.junit.Test;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link MaxConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public abstract class GenericNumericalConstraintTest<T extends AbstractNumericalConstraint> extends GenericConstraintTest<T> {

    @Override
    public void setUp() {
        super.setUp();

        with(Integer.MAX_VALUE)
                .invalid(new UnsupportedNumberType())
                .buildAndAdd();

        with(Integer.MIN_VALUE)
                .invalid(new UnsupportedNumberType())
                .buildAndAdd();
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
                Integer.class,
                Long.class,
                Float.class,
                Double.class,
                BigDecimal.class,
                AtomicInteger.class,
                AtomicLong.class,
                AtomicDouble.class
        };
    }

    /**
     * @return types not supported for the values.
     */
    @Override
    protected Class[] unsupportedTypes() {
        return new Class[]{
                Object.class,
                String.class,
                Collection.class
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
                new Object()
        };
    }

    @Test
    public void validate_unsupportedNumberType_returnInvalidNumberTypeError() {
        T constraint = createConstraintInstance(Integer.MAX_VALUE);
        Config config = createConfig(constraint, new UnsupportedNumberType());

        ConfigError error = constraint.validate(config);

        assertNotNull(error);
        assertTrue(error instanceof InvalidConfigValueNumberTypeError);
    }

    static class UnsupportedNumberType extends Number {

        /**
         * Returns the value of the specified number as an {@code int},
         * which may involve rounding or truncation.
         *
         * @return the numeric value represented by this object after conversion
         * to type {@code int}.
         */
        @Override
        public int intValue() {
            return 0;
        }

        /**
         * Returns the value of the specified number as a {@code long},
         * which may involve rounding or truncation.
         *
         * @return the numeric value represented by this object after conversion
         * to type {@code long}.
         */
        @Override
        public long longValue() {
            return 0;
        }

        /**
         * Returns the value of the specified number as a {@code float},
         * which may involve rounding.
         *
         * @return the numeric value represented by this object after conversion
         * to type {@code float}.
         */
        @Override
        public float floatValue() {
            return 0;
        }

        /**
         * Returns the value of the specified number as a {@code double},
         * which may involve rounding.
         *
         * @return the numeric value represented by this object after conversion
         * to type {@code double}.
         */
        @Override
        public double doubleValue() {
            return 0;
        }

        @Override
        public String toString() {
            return "not parsable Number#toString()";
        }
    }
}
