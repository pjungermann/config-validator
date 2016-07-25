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
package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.reference.SourceLine;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ErrorCollector;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.*;

/**
 * Generic implementation for {@link AbstractConstraint constraints}.
 *
 * @author patrick.jungermann
 * @since 2016-07-21
 */
public abstract class GenericConstraintTest<T extends AbstractConstraint> {

    public static final SourceLine FAKE_SOURCE_LINE = new SourceLine(new File("fake"), -1);

    protected List<TestExpectation> testExpectations = new ArrayList<>();

    @Rule
    public ErrorCollector collector = new ErrorCollector();

    /**
     * @return the {@link Constraint} under test.
     */
    protected abstract Class<T> getConstraintClass();

    /**
     * @return whether the {@link Constraint} is supposed to skip / ignore null values
     */
    protected abstract boolean skipsNullValues();

    /**
     * @return whether the {@link Constraint} is supposed to skip / ignore blank values
     */
    protected abstract boolean skipsBlankValues();

    /**
     * @return types supported for the values.
     */
    protected abstract Class[] supportedTypes();

    /**
     * @return types not supported for the values.
     */
    protected abstract Class[] unsupportedTypes();

    /**
     * @return expectation configs which are not valid for this {@link Constraint}.
     */
    protected abstract Object[] getInvalidExpectationConfigs();

    /**
     * Sets up the test data
     */
    protected abstract void testDataSetUp();

    @Before
    public void setUp() {
        testDataSetUp();
        assertFalse("no expectation provided", testExpectations.isEmpty());

        Object config = testExpectations.get(0).config;
        if (skipsNullValues()) {
            with(config).valid((Object) null).buildAndAdd();
        }
        if (skipsBlankValues()) {
            with(config).valid("").buildAndAdd();
        }
    }

    @Test
    public void supports_supportedTypes_returnTrue() {
        for (Class type: supportedTypes()) {
            collector.checkSucceeds(() -> {
                assertTrue(
                        type.toString() + " expected to be supported",
                        createConstraintInstance((Object) null).supports(type)
                );

                return null;
            });
        }
    }

    @Test
    public void supports_unsupportedTypes_returnFalse() {
        for (Class type: unsupportedTypes()) {
            collector.checkSucceeds(() -> {
                assertFalse(
                        type.toString() + " expected not to be supported",
                        createConstraintInstance((Object) null).supports(type)
                );

                return null;
            });
        }
    }

    @Test
    public void isValidExpectation_invalidValues_returnFalse() {
        for (Object config: getInvalidExpectationConfigs()) {
            collector.checkSucceeds(() -> {
                assertFalse(
                        "expectation config " + config + " is expected to be invalid",
                        createConstraintInstance(config).isValidExpectation()
                );

                return null;
            });
        }
    }

    /**
     * Tests the {@link Constraint#validate(Config)} method
     * with all the test data sets.
     */
    @Test
    public void validate() {
        assertFalse(
                "no test data was provided. Use the \"with(expectationConfig)\" method to build test data sets.",
                testExpectations.isEmpty()
        );

        testExpectations.forEach(this::validate);
    }

    /**
     * Validates the test expectation.
     *
     * @param testExpectation    test data.
     */
    public void validate(TestExpectation testExpectation) {
        collector.checkSucceeds(() -> {
            T constraint = createConstraintInstance(testExpectation);

            assertTrue(
                    "expectation config needs to be valid; used config: " + constraint.expectation,
                    constraint.isValidExpectation()
            );

            for (Object valid: testExpectation.valid) {
                collector.checkSucceeds(() -> {
                    assertNull(
                            "value \"" + valid + "\" was expected to be valid for " + constraint,
                            constraint.validate(createConfig(constraint, valid))
                    );

                    return null;
                });
            }

            for (Object invalid: testExpectation.invalid) {
                collector.checkSucceeds(() -> {
                    assertNotNull(
                            "value \"" + invalid + "\" was expected to be invalid for " + constraint,
                            constraint.validate(createConfig(constraint, invalid))
                    );

                    return null;
                });
            }

            return null;
        });
    }

    /**
     * @param constraint    {@link Constraint} from which it will use the used key.
     * @param value         The config value.
     * @return config with entry with constraint key as key and the value as value.
     */
    protected Config createConfig(T constraint, Object value) {
        Config config = new Config();
        config.put(constraint.getKey(), value);

        return config;
    }

    /**
     * @param testExpectation    the test data, containing the expectation config.
     * @return the {@link Constraint} instance.
     */
    protected T createConstraintInstance(TestExpectation testExpectation) {
        return createConstraintInstance(testExpectation.config);
    }

    /**
     * @param config    expectation config.
     * @return the {@link Constraint} instance.
     */
    protected T createConstraintInstance(Object config) {
        T constraint;
        try {
            constraint = getConstraintClass()
                    .getConstructor(String.class, Object.class, SourceLine.class)
                    .newInstance(UUID.randomUUID().toString(), config, FAKE_SOURCE_LINE);

        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new AssertionError(e);
        }

        return constraint;
    }

    /**
     * Test data set.
     */
    public static class TestExpectation {
        public final Object config;
        public final Object[] valid;
        public final Object[] invalid;

        public TestExpectation(Object config, Object[] valid, Object[] invalid) {
            this.config = config;
            this.valid = valid.clone();
            this.invalid = invalid.clone();
        }
    }

    /**
     * Starts to create an expectation data set with the provided
     * expectation config.
     *
     * Convenience method which creates the builder instance
     * and calls {@link ExpectationBuilder#withConfig(Object)}.
     *
     * @param config    the expectation config.
     * @return builder for creating it.
     */
    protected ExpectationBuilder with(Object config) {
        return new ExpectationBuilder().withConfig(config);
    }

    public class ExpectationBuilder {

        private Object config;
        private final List<Object> validValues = new ArrayList<>();
        private final List<Object> invalidValues = new ArrayList<>();

        /**
         * Will use the provided expectation config as new
         * value for.
         *
         * @param config    the expectation config.
         * @return the builder
         */
        public ExpectationBuilder withConfig(Object config) {
            this.config = config;
            return this;
        }

        /**
         * Adds one or more valid values.
         *
         * @param values    the valid values.
         * @return the builder
         */
        public ExpectationBuilder valid(Object... values) {
            Collections.addAll(validValues, values);
            return this;
        }

        /**
         * Adds one or more invalid values.
         *
         * @param values    the invalid values.
         * @return the builder
         */
        public ExpectationBuilder invalid(Object... values) {
            Collections.addAll(invalidValues, values);
            return this;
        }

        /**
         * Builds and add the expectation.
         */
        public void buildAndAdd() {
            TestExpectation testExpectation = new TestExpectation(config, validValues.toArray(), invalidValues.toArray());
            GenericConstraintTest.this.testExpectations.add(testExpectation);
        }
    }
}
