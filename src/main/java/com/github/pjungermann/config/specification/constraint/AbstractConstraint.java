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
package com.github.pjungermann.config.specification.constraint;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.ConfigError;
import com.github.pjungermann.config.reference.SourceLine;
import com.github.pjungermann.config.specification.constraint.multi.*;
import org.codehaus.groovy.runtime.RangeInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.support.DefaultMessageSourceResolvable;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;

/**
 * Base implementation for {@link Constraint constraints}.
 *
 * @author Patrick Jungermann
 */
public abstract class AbstractConstraint implements Constraint {

    public static final String DEFAULT_MESSAGE_CODE = "constraints.invalid.default.message";

    protected final String key;
    protected final Object expectation;
    protected final SourceLine sourceLine;

    /**
     * @param key            The key for which this {@link Constraint} gets defined for.
     * @param expectation    The expectation which needs to be fulfilled by the config key's value.
     * @param sourceLine     The {@link SourceLine} at which this expectation got expressed at.
     */
    public AbstractConstraint(@NotNull final String key,
                              @Nullable final Object expectation,
                              @NotNull final SourceLine sourceLine) {
        this.key = key;
        this.expectation = expectation;
        this.sourceLine = sourceLine;
    }

    /**
     * @return whether the expectation is a valid one.
     */
    protected abstract boolean isValidExpectation();

    /**
     * Validates the value against the {@link #expectation}.
     * Prior to this, the expectation got validated itself
     * and some base checks are already done.
     *
     * @param value    The value which has to be validated against the expectation.
     * @return a {@link ConfigError} if the value was invalid, {@code null} otherwise.
     * @see #validate(Config)
     * @see #isValidExpectation()
     * @see #skipNullValues()
     * @see #skipBlankValues()
     */
    @Nullable
    protected abstract ConfigError doValidate(final Object value);

    /**
     * Validates the value against the {@link #expectation}.
     * Prior to this, the expectation got validated itself
     * and some base checks are already done.
     *
     * @param config    The config containing the to be validated keys and values.
     * @param value     The value which has to be validated against the expectation.
     * @return a {@link ConfigError} if the value was invalid, {@code null} otherwise.
     * @see #validate(Config)
     * @see #isValidExpectation()
     * @see #skipNullValues()
     * @see #skipBlankValues()
     */
    @Nullable
    protected ConfigError doValidate(final Config config, final Object value) {
        return doValidate(value);
    }

    @NotNull
    @Override
    public SourceLine definedAt() {
        return sourceLine;
    }

    @NotNull
    @Override
    public String getKey() {
        return key;
    }

    @Nullable
    @Override
    public ConfigError validate(@NotNull final Config config) {
        return validate(config, key);
    }

    @Nullable
    protected ConfigError validate(@NotNull final Config config, @NotNull final String key) {
        if (!isValidExpectation()) {
            return new InvalidConstraintConfigError(this, expectation);
        }

        CollectionKey collectionKey = CollectionKeyBuilder.build(key);
        if (collectionKey != null) {
            return validateCollection(config, collectionKey);
        }

        return validateValue(config, config.get(key));
    }

    @Nullable
    protected ConfigError validateCollection(@NotNull final Config config, @NotNull final CollectionKey key) {
        final Object collectionObject = config.get(key.collectionKey);
        if (collectionObject == null && skipNullValues()) {
            return null;
        }

        if (!(collectionObject instanceof Collection)) {
            return new NoCollectionError(key, collectionObject);
        }

        final Collection collection = (Collection) config.get(key.collectionKey);
        if (collection.isEmpty()) {
            return null;
        }

        final Object[] array = collection.toArray();

        RangeInfo rangeInfo = key.entrySelection.subListBorders(array.length);
        int from = rangeInfo.from;
        int to = rangeInfo.to;

        // adjust the collection size
        // TODO: use strict mode to create errors here as well? could also be covered by specifying the size
        if (array.length - 1 < from) {
            // no entry to check
            return null;
        }
        if (array.length < to) {
            to = array.length;
        }

        final ArrayList<ConfigError> errors = new ArrayList<>();
        for (int i = from; i < to; i++) {
            ConfigError error;
            Object entry = array[i];
            if (key.propertyKey == null) {
                error = validateValue(config, entry);

            } else if (entry instanceof Config) {
                error = validate(config, key.propertyKey);

            } else if (entry instanceof Map) {
                error = validateValue(config, ((Map) entry).get(key.propertyKey));

            } else {
                error = validateObjectProperty(config, key, entry, key.propertyKey);
            }

            if (error != null) {
                errors.add(error);
            }
        }

        if (errors.isEmpty()) {
            return null;
        }

        return new MultiConfigError(key, errors);
    }

    @Nullable
    protected ConfigError validateObjectProperty(@NotNull final Config config,
                                                 @NotNull final CollectionKey key,
                                                 @NotNull final Object object,
                                                 @NotNull final String property) {
        Method getter = getGetter(object, property);
        if (getter != null) {
            try {
                return validateValue(config, getter.invoke(object));

            } catch (IllegalAccessException | InvocationTargetException e) {
                return new PropertyGetterAccessFailedError(key, object, getter.getName());
            }
        }

        Field field = getField(object, property);
        if (field != null) {
            try {
                return validateValue(config, field.get(object));

            } catch (IllegalAccessException e) {
                return new PropertyFieldAccessFailedError(key, object, field.getName());
            }
        }

        return new UnsupportedCollectionEntryPropertyError(key, object);
    }

    @Nullable
    protected Field getField(Object object, String name) {
        try {
            return object.getClass().getField(name);

        } catch (NoSuchFieldException e) {
            return null;
        }
    }

    protected Method getGetter(Object object, String property) {
        String getterName = "get" +
                property.substring(0, 1).toUpperCase(Locale.ENGLISH) + property.substring(1);
        Method getter;
        try {
             getter = object.getClass().getMethod(getterName);

        } catch (NoSuchMethodException e) {
            getter = null;
        }

        if (getter != null) {
            return getter;
        }

        for (Method method: object.getClass().getMethods()) {
            if (method.getName().equalsIgnoreCase("get" + property)) {
                return method;
            }
        }

        return null;
    }

    @Nullable
    protected ConfigError validateValue(@NotNull final Config config, @Nullable final Object value) {
        if (skipNullValues() && value == null) {
            return null;
        }

        if (skipBlankValues()
                && value instanceof CharSequence
                && value.toString().trim().isEmpty()) {
            return null;
        }

        if (value != null && !supports(value.getClass())) {
            return new InvalidConfigValueTypeError(this, value);
        }

        return doValidate(config, value);
    }

    @NotNull
    @Override
    public MessageSourceResolvable getMessage(@Nullable final Object value) {
        final String code = getMessageCode();

        return new DefaultMessageSourceResolvable(
                new String[]{
                        code,
                        DEFAULT_MESSAGE_CODE
                },
                new Object[]{
                        sourceLine,
                        key,
                        value,
                        expectation,
                        getName()
                },
                code
        );
    }

    @NotNull
    protected String getMessageCode() {
        return "constraints.invalid." + getName() + ".message";
    }

    protected boolean skipNullValues() {
        // a null is not a value we should even check in most cases
        return true;
    }

    protected boolean skipBlankValues() {
        // most constraints ignore blank values, leaving it to the explicit "blank" constraint.
        return true;
    }

    /**
     * Creates a simple {@link ConfigConstraintError} for the value.
     *
     * @param value    the invalid value.
     * @return a {@link ConfigConstraintError} for the value.
     */
    @NotNull
    protected ConfigError violatedBy(final Object value) {
        return new ConfigConstraintError(this, value);
    }

    @Override
    public String toString() {
        return Constraint.class.getSimpleName() + ": "
                + key + "(" + getName() + ": " + expectation + ") "
                + "[" + sourceLine + "]";
    }

    @Override
    public int compareTo(@NotNull final Constraint o) {
        final int byKey = key.compareTo(o.getKey());
        if (byKey != 0) return byKey;

        final int byName = getName().compareTo(o.getName());
        if (byName != 0) return byName;

        return sourceLine.compareTo(o.definedAt());
    }
}
