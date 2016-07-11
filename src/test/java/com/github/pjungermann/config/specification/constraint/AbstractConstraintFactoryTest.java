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

import com.github.pjungermann.config.reference.SourceLine;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

/**
 * Simple abstract test for {@link ConstraintFactory}
 * implementations for a {@link Constraint} extending
 * {@link AbstractConstraint}.
 *
 * @author Patrick Jungermann
 */
public abstract class AbstractConstraintFactoryTest<F extends ConstraintFactory> {

    F factory;

    @Before
    public void setUp() {
        factory = createFactory();
    }

    protected abstract F createFactory();

    @Test
    public void create_always_createsConstraintWithSameArgumentsAsProvides() throws NoSuchFieldException, IllegalAccessException {
        String key = UUID.randomUUID().toString();
        Object expectation = new FakeExpectation();
        SourceLine sourceLine = new SourceLine(new File(UUID.randomUUID().toString()), (int) System.currentTimeMillis());

        Constraint constraint = factory.create(key, expectation, sourceLine);

        assertTrue(
                "only instances of " + AbstractConstraint.class.getName() + " can be tested with this abstract test implementation",
                constraint instanceof AbstractConstraint);

        assertSame(key, getFieldValue((AbstractConstraint) constraint, "key", String.class));
        assertSame(expectation, getFieldValue((AbstractConstraint) constraint, "expectation", Object.class));
        assertSame(sourceLine, getFieldValue((AbstractConstraint) constraint, "sourceLine", SourceLine.class));
    }

    @SuppressWarnings("unchecked")
    private static <V> V getFieldValue(AbstractConstraint constraint, String name, Class<V> valueType) throws NoSuchFieldException, IllegalAccessException {
        Field field = AbstractConstraint.class.getDeclaredField(name);

        return (V) field.get(constraint);
    }

    static class FakeExpectation {}
}
