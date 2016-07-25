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
package com.github.pjungermann.config.specification.constraint.creditCard;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;

/**
 * Tests for {@link CreditCardConstraint}.
 *
 * @author Patrick Jungermann
 */
public class CreditCardConstraintTest extends GenericConstraintTest<CreditCardConstraint> {

    private static final String VALID_AMEX = "378282246310005";
    private static final String VALID_DINERS = "30569309025904";
    private static final String VALID_DISCOVER = "6011000990139424";
    private static final String VALID_MASTERCARD = "5105105105105100";
    private static final String VALID_SHORT_VISA = "4222222222222";
    private static final String VALID_VISA = "4417123456789113";

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<CreditCardConstraint> getConstraintClass() {
        return CreditCardConstraint.class;
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
                StringBuilder.class
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
                new Object(),
                1234,
                "invalid type",
                Collections.singletonList("invalid type"),
                Arrays.asList("amex", "invalid type")
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        with(false)
                .valid(VALID_AMEX, VALID_DINERS, VALID_DISCOVER, VALID_MASTERCARD, VALID_SHORT_VISA, VALID_VISA)
                .valid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();

        with(true)
                .valid(VALID_AMEX, VALID_DINERS, VALID_DISCOVER, VALID_MASTERCARD, VALID_SHORT_VISA, VALID_VISA)
                .invalid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();

        with("amex")
                .valid(VALID_AMEX)
                .invalid(VALID_DINERS, VALID_DISCOVER, VALID_MASTERCARD, VALID_SHORT_VISA, VALID_VISA)
                .invalid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();

        with("mastercard")
                .valid(VALID_MASTERCARD)
                .invalid(VALID_AMEX, VALID_DINERS, VALID_DISCOVER, VALID_SHORT_VISA, VALID_VISA)
                .invalid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();

        with("MASTERCARD")
                .valid(VALID_MASTERCARD)
                .invalid(VALID_AMEX, VALID_DINERS, VALID_DISCOVER, VALID_SHORT_VISA, VALID_VISA)
                .invalid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();

        with(Arrays.asList("visa", "mastercard"))
                .valid(VALID_MASTERCARD, VALID_SHORT_VISA, VALID_VISA)
                .invalid(VALID_AMEX, VALID_DINERS, VALID_DISCOVER)
                .invalid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();

        with(Arrays.asList("amex", "visa", "mastercard", "discover", "diners"))
                .valid(VALID_AMEX, VALID_DINERS, VALID_DISCOVER, VALID_MASTERCARD, VALID_SHORT_VISA, VALID_VISA)
                .invalid("1234", "431234567890123", "471234567890123")
                .invalid(123, new Object())
                .buildAndAdd();
    }
}
