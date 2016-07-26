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

import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ResourceBundleMessageSource;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link MessageSourceConfiguration}.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class MessageSourceConfigurationTest {

    @Test
    public void isConfigurationClass() {
        assertTrue(
                MessageSourceConfiguration.class
                        .isAnnotationPresent(Configuration.class)
        );
    }

    @Test
    public void messageSourceMethod_markedWithBeanAnnotation() throws NoSuchMethodException {
        assertTrue(
                MessageSourceConfiguration.class
                        .getMethod("messageSource")
                        .isAnnotationPresent(Bean.class)
        );
    }

    @Test
    public void messageSource_always_setUpMessageSourceCorrectly() {
        MessageSource messageSource = new MessageSourceConfiguration().messageSource();

        assertTrue(messageSource instanceof ResourceBundleMessageSource);
        ResourceBundleMessageSource resourceBundleMessageSource = (ResourceBundleMessageSource) messageSource;
        assertEquals(2, resourceBundleMessageSource.getBasenameSet().size());
        String baseDir = MessageSourceConfiguration.class.getPackage().getName().replace(".", "/");
        assertTrue(resourceBundleMessageSource.getBasenameSet().contains(baseDir + "/constraints"));
        assertTrue(resourceBundleMessageSource.getBasenameSet().contains(baseDir + "/errors"));
    }
}
