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
package com.github.pjungermann.config;

import com.github.pjungermann.config.validation.ConfigValidationException;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

/**
 * Tests for {@link Application}.
 *
 * @author Patrick Jungermann
 */
public class ApplicationTest {

    @Test
    public void main_allConfigsAndSpecsAndStrict_validateWithKnownErrors() {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs",
                            "--specs", "src/test/resources/specs",
                            "-r",
                            "-s"
                    }
            );

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))\n" +
                            "- Keys without specification: [another.entry, another_section.another, bill-to.address.city, bill-to.address.lines, bill-to.address.postal, bill-to.address.state, bill-to.family, bill-to.given, closure, comments, date, double.string, double_value, env.aware, env.override, first, float_value, ini_section.foo, ini_section.key1, ini_section.key2, ini_section.sub_section.sub1, ini_section/sub_section.sub1, int_value, invoice, level1.level2.boolean_false, level1.level2.boolean_true, level1.level2.double-type, level1.level2.int-type, level1.level2.string-type, product.0.description, product.0.price, product.0.quantity, product.0.sku, product.1.description, product.1.price, product.1.quantity, product.1.sku, ship-to.address.city, ship-to.address.lines, ship-to.address.postal, ship-to.address.state, ship-to.family, ship-to.given, tax, total, with_flat_key_dependency, with_hierarchical_key_dependency]",
                    e.getMessage());
        }
    }

    @Test
    public void main_allConfigsAndSpecsAndNonStrict_validateWithKnownErrors() {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs",
                            "--specs", "src/test/resources/specs",
                            "-r"
                    }
            );

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))",
                    e.getMessage());
        }
    }

    @Test
    public void main_allConfigsNonRecursiveAndSpecsAndStrict_validateWithKnownErrors() {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs",
                            "--specs", "src/test/resources/specs",
                            "-s"
                    }
            );

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))\n" +
                            "- Config \"my.config.key\" value cannot be null - src/test/resources/specs/config_spec.groovy(config_spec.groovy:18)\n" +
                            "- Keys without specification: [another_section.another, closure, double_value, env.aware, env.override, first, float_value, ini_section.foo, ini_section.key1, ini_section.key2, ini_section.sub_section.sub1, ini_section/sub_section.sub1, int_value, level1.level2.boolean_false, level1.level2.boolean_true, level1.level2.double-type, level1.level2.int-type, level1.level2.string-type, with_flat_key_dependency, with_hierarchical_key_dependency]",
                    e.getMessage());
        }
    }

    @Test
    public void main_allConfigsNonRecursiveAndSpecsAndNonStrict_validateWithKnownErrors() {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs",
                            "--specs", "src/test/resources/specs"
                    }
            );

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))\n" +
                            "- Config \"my.config.key\" value cannot be null - src/test/resources/specs/config_spec.groovy(config_spec.groovy:18)",
                    e.getMessage());
        }
    }

    @Test
    public void main_invalidConfigFileOrFolder_validationFailed() throws IOException {
        try {
            Application.main(
                    new String[]{
                            "--configs", "does_not_exist",
                            "--specs", "src/test/resources/specs"
                    }
            );
            fail("not supposed to be successful");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- no such file: does_not_exist\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))\n" +
                            "- Config \"my.config.key\" value cannot be null - src/test/resources/specs/config_spec.groovy(config_spec.groovy:18)",
                    e.getMessage().replace(new File(".").getCanonicalPath() + File.separator, ""));
        }
    }

    @Test
    public void main_validAndInvalidConfigFiles_validationFailed() throws IOException {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs;does_not_exist",
                            "--specs", "src/test/resources/specs"
                    }
            );
            fail("not supposed to be successful");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- no such file: does_not_exist\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))\n" +
                            "- Config \"my.config.key\" value cannot be null - src/test/resources/specs/config_spec.groovy(config_spec.groovy:18)",
                    e.getMessage().replace(new File(".").getCanonicalPath() + File.separator, ""));
        }
    }

    @Test
    public void main_invalidSpecsFileOrFolder_validationFailed() throws IOException {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs",
                            "--specs", "does_not_exist"
                    }
            );
            fail("not supposed to be successful");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- no such file: does_not_exist",
                    e.getMessage().replace(new File(".").getCanonicalPath() + File.separator, ""));
        }
    }

    @Test
    public void main_validAndInvalidSpecsFiles_validationFailed() throws IOException {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/configs",
                            "--specs", "src/test/resources/specs;does_not_exist"
                    }
            );
            fail("not supposed to be successful");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- no such file: does_not_exist\n" +
                            "- The type conversion for \"will.fail\" as defined at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23) could not be done due to bad settings: invalid\n" +
                            "- There is no such constraint \"doesNotExist\" (used for key \"will.fail\" at src/test/resources/specs/config_spec.groovy(config_spec.groovy:23))\n" +
                            "- Config \"my.config.key\" value cannot be null - src/test/resources/specs/config_spec.groovy(config_spec.groovy:18)",
                    e.getMessage().replace(new File(".").getCanonicalPath() + File.separator, ""));
        }
    }

    @Test
    public void main_collectionItemSpecWithValidValue_noValidationException() throws ConfigValidationException {
        Application.main(
                new String[]{
                        "--configs", "src/test/resources/collectionTypeTest/config_valid.groovy",
                        "--specs", "src/test/resources/collectionTypeTest/spec.groovy"
                }
        );
    }

    @Test
    public void main_collectionItemSpecWithInvalidValue_knowValidationErrors() {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/collectionTypeTest/config_invalid.groovy",
                            "--specs", "src/test/resources/collectionTypeTest/spec.groovy"
                    }
            );
            fail("not supposed to be successful");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- validation errors for collection with key \"collection\":\n" +
                            "  - Config \"collection.[0].field\" with value [item1.field.invalid] is not contained within the list [item1.field] - src/test/resources/collectionTypeTest/spec.groovy(spec.groovy:16)\n" +
                            "- validation errors for collection with key \"collection\":\n" +
                            "  - Config \"collection.[1].field\" with value [item2.field.invalid] is not contained within the list [item2.field] - src/test/resources/collectionTypeTest/spec.groovy(spec.groovy:17)\n" +
                            "- validation errors for collection with key \"collection\":\n" +
                            "  - Config \"collection.[0..1].field\" with value [item1.field.invalid] does not apply to constraint \"matches\" with settings item\\d\\.field - src/test/resources/collectionTypeTest/spec.groovy(spec.groovy:18)\n" +
                            "  - Config \"collection.[0..1].field\" with value [item2.field.invalid] does not apply to constraint \"matches\" with settings item\\d\\.field - src/test/resources/collectionTypeTest/spec.groovy(spec.groovy:18)\n" +
                            "- validation errors for collection with key \"collection\":\n" +
                            "  - Config \"collection.[*].field\" with value [item1.field.invalid] does not apply to constraint \"matches\" with settings item\\d\\.field - src/test/resources/collectionTypeTest/spec.groovy(spec.groovy:19)\n" +
                            "  - Config \"collection.[*].field\" with value [item2.field.invalid] does not apply to constraint \"matches\" with settings item\\d\\.field - src/test/resources/collectionTypeTest/spec.groovy(spec.groovy:19)",
                    e.getMessage());
        }
    }

    @Test
    public void main_collectionItemSpecAndNoCollection_knowValidationErrors() {
        try {
            Application.main(
                    new String[]{
                            "--configs", "src/test/resources/collectionTypeTest/config_noCollection.groovy",
                            "--specs", "src/test/resources/collectionTypeTest/spec.groovy"
                    }
            );
            fail("not supposed to be successful");

        } catch (ConfigValidationException e) {
            assertEquals(
                    "Validation errors:\n" +
                            "- Value \"is not a collection\" for key \"collection\" is not a collection; collection key \"collection.[0].field\"\n" +
                            "- Value \"is not a collection\" for key \"collection\" is not a collection; collection key \"collection.[1].field\"\n" +
                            "- Value \"is not a collection\" for key \"collection\" is not a collection; collection key \"collection.[0..1].field\"\n" +
                            "- Value \"is not a collection\" for key \"collection\" is not a collection; collection key \"collection.[*].field\"",
                    e.getMessage());
        }
    }
}
