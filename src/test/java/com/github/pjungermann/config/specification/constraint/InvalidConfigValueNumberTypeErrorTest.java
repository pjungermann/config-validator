package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.GenericNumericalConstraintTest.UnsupportedNumberType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.io.File;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link InvalidConfigValueNumberTypeError}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class InvalidConfigValueNumberTypeErrorTest {

    @Test
    public void toString_always_messageWithConstraintNameAndKeyAndValueType() {
        InvalidConfigValueNumberTypeError error = new InvalidConfigValueNumberTypeError(new FakeConstraint(), new UnsupportedNumberType());

        assertEquals(
                "fake failed for key fake-key due to wrong type " +
                        UnsupportedNumberType.class.toString() + "; " +
                        "HINT: provide a toString implementation parsable by e.g. BigDecimal",
                error.toString());
    }

    @Test
    public void getMessage_always_errorMessageByConstraintUsingTheValue() {
        InvalidConfigValueNumberTypeError error = new InvalidConfigValueNumberTypeError(new FakeConstraint(), new UnsupportedNumberType());

        assertEquals("not parsable Number#toString()", error.getMessage().getCodes()[0]);
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
