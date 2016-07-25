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
package com.github.pjungermann.config.specification.constraint.inList;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.util.*;

/**
 * Tests for {@link InListConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class InListConstraintTest extends GenericConstraintTest<InListConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<InListConstraint> getConstraintClass() {
        return InListConstraint.class;
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
                Collection.class,
                List.class,
                Set.class,
                ArrayList.class,
                HashSet.class,
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
                new Object(),
                123,
                Collections.emptyList(),
                Collections.emptySet(),
                "any string"
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(Arrays.asList(1, 2, 3, 4))
                .valid(1, 2, 3, 4)
                .invalid(0, new Object(), "invalid")
                .buildAndAdd();

        with(Arrays.asList("valid", Arrays.asList("a", "b"), new AnyType("foo")))
                .valid("valid", Arrays.asList("a", "b"), new AnyType("foo"))
                .invalid("invalid", 123, new AnyType("bar"))
                .buildAndAdd();

        with(Collections.singleton("set"))
                .invalid(123, "invalid", new Object())
                .buildAndAdd();
    }

    static class AnyType {
        public String field;

        public AnyType(String field) {
            this.field = field;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof AnyType)) return false;
            AnyType anyType = (AnyType) o;
            return Objects.equals(field, anyType.field);
        }

        @Override
        public int hashCode() {
            return Objects.hash(field);
        }
    }
}
