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
package com.github.pjungermann.config.specification.constraint.email;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonMap;

/**
 * Tests for {@link EmailConstraint}.
 *
 * Tests cases are limited to very simple cases and the additional config
 * for the extension of {@link org.apache.commons.validator.routines.EmailValidator}
 * within {@link EmailConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class EmailConstraintTest extends GenericConstraintTest<EmailConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<EmailConstraint> getConstraintClass() {
        return EmailConstraint.class;
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
                "invalid",
                1234,
                new Object()

        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(false)
                .valid("anything")
                .buildAndAdd();

        with(true)
                .valid("test@example.org")
                .invalid("test@localhost")
                .invalid("test@example.internal")
                .invalid("test@example.local")
                .invalid("test@example.lan")
                .invalid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        with(Collections.emptyMap())
                .valid("test@example.org")
                .invalid("test@localhost")
                .invalid("test@example.internal")
                .invalid("test@example.local")
                .invalid("test@example.lan")
                .invalid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        with(singletonMap("local", false))
                .valid("test@example.org")
                .invalid("test@localhost")
                .invalid("test@example.internal")
                .invalid("test@example.local")
                .invalid("test@example.lan")
                .invalid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        with(singletonMap("local", true))
                .valid("test@example.org")
                .valid("test@localhost")
                .invalid("test@example.internal")
                .invalid("test@example.local")
                .invalid("test@example.lan")
                .invalid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        with(singletonMap("customTLDs", emptyList()))
                .valid("test@example.org")
                .invalid("test@localhost")
                .invalid("test@example.internal")
                .invalid("test@example.local")
                .invalid("test@example.lan")
                .invalid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        with(singletonMap("customTLDs", Arrays.asList("internal", "local", "lan")))
                .valid("test@example.org")
                .invalid("test@localhost")
                .valid("test@example.internal")
                .valid("test@example.local")
                .valid("test@example.lan")
                .valid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        HashMap<String, Object> config1 = new HashMap<>();
        config1.put("local", false);
        config1.put("customTLDs", emptyList());
        with(config1)
                .valid("test@example.org")
                .invalid("test@localhost")
                .invalid("test@example.internal")
                .invalid("test@example.local")
                .invalid("test@example.lan")
                .invalid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();

        HashMap<String, Object> config2 = new HashMap<>();
        config2.put("local", true);
        config2.put("customTLDs", Arrays.asList("internal", "local", "lan"));
        with(config2)
                .valid("test@example.org")
                .valid("test@localhost")
                .valid("test@example.internal")
                .valid("test@example.local")
                .valid("test@example.lan")
                .valid("test@my.custom.lan")
                .invalid("test@-localhost")
                .invalid("test@-example.internal")
                .invalid("test@-example.local")
                .invalid("test@-example.lan")
                .invalid("test @-example.org")
                .buildAndAdd();
    }
}
