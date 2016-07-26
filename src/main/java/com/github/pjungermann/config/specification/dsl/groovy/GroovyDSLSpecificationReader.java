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
import com.github.pjungermann.config.specification.FailedToLoadSpecificationError;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.reader.SpecificationPartial;
import com.github.pjungermann.config.specification.reader.SpecificationReader;
import groovy.lang.Binding;
import groovy.lang.GroovyShell;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.jetbrains.annotations.NotNull;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

import static java.util.Collections.emptyList;

/**
 * {@link SpecificationReader} using the {@link SpecificationDSLInterpreter}
 * to read specification files based on the Groovy baked DSL.
 *
 * @author Patrick Jungermann
 */
@Component
@Singleton
public class GroovyDSLSpecificationReader implements SpecificationReader {

    private ApplicationContext applicationContext;

    @Inject
    public GroovyDSLSpecificationReader(@NotNull final ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public SpecificationPartial apply(@NotNull final File file) {
        final Binding binding = new Binding();
        binding.setProperty("context", applicationContext);
        binding.setProperty("specification.source", file);

        final CompilerConfiguration compilerConfiguration = new CompilerConfiguration();
        compilerConfiguration.setScriptBaseClass(SpecificationDSLInterpreter.class.getName());

        final GroovyShell shell = new GroovyShell(binding, compilerConfiguration);
        try {
            shell.evaluate(file);

            return new SpecificationPartial(
                    (Collection<Constraint>) binding.getProperty("constraints"),
                    (Collection<ConfigError>) binding.getProperty("errors")
            );

        } catch (IOException e) {
            final Collection<ConfigError> errors = new ArrayList<>();
            errors.add(new FailedToLoadSpecificationError(file, e));

            return new SpecificationPartial(emptyList(), errors);
        }
    }
}
