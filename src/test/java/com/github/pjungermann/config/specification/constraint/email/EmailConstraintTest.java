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

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.creditCard.CreditCardConstraint;
import org.junit.Test;

import java.io.File;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.HashMap;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;

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
public class EmailConstraintTest {

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
    public void validate_nullValue_acceptedAsItGotSkipped() {
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, true, fakeSourceLine);

        assertNull(email.validate(config(key, null)));
    }

    @Test
    public void validate_blankValue_acceptedAsItGotSkipped() {
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, true, fakeSourceLine);

        assertNull(email.validate(config(key, "")));
    }

    @Test
    public void validate_validEmail_noError() {
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, true, fakeSourceLine);

        assertNull(email.validate(config(key, "test@example.org")));

    }

    @Test
    public void validate_invalidEmail_error() {
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, true, fakeSourceLine);

        assertNotNull(email.validate(config(key, "test @-example.org")));
    }

    @Test
    public void validate_localEmailAndNotAllowed_error() {
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, true, fakeSourceLine);

        assertNotNull(email.validate(config(key, "test@localhost")));
    }

    @Test
    public void validate_localEmailAndAllowed_acceptIt() {
        HashMap<String, Object> config = new HashMap<>();
        config.put(EmailConstraint.LOCAL_KEY, true);
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, config, fakeSourceLine);

        assertNull(email.validate(config(key, "test@localhost")));
    }

    @Test
    public void validate_customTDLButNotAllowed_error() {
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, true, fakeSourceLine);

        assertNotNull(email.validate(config(key, "foo@localhost")));
        assertNotNull(email.validate(config(key, "foo@example.local")));
        assertNotNull(email.validate(config(key, "foo@exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-localhost")));
        assertNotNull(email.validate(config(key, "foo@-example.local")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
    }

    @Test
    public void validate_customTLDAndAllowed_acceptIt() {
        HashMap<String, Object> constraintConfig = new HashMap<>();
        constraintConfig.put(EmailConstraint.CUSTOM_TLDS_KEY, singletonList("local"));
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, constraintConfig, fakeSourceLine);

        assertNull(email.validate(config(key, "foo@example.local")));

        assertNotNull(email.validate(config(key, "foo@localhost")));
        assertNotNull(email.validate(config(key, "foo@exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-localhost")));
        assertNotNull(email.validate(config(key, "foo@-example.local")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
    }

    @Test
    public void validate_customTLDsAndMultipleAllowed_acceptIt() {
        HashMap<String, Object> constraintConfig = new HashMap<>();
        constraintConfig.put(EmailConstraint.CUSTOM_TLDS_KEY, Arrays.asList("local", "lan"));
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, constraintConfig, fakeSourceLine);

        assertNotNull(email.validate(config(key, "foo@localhost")));
        assertNull(email.validate(config(key, "foo@example.local")));
        assertNull(email.validate(config(key, "foo@exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-localhost")));
        assertNotNull(email.validate(config(key, "foo@-example.local")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
    }

    @Test
    public void validate_customTLDAndLocalAllowed_acceptLocalEmailAndCustomTLDsWithinIt() {
        HashMap<String, Object> constraintConfig = new HashMap<>();
        constraintConfig.put(EmailConstraint.LOCAL_KEY, true);
        constraintConfig.put(EmailConstraint.CUSTOM_TLDS_KEY, Arrays.asList("local", "lan"));
        String key = "fake-key";
        EmailConstraint email = new EmailConstraint(key, constraintConfig, fakeSourceLine);

        assertNull(email.validate(config(key, "foo@localhost")));
        assertNull(email.validate(config(key, "foo@example.local")));
        assertNull(email.validate(config(key, "foo@exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-localhost")));
        assertNotNull(email.validate(config(key, "foo@-example.local")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
        assertNotNull(email.validate(config(key, "foo@-exmaple.lan")));
    }

    private static Config config(final String key, final Object value) {
        final Config config = new Config();
        config.put(key, value);

        return config;
    }
}
