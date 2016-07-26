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
package com.github.pjungermann.config.specification.dsl.groovy;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.ConfigSpecification;
import com.github.pjungermann.config.specification.constraint.*;
import com.github.pjungermann.config.specification.types.TypeConversionConfigError;
import com.github.pjungermann.config.specification.types.TypeConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Map.Entry;
import java.util.function.Function;

/**
 * Parses a definition with a {@link ConfigSpecification} file.
 * Returns a {@link Constraint} instance or {@code null} in case
 * of collected errors or {@link TypeConverter type conversion statements}.
 *
 * @author Patrick Jungermann
 */
public class DefinitionParser implements Function<Entry<String, Object>, Constraint> {

    private final String key;
    private final SourceLine sourceLine;

    private final TypeConverter typeConverter;
    private final ConstraintRegistry constraintRegistry;
    private final Collection<ConfigError> errors;

    public DefinitionParser(@NotNull final String key,
                            @NotNull final SourceLine sourceLine,
                            @NotNull final TypeConverter typeConverter,
                            @NotNull final ConstraintRegistry constraintRegistry,
                            @NotNull final Collection<ConfigError> errors) {
        this.key = key;
        this.sourceLine = sourceLine;

        this.typeConverter = typeConverter;
        this.constraintRegistry = constraintRegistry;
        this.errors = errors;
    }

    @Nullable
    @Override
    public Constraint apply(@NotNull final Entry<String, Object> entry) {
        if (typeConverter.isConversionCommand(entry.getKey())) {
            if (typeConverter.isValidConversionConfig(entry.getValue())) {
                typeConverter.register(key, (Class) entry.getValue());

            } else {
                errors.add(new TypeConversionConfigError(key, entry.getValue(), sourceLine));
            }

            return null;
        }

        final ConstraintFactory constraintFactory;
        try {
            constraintFactory = constraintRegistry.byName(entry.getKey());

        } catch (NoSuchConstraintException e) {
            errors.add(new NoSuchConstraintError(entry.getKey(), key, sourceLine));
            return null;
        }

        return constraintFactory.create(
                key,
                entry.getValue(),
                sourceLine);
    }
}
