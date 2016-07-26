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
import com.github.pjungermann.config.validation.ConfigValidator;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.MissingOptionException;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

/**
 * Application to run the validation.
 *
 * @author Patrick Jungermann
 */
@Configuration
@ComponentScan
@SuppressWarnings("SpringFacetCodeInspection")
public class Application {

    private static final Logger LOGGER = LoggerFactory.getLogger(Application.class);

    public static void main(final String[] arguments) throws ConfigValidationException {
        LOGGER.info("start config validation");

        final ApplicationCLI cli;
        try {
            cli = new ApplicationCLI(arguments);

        } catch (MissingArgumentException | MissingOptionException | UnrecognizedOptionException e) {
            ApplicationCLI.usage();
            return;

        } catch (ParseException e) {
            LOGGER.error("arguments parsing failed", e);
            ApplicationCLI.usage();
            return;
        }

        final AnnotationConfigApplicationContext context =
                new AnnotationConfigApplicationContext(Application.class);

        final ConfigValidator validator = context.getBean(ConfigValidator.class);
        validator.validate(cli.configs, cli.specs, cli.recursive, cli.profile, cli.strict);

        LOGGER.info("config validation finished");
    }
}
