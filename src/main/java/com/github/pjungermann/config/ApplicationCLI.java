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

import org.apache.commons.cli.*;
import org.jetbrains.annotations.NotNull;

/**
 * Application's command line interface.
 *
 * @author Patrick Jungermann
 */
public class ApplicationCLI {

    static final Option PROFILE_OPTION = Option
            .builder("p")
            .longOpt("profile")
            .hasArg()
            .argName("profile name")
            .desc("profile")
            .build();

    static final Option CONFIGS_OPTION = Option
            .builder()
            .longOpt("configs")
            .required()
            .hasArgs()
            .numberOfArgs(Option.UNLIMITED_VALUES)
            .valueSeparator(';')
            .argName("config1;config2;...;configN")
            .desc("config sources")
            .build();

    static final Option SPECS_OPTION = Option
            .builder()
            .longOpt("specs")
            .required()
            .hasArgs()
            .numberOfArgs(Option.UNLIMITED_VALUES)
            .valueSeparator(';')
            .argName("spec1;spec2;...;specN")
            .desc("specification sources")
            .build();

    static final Option RECURSIVE_OPTION = Option
            .builder("r")
            .longOpt("recursive")
            .required(false)
            .desc("config and specification sources will get discovered from folders recursively")
            .build();

    static final Option STRICT_OPTION = Option
            .builder("s")
            .longOpt("strict")
            .required(false)
            .desc("In strict mode, keys without specification are not permitted.")
            .build();

    static final Options OPTIONS;

    static {
        OPTIONS = new Options();
        OPTIONS.addOption(PROFILE_OPTION);
        OPTIONS.addOption(CONFIGS_OPTION);
        OPTIONS.addOption(SPECS_OPTION);
        OPTIONS.addOption(RECURSIVE_OPTION);
        OPTIONS.addOption(STRICT_OPTION);
    }

    public static void usage() {
        new HelpFormatter().printHelp(
                "config_validator",
                OPTIONS,
                true
        );
    }

    public final String profile;
    public final String[] configs;
    public final String[] specs;
    public final boolean recursive;
    public final boolean strict;

    public ApplicationCLI(@NotNull final String[] arguments) throws ParseException {
        final CommandLine cmd = new DefaultParser().parse(OPTIONS, arguments);

        profile = cmd.getOptionValue(PROFILE_OPTION.getLongOpt());
        configs = cmd.getOptionValues(CONFIGS_OPTION.getLongOpt());
        specs = cmd.getOptionValues(SPECS_OPTION.getLongOpt());
        recursive = cmd.hasOption(RECURSIVE_OPTION.getLongOpt());
        strict = cmd.hasOption(STRICT_OPTION.getLongOpt());
    }
}
