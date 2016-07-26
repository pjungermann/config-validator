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

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.ConstraintRegistry;
import com.github.pjungermann.config.specification.constraint.NoSuchConstraintError;
import com.github.pjungermann.config.specification.constraint.matches.MatchesConstraint;
import com.github.pjungermann.config.specification.constraint.matches.MatchesConstraintFactory;
import com.github.pjungermann.config.specification.constraint.maxSize.MaxSizeConstraint;
import com.github.pjungermann.config.specification.constraint.maxSize.MaxSizeConstraintFactory;
import com.github.pjungermann.config.specification.constraint.nullable.NullableConstraint;
import com.github.pjungermann.config.specification.constraint.nullable.NullableConstraintFactory;
import com.github.pjungermann.config.specification.constraint.range.RangeConstraint;
import com.github.pjungermann.config.specification.constraint.range.RangeConstraintFactory;
import com.github.pjungermann.config.specification.constraint.size.SizeConstraint;
import com.github.pjungermann.config.specification.constraint.size.SizeConstraintFactory;
import com.github.pjungermann.config.specification.reader.SpecificationPartial;
import com.github.pjungermann.config.specification.types.AsTypeConverter;
import com.github.pjungermann.config.specification.types.TypeConversionConfigError;
import groovy.lang.IntRange;
import groovy.lang.ObjectRange;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.StaticApplicationContext;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * Tests for {@link GroovyDSLSpecificationReader}.
 * Also tests the {@link SpecificationDSLInterpreter}
 * and {@link DefinitionParser} implicitly.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class GroovyDSLSpecificationReaderTest {

    private static final File TEST_SPEC = new File("src/test/resources/specs/config_spec.groovy");

    StaticApplicationContext applicationContext;
    GroovyDSLSpecificationReader reader;

    @Before
    public void setUp() {
        applicationContext = new StaticApplicationContext();
        applicationContext.registerSingleton(NullableConstraintFactory.class.getName(), NullableConstraintFactory.class);
        applicationContext.registerSingleton(MatchesConstraintFactory.class.getName(), MatchesConstraintFactory.class);
        applicationContext.registerSingleton(RangeConstraintFactory.class.getName(), RangeConstraintFactory.class);
        applicationContext.registerSingleton(SizeConstraintFactory.class.getName(), SizeConstraintFactory.class);
        applicationContext.registerSingleton(MaxSizeConstraintFactory.class.getName(), MaxSizeConstraintFactory.class);
        applicationContext.registerSingleton("typeConverter", AsTypeConverter.class);
        BeanDefinition beanDefinition = BeanDefinitionBuilder
                .rootBeanDefinition(ConstraintRegistry.class)
                .setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR)
                .getBeanDefinition();
        applicationContext.registerBeanDefinition("constraintRegistry", beanDefinition);

        reader = new GroovyDSLSpecificationReader(applicationContext);
    }

    @Test
    public void apply_testSpec_readAllDefinitions() {
        SpecificationPartial partial = reader.apply(TEST_SPEC);

        assertNotNull(partial);
        // validate type conversions
        AsTypeConverter converter = applicationContext.getBean(AsTypeConverter.class);
        assertEquals(3, converter.getKeys().size());
        assertTrue(converter.getKeys().contains("double.string.1"));
        assertTrue(converter.getKeys().contains("double.string.2"));
        assertTrue(converter.getKeys().contains("without.round.braces"));
        Config config = new Config();
        config.put("double.string.1", "24");
        config.put("double.string.2", "54.3");
        config.put("without.round.braces", 34);
        converter.convert(config);
        assertTrue(config.get("double.string.1") instanceof Double);
        assertTrue(config.get("double.string.2") instanceof Double);
        assertTrue(config.get("without.round.braces") instanceof String);
        // validate constraints
        assertEquals(6, partial.constraints.size());
        List<String> constraints = partial.constraints.stream()
                .map(Constraint::toString)
                .collect(Collectors.toList());
        assertTrue(constraints.contains(
                new NullableConstraint("my.config.key", false, new SourceLine(TEST_SPEC, 18)).toString()
        ));
        assertTrue(constraints.contains(
                new MatchesConstraint("my.config.key", "config.*", new SourceLine(TEST_SPEC, 18)).toString()
        ));
        assertTrue(constraints.contains(
                new RangeConstraint("double.string.1", new IntRange(0, 1), new SourceLine(TEST_SPEC, 19)).toString()
        ));
        assertTrue(constraints.contains(
                new RangeConstraint("double.string.2", new ObjectRange(0D, 1D), new SourceLine(TEST_SPEC, 20)).toString()
        ));
        assertTrue(constraints.contains(
                new SizeConstraint("list", new IntRange(1, 3), new SourceLine(TEST_SPEC, 21)).toString()
        ));
        assertTrue(constraints.contains(
                new MaxSizeConstraint("without.round.braces", 5, new SourceLine(TEST_SPEC, 22)).toString()
        ));
        // validate errors
        assertEquals(2, partial.errors.size());
        assertTrue(partial.errors.stream().anyMatch(error ->
            error instanceof TypeConversionConfigError
                    && ((TypeConversionConfigError) error).key.equals("will.fail")
                    && ((TypeConversionConfigError) error).sourceLine.equals(new SourceLine(TEST_SPEC, 23))
                    && ((TypeConversionConfigError) error).config.equals("invalid")
        ));
        assertTrue(partial.errors.stream().anyMatch(error ->
                        error instanceof NoSuchConstraintError
                                && ((NoSuchConstraintError) error).key.equals("will.fail")
                                && ((NoSuchConstraintError) error).sourceLine.equals(new SourceLine(TEST_SPEC, 23))
                                && ((NoSuchConstraintError) error).name.equals("doesNotExist")
        ));
    }
}
