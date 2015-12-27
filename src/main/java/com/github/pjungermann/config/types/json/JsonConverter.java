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
package com.github.pjungermann.config.types.json;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.KeyBuilder;
import com.github.pjungermann.config.types.ConfigConversionException;
import com.github.pjungermann.config.types.ConfigConverter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;

import static java.util.Collections.synchronizedMap;

/**
 * Converter from JSON to {@link Config} and vise versa.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class JsonConverter implements ConfigConverter<ObjectNode> {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonConverter.class);

    static final ObjectMapper MAPPER;

    static {
        MAPPER = new ObjectMapper();
        MAPPER.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
    }

    private KeyBuilder keyBuilder;

    @Inject
    public void setKeyBuilder(@NotNull final KeyBuilder keyBuilder) {
        this.keyBuilder = keyBuilder;
    }

    @NotNull
    @Override
    public Config from(@NotNull final ObjectNode convertible) {
        final Config config = new Config();
        putAll(synchronizedMap(config), convertible, "");

        return config;
    }

    @SuppressWarnings("unchecked")
    protected void putAll(@NotNull final Map<String, Object> config,
                          @NotNull final ObjectNode convertible,
                          @NotNull final String keyPrefix) {
        final Iterator<Map.Entry<String, JsonNode>> fields = convertible.fields();

        while (fields.hasNext()) {
            final Map.Entry<String, JsonNode> entry = fields.next();
            put(config, keyPrefix + entry.getKey(), entry.getValue());
        }
    }

    @SuppressWarnings("unchecked")
    protected void put(@NotNull final Map<String, Object> config,
                       @NotNull final String key,
                       @NotNull JsonNode node) {
        if (node instanceof ObjectNode) {
            putAll(config, (ObjectNode) node, keyBuilder.toPrefix(key));
            return;
        }

        config.put(key, extractValue(node));
    }

    @Nullable
    protected Object extractValue(@NotNull final JsonNode node) {
        if (node.isNull()) {
            return null;
        }

        if (node.isBoolean()) {
            return node.booleanValue();
        }

        if (node.isNumber()) {
            return node.numberValue();
        }

        if (node.isTextual()) {
            return node.textValue();
        }

        if (node.isBinary()) {
            try {
                return node.binaryValue();

            } catch (IOException e) {
                LOGGER.error(String.format(
                                "failed to read binary value from node %s of type %s",
                                node, node.getClass()
                        ), e);
            }
        }

        if (node instanceof ArrayNode) {
            return prepareEntries((ArrayNode) node);
        }

        throw new UnsupportedOperationException(String.format(
                "node %s of type %s is not supported",
                node, node.getClass()
        ));
    }

    @NotNull
    @SuppressWarnings("unchecked")
    protected ArrayList prepareEntries(@NotNull final ArrayNode valueList) {
        final ArrayList list = new ArrayList(valueList.size());

        for (JsonNode entry : valueList) {
            if (entry instanceof ObjectNode) {
                list.add(from((ObjectNode) entry));

            } else {
                list.add(extractValue(entry));
            }
        }

        return list;
    }

    @NotNull
    @Override
    @SuppressWarnings("unchecked")
    public ObjectNode to(@NotNull final Config config) throws ConfigConversionException {
        final HashMap<String, Object> map = new HashMap<>(config.size());

        new HashSet<>(config.keySet())
                .stream()
                .forEach(key -> addHierarchicalEntry(map, (String) key, config.get(key)));

        try {
            final String jsonString = MAPPER.writeValueAsString(map);
            return (ObjectNode) MAPPER.readTree(jsonString);

        } catch (JsonProcessingException e) {
            throw new ConfigConversionException(
                    "failed to serialize the config as json " +
                            "and deserialize it into a JsonNode",
                    e);

        } catch (IOException e) {
            // unlikely to happen, but part of the interface
            throw new ConfigConversionException(
                    "failed to read from the content", e);
        }
    }

    @SuppressWarnings("unchecked")
    protected void addHierarchicalEntry(@NotNull final Map<String, Object> map,
                                        @NotNull final String key,
                                        final Object value) {
        final int index = key.indexOf(keyBuilder.getSeparator());
        if (index == -1) {
            map.put(key, value);
            return;
        }

        final String rootKey = key.substring(0, index);
        if (!map.containsKey(rootKey)) {
            map.putIfAbsent(rootKey, new HashMap<>());
        }

        addHierarchicalEntry(
                (Map<String, Object>) map.get(rootKey), 
                key.substring(index + 1), 
                value);
    }

}
