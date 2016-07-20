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

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.InvalidConstraintConfigError;
import org.apache.commons.validator.routines.CodeValidator;
import org.apache.commons.validator.routines.CreditCardValidator;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;

import java.io.File;
import java.lang.reflect.Field;
import java.nio.CharBuffer;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

/**
 * Tests for {@link CreditCardConstraint}.
 *
 * This class does not include any specific test case
 * for the credit card validation itself and assumes
 * a well-tested and up-to-date state of the used
 * {@link CreditCardValidator} in order to not be needed
 * to reinvent or copy those test cases here also and
 * being forced to their maintenance.
 *
 * @author Patrick Jungermann
 */
public class CreditCardConstraintTest {

    final SourceLine fakeSourceLine = new SourceLine(new File("fake"), -1);

    @Test
    public void supports_always_returnTrueForCharSequenceTypesOnly() {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", false, fakeSourceLine);

        assertTrue(creditCard.supports(CharSequence.class));
        assertTrue(creditCard.supports(String.class));
        assertTrue(creditCard.supports(StringBuilder.class));
        assertTrue(creditCard.supports(StringBuffer.class));
        assertTrue(creditCard.supports(CharBuffer.class));

        assertFalse(creditCard.supports(Object.class));
        assertFalse(creditCard.supports(Integer.class));
        assertFalse(creditCard.supports(Double.class));
    }

    @Test
    public void getValidator_masterCard_correctValidator() throws NoSuchFieldException, IllegalAccessException {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", "masterCard", fakeSourceLine);
        CreditCardValidator validator = creditCard.getValidator();
        assertValidator(validator, CreditCardValidator.MASTERCARD_VALIDATOR);
    }

    @Test
    public void getValidator_masterCardAndVisaAndDiner_correctValidator() throws NoSuchFieldException, IllegalAccessException {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", asList("masterCard", "visa", "diners"), fakeSourceLine);
        CreditCardValidator validator = creditCard.getValidator();
        assertValidator(
                validator,
                CreditCardValidator.DINERS_VALIDATOR,
                CreditCardValidator.MASTERCARD_VALIDATOR,
                CreditCardValidator.VISA_VALIDATOR);
    }

    @Test
    public void getValidator_true_correctValidatorWithAllTypes() throws NoSuchFieldException, IllegalAccessException {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", true, fakeSourceLine);
        CreditCardValidator validator = creditCard.getValidator();
        assertValidator(
                validator,
                CreditCardValidator.AMEX_VALIDATOR,
                CreditCardValidator.DINERS_VALIDATOR,
                CreditCardValidator.DISCOVER_VALIDATOR,
                CreditCardValidator.MASTERCARD_VALIDATOR,
                CreditCardValidator.VISA_VALIDATOR);
    }

    @Test
    public void getValidator_false_correctValidatorWithNoType() throws NoSuchFieldException, IllegalAccessException {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", false, fakeSourceLine);
        CreditCardValidator validator = creditCard.getValidator();
        assertValidator(validator);
    }

    @Test
    public void isValidExpectation_nullValue_invalid() {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", null, fakeSourceLine);
        assertFalse(creditCard.isValidExpectation());
    }

    @Test
    public void isValidExpectation_booleanValue_valid() {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", true, fakeSourceLine);
        assertTrue(creditCard.isValidExpectation());

        creditCard = new CreditCardConstraint("fake-key", false, fakeSourceLine);
        assertTrue(creditCard.isValidExpectation());
    }

    @Test
    public void isValidExpectation_cardTypeString_validForSupportedTypes() {
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", "visa", fakeSourceLine);
        assertTrue(creditCard.isValidExpectation());

        creditCard = new CreditCardConstraint("fake-key", "ViSa", fakeSourceLine);
        assertTrue(creditCard.isValidExpectation());

        creditCard = new CreditCardConstraint("fake-key", "visaa", fakeSourceLine);
        assertFalse(creditCard.isValidExpectation());
    }

    @Test
    public void isValidExpectation_cardTypeList_validForSupportedTypes() {
        CreditCardConstraint creditCard =
                new CreditCardConstraint("fake-key", asList("visa", "amex"), fakeSourceLine);
        assertTrue(creditCard.isValidExpectation());

        creditCard = new CreditCardConstraint("fake-key", asList("ViSa", "aMex"), fakeSourceLine);
        assertTrue(creditCard.isValidExpectation());

        creditCard = new CreditCardConstraint("fake-key", asList("visa", "amexx"), fakeSourceLine);
        assertFalse(creditCard.isValidExpectation());
    }

    @Test
    public void validate_nullExpectation_error() {
        Config config = new Config();
        config.put("fake-key", null);
        CreditCardConstraint creditCard = new CreditCardConstraint("fake-key", null, fakeSourceLine);

        ConfigError error = creditCard.validate(config);

        assertNotNull(error);
        assertTrue(error instanceof InvalidConstraintConfigError);
    }

    @Test
    public void validate_nullValue_getsSkippedAndAccepted() {
        Config config = new Config();
        config.put("fake-key", null);
        CreditCardConstraint creditCard =
                new WithStubValidatorCreditCardConstraint("fake-key", true, fakeSourceLine)
                        .withAcceptAll(false);

        assertNull(creditCard.validate(config));
    }

    @Test
    public void validate_blankValue_acceptedAsItGotSkipped() {
        Config config = new Config();
        config.put("fake-key", "");
        CreditCardConstraint creditCard =
                new WithStubValidatorCreditCardConstraint("fake-key", true, fakeSourceLine)
                        .withAcceptAll(false);

        assertNull(creditCard.validate(config));
    }

    @Test
    public void validate_otherType_notAcceptInvalidType() {
        Config config = new Config();
        config.put("fake-key", new Object());
        CreditCardConstraint creditCard =
                new WithStubValidatorCreditCardConstraint("fake-key", true, fakeSourceLine)
                        .withAcceptAll(true);

        ConfigError error = creditCard.validate(config);
        assertNotNull(error);
        assertEquals(
                "withStubValidatorCreditCard failed for key fake-key due to wrong type class java.lang.Object",
                error.toString());
    }

    @Test
    public void validate_nonEmptyString_returnValidatorValue() {
        Config config = new Config();
        config.put("fake-key", "fake-credit-card");
        CreditCardConstraint creditCard1 =
                new WithStubValidatorCreditCardConstraint("fake-key", true, fakeSourceLine)
                        .withAcceptAll(true);
        CreditCardConstraint creditCard2 =
                new WithStubValidatorCreditCardConstraint("fake-key", true, fakeSourceLine)
                        .withAcceptAll(false);

        assertNull(creditCard1.validate(config));
        ConfigError error = creditCard2.validate(config);
        assertNotNull(error);
        assertEquals("withStubValidatorCreditCard failed for key fake-key", error.toString());
    }

    private static void assertValidator(CreditCardValidator validator, CodeValidator... expectedCodeValidators) throws NoSuchFieldException, IllegalAccessException {
        List cardTypes = getCardTypes(validator);

        assertEquals(expectedCodeValidators.length, cardTypes.size());
        for (CodeValidator expectedCodeValidator: expectedCodeValidators) {
            assertTrue(cardTypes.contains(expectedCodeValidator));
        }
    }

    private static List getCardTypes(CreditCardValidator validator) throws NoSuchFieldException, IllegalAccessException {
        Field cardTypesField = CreditCardValidator.class.getDeclaredField("cardTypes");
        cardTypesField.setAccessible(true);

        return (List) cardTypesField.get(validator);
    }

    /**
     * This validator is not doing any credit card validation
     * as it is assumed that the validator itself is well-tested
     * and test cases are kept up-to-date also at future library upgrades.
     *
     * Therefore, we use the {@link CreditCardConstraintTest.FakeValidator}
     * in order to define the result beforehand. The validator is only used
     * at some paths, and the goal is test esp. the other paths as well as
     * the usage of the validator rather than the specification and validation
     * of it itself.
     */
    static class WithStubValidatorCreditCardConstraint extends CreditCardConstraint {

        boolean acceptAll;

        /**
         * @param key         The key for which this {@link Constraint} gets defined for.
         * @param expectation The expectation which needs to be fulfilled by the config key's value.
         * @param sourceLine  The {@link SourceLine} at which this expectation got expressed at.
         */
        public WithStubValidatorCreditCardConstraint(@NotNull String key, @Nullable Object expectation, @NotNull SourceLine sourceLine) {
            super(key, expectation, sourceLine);
        }

        public WithStubValidatorCreditCardConstraint withAcceptAll(boolean acceptAll) {
            this.acceptAll = acceptAll;

            return this;
        }

        @Override
        protected CreditCardValidator getValidator() {
            return new FakeValidator(acceptAll);
        }
    }

    /**
     * This validator is not doing any credit card validation
     * as it is assumed that the validator itself is well-tested
     * and test cases are kept up-to-date also at future library upgrades.
     *
     * This extension allows to define the validation result beforehand.
     */
    static class FakeValidator extends CreditCardValidator {

        boolean accept;

        public FakeValidator(boolean accept) {
            this.accept = accept;
        }

        @Override
        public boolean isValid(String card) {
            return accept;
        }
    }
}
