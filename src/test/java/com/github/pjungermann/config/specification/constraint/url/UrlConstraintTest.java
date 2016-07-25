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
package com.github.pjungermann.config.specification.constraint.url;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.net.URL;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashMap;

import static java.util.Collections.singletonMap;

/**
 * Tests for {@link UrlConstraint}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class UrlConstraintTest extends GenericConstraintTest<UrlConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<UrlConstraint> getConstraintClass() {
        return UrlConstraint.class;
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
                CharBuffer.class,
                StringBuffer.class,
                StringBuilder.class,
                URL.class
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
                Long.class,
                Number.class
        };
    }

    /**
     * @return expectation configs which are not valid for this {@link Constraint}.
     */
    @Override
    protected Object[] getInvalidExpectationConfigs() {
        return new Object[]{
                null,
                singletonMap("local", "invalid"),
                singletonMap("customTLDs", "invalid"),
                singletonMap("schemes", "invalid"),
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
                .valid("http://example.org/foo?bar=123#test=abc")
                .invalid("http://localhost/test")
                .invalid("http://example.internal/foo?bar=123#test=abc")
                .invalid("http://example.local/foo?bar=123#test=abc")
                .invalid("http://example.lan/foo?bar=123#test=abc")
                .invalid("invalid://example.org")
                .buildAndAdd();

        with(singletonMap("local", true))
                .valid("http://example.org/foo?bar=123#test=abc")
                .valid("http://localhost/test")
                .invalid("http://example.internal/foo?bar=123#test=abc")
                .invalid("http://example.local/foo?bar=123#test=abc")
                .invalid("http://example.lan/foo?bar=123#test=abc")
                .invalid("invalid://example.org")
                .buildAndAdd();

        with(singletonMap("customTLDs", Arrays.asList("internal", "local", "lan")))
                .valid("http://example.org/foo?bar=123#test=abc")
                .valid("http://example.internal/foo?bar=123#test=abc")
                .valid("http://example.local/foo?bar=123#test=abc")
                .valid("http://example.lan/foo?bar=123#test=abc")
                .invalid("http://localhost/test")
                .invalid("invalid://example.org")
                .buildAndAdd();

        with(singletonMap("schemes", Arrays.asList("fakeScheme", "test")))
                .invalid("http://example.org/foo?bar=123#test=abc")
                .valid("fakeScheme://example.org/foo?bar=123#test=abc")
                .valid("test://example.org/foo?bar=123#test=abc")
                .valid("fakeScheme://example.org")
                .valid("test://example.org")
                .invalid("http://example.internal/foo?bar=123#test=abc")
                .invalid("http://example.local/foo?bar=123#test=abc")
                .invalid("http://example.lan/foo?bar=123#test=abc")
                .invalid("http://localhost/test")
                .invalid("invalid://example.org")
                .buildAndAdd();

        HashMap<String, Object> config = new HashMap<>();
        config.put("local", true);
        config.put("customTLDs", Arrays.asList("internal", "local", "lan"));
        config.put("schemes", Arrays.asList("fakeScheme", "test", "ftp"));
        with(config)
                .invalid("http://example.org/foo?bar=123#test=abc")
                .valid("ftp://example.org/foo?bar=123#test=abc")
                .valid("fakeScheme://example.org")
                .valid("test://example.org")
                .invalid("http://example.internal/foo?bar=123#test=abc")
                .valid("ftp://example.internal/foo?bar=123#test=abc")
                .invalid("http://example.local/foo?bar=123#test=abc")
                .valid("ftp://example.local/foo?bar=123#test=abc")
                .invalid("http://example.lan/foo?bar=123#test=abc")
                .valid("ftp://example.lan/foo?bar=123#test=abc")
                .invalid("http://localhost/test")
                .valid("test://localhost/test")
                .valid("fakeScheme://foo.bar.lan/resource/path")
                .invalid("invalid://example.org")
                .buildAndAdd();
    }
}
