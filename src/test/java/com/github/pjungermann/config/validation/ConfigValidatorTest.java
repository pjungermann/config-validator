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
package com.github.pjungermann.config.validation;

import com.github.pjungermann.config.DefaultKeyBuilder;
import com.github.pjungermann.config.loader.DefaultConfigLoader;
import com.github.pjungermann.config.specification.DefaultConfigSpecificationLoader;
import com.github.pjungermann.config.specification.constraint.ConstraintRegistry;
import com.github.pjungermann.config.specification.constraint.matches.MatchesConstraintFactory;
import com.github.pjungermann.config.specification.constraint.maxSize.MaxSizeConstraintFactory;
import com.github.pjungermann.config.specification.constraint.nullable.NullableConstraintFactory;
import com.github.pjungermann.config.specification.constraint.range.RangeConstraintFactory;
import com.github.pjungermann.config.specification.constraint.size.SizeConstraintFactory;
import com.github.pjungermann.config.specification.dsl.groovy.GroovyDSLSpecificationReader;
import com.github.pjungermann.config.specification.types.AsTypeConverter;
import com.github.pjungermann.config.types.DefaultConfigFactorySelector;
import com.github.pjungermann.config.types.groovy.ConfigObjectConfigFactory;
import com.github.pjungermann.config.types.groovy.ConfigObjectConverter;
import com.github.pjungermann.config.types.ini.IniConfigFactory;
import com.github.pjungermann.config.types.ini.IniConverter;
import com.github.pjungermann.config.types.json.JsonConfigFactory;
import com.github.pjungermann.config.types.json.JsonConverter;
import com.github.pjungermann.config.types.properties.PropertiesConfigFactory;
import com.github.pjungermann.config.types.properties.PropertiesConverter;
import com.github.pjungermann.config.types.yaml.YamlConfigFactory;
import com.github.pjungermann.config.types.yaml.YamlConverter;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.context.support.StaticApplicationContext;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Locale;

import static org.junit.Assert.*;

/**
 * Tests for {@link ConfigValidator}.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class ConfigValidatorTest {

    private static final File TEST_RESOURCES = new File("src/test/resources");
    private static final File CONFIG_ROOT = new File(TEST_RESOURCES, "configs");
    private static final File SPECIFICATION_ROOT = new File(TEST_RESOURCES, "specs");

    private static StaticApplicationContext applicationContext;

    private ConfigValidator validator;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    static void autowireByConstructor(Class... classes) {
        for (Class clazz : classes) {
            applicationContext.registerBeanDefinition(
                    clazz.getName(),
                    BeanDefinitionBuilder
                            .rootBeanDefinition(clazz)
                            .setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_CONSTRUCTOR)
                            .getBeanDefinition()
            );
        }
    }

    static void registerSingleton(Class... classes) {
        for (Class clazz : classes) {
            applicationContext.registerBeanDefinition(
                    clazz.getName(),
                    BeanDefinitionBuilder
                            .rootBeanDefinition(clazz)
                            .setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_TYPE)
                            .getBeanDefinition()
            );
        }
    }

    @BeforeClass
    public static void setUpApplicationContext() {
        applicationContext = new StaticApplicationContext();

        registerSingleton(
                // config factories + converters
                DefaultKeyBuilder.class,
                ConfigObjectConfigFactory.class,
                ConfigObjectConverter.class,
                IniConfigFactory.class,
                IniConverter.class,
                JsonConfigFactory.class,
                JsonConverter.class,
                PropertiesConfigFactory.class,
                PropertiesConverter.class,
                YamlConfigFactory.class,
                YamlConverter.class,
                // config factory selector
                DefaultConfigFactorySelector.class,
                // type converter
                AsTypeConverter.class,
                // constraints
                MatchesConstraintFactory.class,
                MaxSizeConstraintFactory.class,
                NullableConstraintFactory.class,
                RangeConstraintFactory.class,
                SizeConstraintFactory.class
        );

        autowireByConstructor(
                // constraint registry
                ConstraintRegistry.class,
                // config and specification loader
                DefaultConfigLoader.class,
                GroovyDSLSpecificationReader.class,
                DefaultConfigSpecificationLoader.class,
                // validator
                ConfigValidator.class
        );

        applicationContext.refresh();
    }

    /**
     * Creates a message for a message code
     * which consists of the code itself
     * as well as of {@code numArguments} placeholders
     * for the {@code numArguments} arguments max. provided.
     * This helps to print out the arguments as well as
     * the used message code in a predefined order.
     *
     * @param code         The message code.
     * @param numArguments The amount of arguments, max expected for it.
     */
    void addMessage(String code, int numArguments) {
        String argumentPlaceholders = "";
        for (int i = 0; i < numArguments; i++) {
            argumentPlaceholders += "{" + i + "} - ";
        }
        if (numArguments > 0) {
            argumentPlaceholders = argumentPlaceholders.substring(0, argumentPlaceholders.length() - 3);
        }

        applicationContext.getStaticMessageSource().addMessage(
                code,
                Locale.getDefault(),
                code + ": " + argumentPlaceholders
        );
    }

    @Before
    public void setUp() {
        addMessage("constraints.invalid.default.message", 6);
        addMessage("errors.config_file.default", 4);
        addMessage("errors.type_conversion_settings", 4);
        addMessage("errors.type_conversion_settings", 3);
        addMessage("errors.no_such_constraint", 3);
        addMessage("errors.keys_without_specification", 1);

        validator = applicationContext.getBean(ConfigValidator.class);
    }

    @Test
    public void validate_allConfigsAndSpecsAndNonStrict_validateWithKnownErrors() {
        try {
            validator.validate(
                    new String[]{CONFIG_ROOT.toString()},
                    new String[]{SPECIFICATION_ROOT.toString()},
                    true,
                    null,
                    false
            );
            fail("was expected to fail with an exception");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- errors.type_conversion_settings: will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) - invalid\n" +
                            "- errors.no_such_constraint: doesNotExist - will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23)",
                    e.getMessage());
        }
    }

    @Test
    public void validate_allConfigsAndSpecsAndStrict_validateWithKnownErrors() {
        try {
            validator.validate(
                    new String[]{CONFIG_ROOT.toString()},
                    new String[]{SPECIFICATION_ROOT.toString()},
                    true,
                    null,
                    true
            );
            fail("was expected to fail with an exception");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- errors.type_conversion_settings: will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) - invalid\n" +
                            "- errors.no_such_constraint: doesNotExist - will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23)\n" +
                            "- errors.keys_without_specification: [another.entry, another_section.another, bill-to.address.city, bill-to.address.lines, bill-to.address.postal, bill-to.address.state, bill-to.family, bill-to.given, closure, comments, date, double.string, double_value, env.aware, env.override, first, float_value, ini_section.foo, ini_section.key1, ini_section.key2, ini_section.sub_section.sub1, ini_section/sub_section.sub1, int_value, invoice, level1.level2.boolean_false, level1.level2.boolean_true, level1.level2.double-type, level1.level2.int-type, level1.level2.string-type, product.0.description, product.0.price, product.0.quantity, product.0.sku, product.1.description, product.1.price, product.1.quantity, product.1.sku, ship-to.address.city, ship-to.address.lines, ship-to.address.postal, ship-to.address.state, ship-to.family, ship-to.given, tax, total, with_flat_key_dependency, with_hierarchical_key_dependency]",
                    e.getMessage());
        }
    }

    @Test
    public void validate_allConfigsAndSpecsAndStrictAdditionalSpecForSomeKeysWithoutSpec_validateWithKnownErrors() throws IOException {
        File customSpec = temporaryFolder.newFile();
        try (FileWriter writer = new FileWriter(customSpec)) {
            writer.write(
                    "\"another.entry\"(nullable: false)\n" +
                            "\"bill-to.address.city\"(nullable: false)"
            );
        }

        try {
            validator.validate(
                    new String[]{CONFIG_ROOT.toString()},
                    new String[]{SPECIFICATION_ROOT.toString(), customSpec.toString()},
                    true,
                    null,
                    true
            );
            fail("was expected to fail with an exception");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- errors.type_conversion_settings: will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) - invalid\n" +
                            "- errors.no_such_constraint: doesNotExist - will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23)\n" +
                            "- errors.keys_without_specification: [another_section.another, bill-to.address.lines, bill-to.address.postal, bill-to.address.state, bill-to.family, bill-to.given, closure, comments, date, double.string, double_value, env.aware, env.override, first, float_value, ini_section.foo, ini_section.key1, ini_section.key2, ini_section.sub_section.sub1, ini_section/sub_section.sub1, int_value, invoice, level1.level2.boolean_false, level1.level2.boolean_true, level1.level2.double-type, level1.level2.int-type, level1.level2.string-type, product.0.description, product.0.price, product.0.quantity, product.0.sku, product.1.description, product.1.price, product.1.quantity, product.1.sku, ship-to.address.city, ship-to.address.lines, ship-to.address.postal, ship-to.address.state, ship-to.family, ship-to.given, tax, total, with_flat_key_dependency, with_hierarchical_key_dependency]",
                    e.getMessage());
            assertFalse(e.getMessage().contains("another.entry"));
            assertFalse(e.getMessage().contains("bill-to.address.city"));
        }
    }

    @Test
    public void validate_allConfigsNonRecursiveAndSpecsAndNonStrict_validateWithKnownErrors() {
        try {
            validator.validate(
                    new String[]{CONFIG_ROOT.toString()},
                    new String[]{SPECIFICATION_ROOT.toString()},
                    false,
                    null,
                    false
            );
            fail("was expected to fail with an exception");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- errors.type_conversion_settings: will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) - invalid\n" +
                            "- errors.no_such_constraint: doesNotExist - will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23)\n" +
                            "- constraints.invalid.default.message: src/test/resources/specs/config_spec.groovy(config_spec.groovy:18) - my.config.key - null - false - nullable - {5}",
                    e.getMessage());
        }
    }

    @Test
    public void validate_allConfigsNonRecursiveAndSpecsAndStrict_validateWithKnownErrors() {
        try {
            validator.validate(
                    new String[]{CONFIG_ROOT.toString()},
                    new String[]{SPECIFICATION_ROOT.toString()},
                    false,
                    null,
                    true
            );
            fail("was expected to fail with an exception");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- errors.type_conversion_settings: will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) - invalid\n" +
                            "- errors.no_such_constraint: doesNotExist - will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23)\n" +
                            "- constraints.invalid.default.message: src/test/resources/specs/config_spec.groovy(config_spec.groovy:18) - my.config.key - null - false - nullable - {5}\n" +
                            "- errors.keys_without_specification: [another_section.another, closure, double_value, env.aware, env.override, first, float_value, ini_section.foo, ini_section.key1, ini_section.key2, ini_section.sub_section.sub1, ini_section/sub_section.sub1, int_value, level1.level2.boolean_false, level1.level2.boolean_true, level1.level2.double-type, level1.level2.int-type, level1.level2.string-type, with_flat_key_dependency, with_hierarchical_key_dependency]",
                    e.getMessage());
        }
    }

    @Test
    public void validate_allConfigsNonRecursiveAndSpecsAndStrictAdditionalSpecForSomeKeysWithoutSpec_validateWithKnownErrors() throws IOException {
        File customSpec = temporaryFolder.newFile();
        try (FileWriter writer = new FileWriter(customSpec)) {
            writer.write(
                    "\"another.entry\"(nullable: false)\n" +
                            "\"bill-to.address.city\"(nullable: false)"
            );
        }

        try {
            validator.validate(
                    new String[]{new File(CONFIG_ROOT, "sub-dir").toString()},
                    new String[]{SPECIFICATION_ROOT.toString(), customSpec.toString()},
                    false,
                    null,
                    true
            );
            fail("was expected to fail with an exception");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- errors.type_conversion_settings: will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) - invalid\n" +
                            "- errors.no_such_constraint: doesNotExist - will.fail - src/test/resources/specs/config_spec.groovy(config_spec.groovy:23)\n" +
                            "- errors.keys_without_specification: [bill-to.address.lines, bill-to.address.postal, bill-to.address.state, bill-to.family, bill-to.given, comments, date, double.string, invoice, product.0.description, product.0.price, product.0.quantity, product.0.sku, product.1.description, product.1.price, product.1.quantity, product.1.sku, ship-to.address.city, ship-to.address.lines, ship-to.address.postal, ship-to.address.state, ship-to.family, ship-to.given, tax, total]",
                    e.getMessage());
            assertFalse(e.getMessage().contains("another.entry"));
            assertFalse(e.getMessage().contains("bill-to.address.city"));
        }
    }
}
