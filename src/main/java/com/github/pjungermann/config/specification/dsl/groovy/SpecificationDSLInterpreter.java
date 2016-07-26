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
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.ConstraintRegistry;
import com.github.pjungermann.config.specification.types.TypeConverter;
import groovy.lang.Binding;
import groovy.lang.Script;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

/**
 * Groovy baked DSL for {@link com.github.pjungermann.config.specification.ConfigSpecification}
 * files.
 *
 * The interpreter will read the specification within such a {@link File}
 * and return the specification definitions.
 *
 * @author Patrick Jungermann
 */
public abstract class SpecificationDSLInterpreter extends Script {

    private static final Logger LOGGER = LoggerFactory.getLogger(SpecificationDSLInterpreter.class);

    private File source;
    private TypeConverter typeConverter;
    private ConstraintRegistry constraintRegistry;
    private List<Constraint> constraints = new ArrayList<>();
    private Collection<ConfigError> errors = new ArrayList<>();

    public abstract Object specificationBody();

    public Object run() {
        source = (File) getBinding().getProperty("specification.source");
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("load specification from {}", source);
        }

        resolveBeans();

        specificationBody();

        Binding binding = getBinding();
        binding.setProperty("errors", errors);
        binding.setProperty("constraints", constraints);

        return null;
    }

    public void resolveBeans() {
        final ApplicationContext context = (ApplicationContext) getBinding().getProperty("context");

        typeConverter = context.getBean(TypeConverter.class);
        constraintRegistry = context.getBean(ConstraintRegistry.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object invokeMethod(final String key, final Object args) {
        if (args instanceof Object[]
                && ((Object[]) args).length == 1
                && ((Object[]) args)[0] instanceof Map) {
            final Map<String, Object> settings = (Map<String, Object>) ((Object[]) args)[0];
            final SourceLine sourceLine = new SourceLine(source, getCurrentLine());

            final List<Constraint> constraints = settings
                    .entrySet()
                    .stream()
                    .map(new DefinitionParser(key, sourceLine, typeConverter, constraintRegistry, errors))
                    .filter(item -> item != null)
                    .collect(toList());

            this.constraints.addAll(constraints);

            return null;
        }

        return super.invokeMethod(key, args);
    }

    protected int getCurrentLine() {
        for (final StackTraceElement element : new Exception().getStackTrace()) {
            if (element.getFileName().equals(source.getName())) {
                return element.getLineNumber();
            }
        }

        return -1;
    }
}
