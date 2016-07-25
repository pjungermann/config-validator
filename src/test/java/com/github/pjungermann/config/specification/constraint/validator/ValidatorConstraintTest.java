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
package com.github.pjungermann.config.specification.constraint.validator;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;
import groovy.lang.Closure;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

/**
 * Tests for {@link ValidatorConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class ValidatorConstraintTest extends GenericConstraintTest<ValidatorConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<ValidatorConstraint> getConstraintClass() {
        return ValidatorConstraint.class;
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
                Object.class,
                String.class,
                Integer.class,
                Long.class
        };
    }

    /**
     * @return types not supported for the values.
     */
    @Override
    protected Class[] unsupportedTypes() {
        return new Class[0];
    }

    /**
     * @return expectation configs which are not valid for this {@link Constraint}.
     */
    @Override
    protected Object[] getInvalidExpectationConfigs() {
        return new Object[]{
                null,
                "invalid",
                1234,
                Collections.emptySet(),
                Collections.emptyList(),
                Collections.emptyMap()
        };
    }

    @Override
    protected Config createConfig(ValidatorConstraint constraint, Object value) {
        Config config = super.createConfig(constraint, value);
        config.put("another", "foo");

        return config;
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        Closure closure = new Closure(this) {
            @Override
            public Object call(Object... args) {
                if (args.length == 3 && args[0] instanceof Config && args[1] instanceof String) {
                    return call((Config) args[0], (String) args[1], args[2]);
                }

                return super.call(args);
            }

            @NotNull
            public Object call(Config config, @SuppressWarnings("UnusedParameters") String key, Object value) {
                return config.get("another").equals(value);
            }
        };

        with(closure)
                .valid("foo")
                .invalid("bar")
                .buildAndAdd();
    }
}
