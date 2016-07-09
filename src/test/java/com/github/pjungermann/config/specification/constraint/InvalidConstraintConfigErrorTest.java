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

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.io.File;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link InvalidConstraintConfigError}.
 *
 * @author Patrick Jungermann
 */
public class InvalidConstraintConfigErrorTest {

    @Test
    public void toString_always_messageContainingConstraintNameAndConfigKeyAndUsedConfig() {
        InvalidConstraintConfigError error = new InvalidConstraintConfigError(new FakeConstraint(), null);

        assertEquals("Illegal config for constraint fake for config key fake-key: null", error.toString());
    }

    @Test
    public void getMessage_always_resolvableWithCorrectCodeAndArguments() {
        InvalidConstraintConfigError error = new InvalidConstraintConfigError(new FakeConstraint(), null);

        MessageSourceResolvable resolvable = error.getMessage();
        String[] codes = resolvable.getCodes();
        assertEquals(1, codes.length);
        assertEquals("errors.constraints.config.invalid", codes[0]);
        assertArrayEquals(new Object[]{
                "fake-key", "fake", null, new SourceLine(new File("fake"), -1)
        }, resolvable.getArguments());
        assertEquals(codes[0], resolvable.getDefaultMessage());
    }

    static class FakeConstraint implements Constraint {

        @NotNull
        @Override
        public SourceLine definedAt() {
            return new SourceLine(new File("fake"), -1);
        }

        @NotNull
        @Override
        public String getKey() {
            return "fake-key";
        }

        @Override
        public boolean supports(Class type) {
            return false;
        }

        @Nullable
        @Override
        public ConfigError validate(@NotNull Config config) {
            return null;
        }

        @NotNull
        @Override
        public MessageSourceResolvable getMessage(@Nullable Object value) {
            return new DefaultMessageSourceResolvable(value == null ? "<null>" : value.toString());
        }

        @Override
        public int compareTo(@NotNull Constraint o) {
            return this.equals(o) ? 0 : -1;
        }

        @Override
        public boolean equals(Object o) {
            return this == o;
        }

        @Override
        public int hashCode() {
            return 1;
        }
    }
}
