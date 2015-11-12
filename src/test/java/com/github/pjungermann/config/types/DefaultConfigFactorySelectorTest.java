/*
 * Copyright 2015 Patrick Jungermann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.pjungermann.config.types;

import com.github.pjungermann.config.Config;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Module;
import com.google.inject.Provider;
import com.google.inject.multibindings.Multibinder;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Test;
import org.springframework.context.support.StaticApplicationContext;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for {@link DefaultConfigFactorySelector}.
 *
 * @author Patrick Jungermann
 */
public class DefaultConfigFactorySelectorTest {

    @Test
    public void beanCreation_withSpringContextAndNoFactory_noErrorAndEmptyListAsDefault() {
        StaticApplicationContext context = new StaticApplicationContext();
        context.registerSingleton(DefaultConfigFactorySelector.class.getName(), DefaultConfigFactorySelector.class);
        ConfigFactorySelector selector = context.getBean(ConfigFactorySelector.class);

        assertValidBean(selector);
    }

    @Test
    public void beanCreation_withGuiceAndNoFactory_noErrorAndEmptyListAsDefault() {
        Module module = new AbstractModule() {
            @Override
            protected void configure() {
                bind(ConfigFactorySelector.class)
                        .to(DefaultConfigFactorySelector.class);
                // multi binder without implementations
                Multibinder.newSetBinder(binder(), ConfigFactory.class);
            }
        };
        Provider<ConfigFactorySelector> provider =
                Guice.createInjector(module)
                        .getProvider(ConfigFactorySelector.class);
        ConfigFactorySelector selector = provider.get();

        assertValidBean(selector);
    }

    @Test
    public void getFactory_withoutFactories_returnNull() {
        DefaultConfigFactorySelector selector = new DefaultConfigFactorySelector();
        assertNull(selector.getFactory(new File("foo.bar")));
    }

    @Test
    public void getFactory_withFactories_returnApplicableFactory() {
        ConfigFactory factory1 = new TestConfigFactory(false);
        ConfigFactory factory2 = new TestConfigFactory(true);
        ConfigFactory factory3 = new TestConfigFactory(true);
        Set<ConfigFactory> factories = new HashSet<>(3);
        factories.add(factory1);
        factories.add(factory2);
        factories.add(factory3);
        assertEquals(3, factories.size());

        DefaultConfigFactorySelector selector = new DefaultConfigFactorySelector();
        selector.setConfigFactories(factories);

        ConfigFactory result = selector.getFactory(new File("foo.bar"));

        assertOneOf(result, factory2, factory3);
    }

    @Test
    public void getFactory_withFactoriesButNoMatchingOne_returnNull() {
        ConfigFactory factory1 = new TestConfigFactory(false);
        ConfigFactory factory2 = new TestConfigFactory(false);
        ConfigFactory factory3 = new TestConfigFactory(false);
        Set<ConfigFactory> factories = new HashSet<>(3);
        factories.add(factory1);
        factories.add(factory2);
        factories.add(factory3);
        assertEquals(3, factories.size());

        DefaultConfigFactorySelector selector = new DefaultConfigFactorySelector();
        selector.setConfigFactories(factories);

        ConfigFactory result = selector.getFactory(new File("foo.bar"));

        assertNull(result);
    }

    @Test
    @SuppressWarnings("ConstantConditions")
    public void setFactories_null_useEmptySet() {
        // nullable annotation had to be used for optional binding in Guice
        DefaultConfigFactorySelector selector = new DefaultConfigFactorySelector();
        assertTrue(getConfigFactories(selector).isEmpty());

        selector.setConfigFactories(null);

        assertTrue(getConfigFactories(selector).isEmpty());
    }

    static void assertOneOf(Object result, Object... instances) {
        for (Object instance : instances) {
            try {
                assertSame(instance, result);
                return;

            } catch (AssertionError ignore) {
            }
        }

        fail("expected one of:<" + Arrays.toString(instances) + "> but was:<" + result + ">");
    }

    static void assertValidBean(ConfigFactorySelector selector) {
        assertTrue(selector instanceof DefaultConfigFactorySelector);
        assertNull(selector.getFactory(new File("foo.bar")));
        Collection<ConfigFactory> configFactories = getConfigFactories((DefaultConfigFactorySelector) selector);
        //noinspection ConstantConditions
        assertTrue(configFactories.isEmpty());
    }

    @SuppressWarnings("unchecked")
    static Collection<ConfigFactory> getConfigFactories(DefaultConfigFactorySelector selector) {
        try {
            Field listField = DefaultConfigFactorySelector.class.getDeclaredField("configFactories");
            listField.setAccessible(true);
            return (Collection<ConfigFactory>) listField.get(selector);

        } catch (NoSuchFieldException e) {
            fail("field not found: " + e.getMessage());

        } catch (IllegalAccessException e) {
            fail("field was not accessible: " + e.getMessage());
        }

        return null;
    }

    static class TestConfigFactory implements ConfigFactory {
        final boolean supported;

        TestConfigFactory(boolean supported) {
            this.supported = supported;
        }

        @Override
        public boolean supports(@NotNull File source) {
            return supported;
        }

        @NotNull
        @Override
        public Config create(@NotNull File source, @Nullable String profile, @NotNull Config context) throws IOException {
            return new Config();
        }
    }

}
