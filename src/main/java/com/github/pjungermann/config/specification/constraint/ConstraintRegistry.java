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

import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

import javax.inject.Inject;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.util.Collections.emptyMap;

/**
 * Registry for {@link ConstraintFactory} instances, creating {@link Constraint} instances
 * to be used at specifications with a certain configuration.
 *
 * @author Patrick Jungermann
 */
@Component
public class ConstraintRegistry {

    /**
     * Mapping between {@link Constraint} name and {@link ConstraintFactory}.
     */
    private final Map<String, ConstraintFactory> mapping;

    @Inject
    public ConstraintRegistry(final List<ConstraintFactory> constraintFactoryList) {
        if (constraintFactoryList == null) {
            this.mapping = emptyMap();
            return;
        }

        Map<String, ConstraintFactory> mapping = new HashMap<>();
        constraintFactoryList.stream().forEach(constraintFactory ->
                        mapping.put(constraintFactory.getName(), constraintFactory)
        );

        this.mapping = Collections.unmodifiableMap(mapping);
    }

    /**
     * @param name    The {@link Constraint} name to look for.
     * @return the {@link ConstraintFactory} for the provided name.
     * @throws NoSuchConstraintException if no suitable {@link ConstraintFactory} was found.
     */
    @NotNull
    public ConstraintFactory byName(@NotNull final String name) throws NoSuchConstraintException {
        if (mapping.containsKey(name)) {
            return mapping.get(name);
        }

        throw new NoSuchConstraintException(name);
    }
}
