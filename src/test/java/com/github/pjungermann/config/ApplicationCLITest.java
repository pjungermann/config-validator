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

import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Tests for {@link ApplicationCLI}.
 *
 * @author Patrick Jungermann
 */
public class ApplicationCLITest {

    static String PROFILE = "fake-profile";

    @Test
    public void constructor_allLongOptionsWithValues_validArguments() throws ParseException {
        ApplicationCLI cli = new ApplicationCLI(
                new String[]{
                        "--profile", PROFILE,
                        "--configs", "foo/bar;baz",
                        "--specs", "spec1;specs/spec2",
                        "--recursive",
                        "--strict"
                }
        );

        assertEquals(PROFILE, cli.profile);
        assertArrayEquals(new String[]{
                "foo/bar",
                "baz"
        }, cli.configs);
        assertArrayEquals(new String[]{
                "spec1",
                "specs/spec2"
        }, cli.specs);
        assertTrue(cli.recursive);
        assertTrue(cli.strict);
    }

    @Test
    public void constructor_shortOptionsAndLongOptionsWithValues_validArguments() throws ParseException {
        ApplicationCLI cli = new ApplicationCLI(
                new String[]{
                        "-p", PROFILE,
                        "--configs", "foo/bar;baz",
                        "--specs", "spec1;specs/spec2",
                        "-r",
                        "-s"
                }
        );

        assertEquals(PROFILE, cli.profile);
        assertArrayEquals(new String[]{
                "foo/bar",
                "baz"
        }, cli.configs);
        assertArrayEquals(new String[]{
                "spec1",
                "specs/spec2"
        }, cli.specs);
        assertTrue(cli.recursive);
        assertTrue(cli.strict);
    }

    @Test
    public void constructor_allNonOptionalOptionsWithValues_validArgumentsAndNotStrictAndNotRecursive() throws ParseException {
        ApplicationCLI cli = new ApplicationCLI(
                new String[]{
                        "--configs", "foo/bar;baz",
                        "--specs", "spec1;specs/spec2"
                }
        );

        assertNull(cli.profile);
        assertArrayEquals(new String[]{
                "foo/bar",
                "baz"
        }, cli.configs);
        assertArrayEquals(new String[]{
                "spec1",
                "specs/spec2"
        }, cli.specs);
        assertFalse(cli.recursive);
        assertFalse(cli.strict);
    }

    @Test(expected = MissingOptionException.class)
    public void constructor_noConfigs_missingOption() throws ParseException {
        new ApplicationCLI(
                new String[]{
                        "--specs", "spec1;specs/spec2"
                }
        );
    }

    @Test(expected = MissingOptionException.class)
    public void constructor_noSpecs_missingOption() throws ParseException {
        new ApplicationCLI(
                new String[]{
                        "--configs", "foo/bar;baz"
                }
        );
    }

    @Test(expected = MissingArgumentException.class)
    public void constructor_noConfigsValues_missingArgument() throws ParseException {
        new ApplicationCLI(
                new String[]{
                        "--configs",
                        "--specs", "spec1;specs/spec2"
                }
        );
    }

    @Test(expected = MissingArgumentException.class)
    public void constructor_noSpecsValues_missingArgument() throws ParseException {
        new ApplicationCLI(
                new String[]{
                        "--configs", "foo/bar;baz",
                        "--specs"
                }
        );
    }

    @Test(expected = MissingArgumentException.class)
    public void constructor_noProfileValue_missingArgument() throws ParseException {
        new ApplicationCLI(
                new String[]{
                        "-p",
                        "--configs", "foo/bar;baz",
                        "--specs", "spec1;specs/spec2"
                }
        );
    }
}
