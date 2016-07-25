package com.github.pjungermann.config.specification.constraint;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown in case that a {@link com.github.pjungermann.config.Config} value
 * was a (custom) {@link Number} type without parsable {@link Number#toString()}
 * implementation (or no override).
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class InvalidConfigValueNumberTypeError extends InvalidConfigValueTypeError {

    /**
     * @param constraint The {@link Constraint} which got checked.
     * @param value      The invalid value.
     */
    public InvalidConfigValueNumberTypeError(@NotNull final Constraint constraint, @NotNull final Number value) {
        super(constraint, value);
    }

    @NotNull
    @Override
    public String toString() {
        return super.toString() + "; HINT: provide a toString implementation parsable by e.g. BigDecimal";
    }
}
