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
package com.github.pjungermann.config.specification.constraint.matches;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.nio.CharBuffer;
import java.util.regex.Pattern;

/**
 * Tests for {@link MatchesConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class MatchesConstraintTest extends GenericConstraintTest<MatchesConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<MatchesConstraint> getConstraintClass() {
        return MatchesConstraint.class;
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
                StringBuffer.class,
                StringBuilder.class,
                CharBuffer.class
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
                42,
                21L,
                12.34D,
                new Object()
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with("[abc]+")
                .valid("a", "b", "c", "ab", "ac", "bc", "ca", "cb", "ba", "abc", "bca", "cab")
                .invalid("foo", "bar")
                .buildAndAdd();

        with("abc")
                .valid("abc")
                .invalid("a", "b", "c", "ab", "ac", "bc", "ca", "cb", "ba", "bca", "cab")
                .invalid("foo", "bar")
                .buildAndAdd();

        with(Pattern.compile("\\s+\\d{3}\\.[a-z]"))
                .valid("\t123.d", " 456.f", "     762.g")
                .invalid("123.d", "invalid")
                .buildAndAdd();
    }
}
