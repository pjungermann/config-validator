package com.github.pjungermann.config.specification.constraint;

import org.jetbrains.annotations.NotNull;

/**
 * Thrown in case that a {@link com.github.pjungermann.config.Config} value
 * was of {@link Constraint#supports(Class) unsupported type}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
public class InvalidConfigValueTypeError extends ConfigConstraintError {

    /**
     * @param constraint The {@link Constraint} which got checked.
     * @param value      The invalid value.
     */
    public InvalidConfigValueTypeError(@NotNull final Constraint constraint, @NotNull final Object value) {
        super(constraint, value);
    }

    @NotNull
    @Override
    public String toString() {
        assert value != null;
        return constraint.getName() + " failed for key " + constraint.getKey() + " due to wrong type " + value.getClass();
    }
}
