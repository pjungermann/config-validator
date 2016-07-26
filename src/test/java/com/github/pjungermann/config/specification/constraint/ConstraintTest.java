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
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link Constraint}.
 *
 * @author Patrick Jungermann
 */
public class ConstraintTest {

    @Test
    public void getName_classFollowingTheConvention_returnPropertyNameVersionOfClassWithoutSuffix() {
        assertEquals("fakeTest", new FakeTestConstraint().getName());
    }

    @Test
    public void getName_classNotFollowingTheConvention_returnPropertyNameVersionOfClass() {
        assertEquals("notFollowing", new NotFollowing().getName());
    }

    static class FakeTestConstraint implements Constraint {

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
            return new MessageSourceResolvable() {
                @Override
                public String[] getCodes() {
                    return new String[0];
                }

                @Override
                public Object[] getArguments() {
                    return new Object[0];
                }

                @Override
                public String getDefaultMessage() {
                    return null;
                }
            };
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

    static class NotFollowing implements Constraint {

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
            return new MessageSourceResolvable() {
                @Override
                public String[] getCodes() {
                    return new String[0];
                }

                @Override
                public Object[] getArguments() {
                    return new Object[0];
                }

                @Override
                public String getDefaultMessage() {
                    return null;
                }
            };
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
