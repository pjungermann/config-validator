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
package com.github.pjungermann.config.specification;

import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.loader.errors.NoSuchFileError;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.reader.SpecificationReader;
import com.github.pjungermann.config.specification.types.TypeConverter;
import com.github.pjungermann.config.utils.FilesResolver;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Collections.synchronizedList;

/**
 * Default implementation of the {@link ConfigSpecificationLoader}.
 *
 * @author Patrick Jungermann
 */
@Component
@Singleton
public class DefaultConfigSpecificationLoader implements ConfigSpecificationLoader {

    private TypeConverter typeConverter;

    private SpecificationReader specificationReader;

    @Inject
    public DefaultConfigSpecificationLoader(@NotNull final TypeConverter typeConverter,
                                            @NotNull final SpecificationReader specificationReader) {
        this.typeConverter = typeConverter;
        this.specificationReader = specificationReader;
    }

    @NotNull
    @Override
    public ConfigSpecification load(boolean recursive, @NotNull final Stream<File> sourceStream) {
        final List<Constraint> constraints = new ArrayList<>();
        final List<Constraint> syncConstraints = synchronizedList(constraints);
        final List<ConfigError> errors = new ArrayList<>();
        final List<ConfigError> syncErrors = synchronizedList(errors);

        sourceStream
                .parallel()
                .filter(file -> {
                    if (file.exists()) {
                        return true;
                    }

                    syncErrors.add(new NoSuchFileError(file));
                    return false;
                })
                .flatMap(new FilesResolver(recursive))
                .map(specificationReader)
                .forEach(partial -> {
                    syncConstraints.addAll(partial.constraints);
                    syncErrors.addAll(partial.errors);
                });

        return new ConfigSpecification(typeConverter, constraints, errors);
    }
}
