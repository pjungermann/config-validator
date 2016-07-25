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
package com.github.pjungermann.config.specification.types;

import com.github.pjungermann.config.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.codehaus.groovy.runtime.DefaultGroovyMethods.asType;

/**
 * Can be used similar to constraints by using the "as" label / command
 * and the target type as value within the specification for a config key.
 *
 * Type conversion gets applied <b>before</b> any constraint gets checked
 * and therefore can be used to modify the type beforehand to the validated
 * one.
 *
 * This {@link TypeConverter} implementation mutates the {@link Config}.
 *
 * @author Patrick Jungermann
 */
@Component
public class AsTypeConverter implements TypeConverter {

    public static final String COMMAND = "as";

    protected final ConcurrentHashMap<String, Class> keyAsTypeMapping = new ConcurrentHashMap<>();

    @NotNull
    @Override
    public Set<String> getKeys() {
        return new HashSet<>(keyAsTypeMapping.keySet());
    }

    @Override
    public void register(@NotNull final String key, @NotNull final Class asType) {
        keyAsTypeMapping.put(key, asType);
    }

    @Override
    public boolean isConversionCommand(@NotNull final String name) {
        return COMMAND.equals(name);
    }

    @Override
    public boolean isValidConversionConfig(@Nullable final Object conversionConfig) {
        return conversionConfig != null && conversionConfig instanceof Class;
    }

    @Override
    public void convert(@NotNull final Config config) {
        keyAsTypeMapping.forEach((key, type) -> {
            if (!config.containsKey(key)) {
                return;
            }

            Object value = config.get(key);

            try {
                if (value instanceof CharSequence && Number.class.isAssignableFrom(type)) {
                    value = new BigDecimal(value.toString());
                }
                config.put(key, asType(value, type));

            } catch (NumberFormatException | ClassCastException e) {
                config.errors.add(new TypeConversionFailedError(key, value, type, e));
            }
        });
    }
}
