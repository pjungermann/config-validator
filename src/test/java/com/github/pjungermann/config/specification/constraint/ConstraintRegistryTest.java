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
import java.util.Arrays;

import static java.util.Collections.emptyList;
import static java.util.Collections.singletonList;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link ConstraintRegistry}.
 *
 * @author Patrick Jungermann
 */
public class ConstraintRegistryTest {

    @Test
    public void constructor_nullArgument_buildWithoutExceptionAndTreatAsEmptyList() {
        new ConstraintRegistry(null);
    }

    @Test
    public void constructor_emptyList_buildInstanceWithoutException() {
        new ConstraintRegistry(emptyList());
    }

    @Test
    public void constructor_nonEmptyList_buildInstanceWithoutException() {
        new ConstraintRegistry(singletonList(new TestConstraintFactory()));
    }

    @Test
    public void byName_knownConstraint_returnCorrectConstraintFactory() throws NoSuchConstraintException {
        ConstraintRegistry registry = new ConstraintRegistry(Arrays.asList(
                new TestConstraintFactory(),
                new AnotherConstraintFactory()
        ));

        assertTrue(registry.byName("test") instanceof TestConstraintFactory);
        assertTrue(registry.byName("another") instanceof AnotherConstraintFactory);
    }

    @Test(expected = NoSuchConstraintException.class)
    public void byName_unknownConstraint_throwNoSuchConstraintException() throws NoSuchConstraintException {
        ConstraintRegistry registry = new ConstraintRegistry(Arrays.asList(
                new TestConstraintFactory(),
                new AnotherConstraintFactory()
        ));

        registry.byName("unknown");
    }

    static class TestConstraintFactory implements ConstraintFactory<Constraint> {

        @NotNull
        @Override
        public Constraint create(@NotNull String key, @Nullable Object expectation, @NotNull SourceLine sourceLine) {
            return new Constraint() {

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
                    return new DefaultMessageSourceResolvable("fake-code");
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
                    return -1;
                }
            };
        }
    }

    static class AnotherConstraintFactory extends TestConstraintFactory {}
}
