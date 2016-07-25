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
package com.github.pjungermann.config.types.json;

import com.fasterxml.jackson.databind.node.*;
import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import com.github.pjungermann.config.types.ConfigConversionException;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link JsonConverter}.
 *
 * @author Patrick Jungermann
 */
public class JsonConverterTest {

    JsonConverter converter;

    @Before
    public void setUp() {
        converter = new JsonConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());
    }

    @Test
    public void from_hierarchicalJsonObject_convertToConfig() {
        ObjectNode level2 = new ObjectNode(JsonNodeFactory.instance);
        level2.set("an", new TextNode("entry"));

        ObjectNode level1 = new ObjectNode(JsonNodeFactory.instance);
        level1.set("level_2", level2);
        level1.set("another", new TextNode("value"));

        ArrayNode arrayNode = new ArrayNode(JsonNodeFactory.instance);
        arrayNode.add(1);
        arrayNode.add(2);
        arrayNode.add(3);

        ObjectNode json = new ObjectNode(JsonNodeFactory.instance);
        json.set("level_1", level1);
        json.set("boolean_true", BooleanNode.getTrue());
        json.set("boolean_false", BooleanNode.getFalse());
        json.set("number", new IntNode(123456));
        json.set("string", new TextNode("string value"));
        json.set("list", arrayNode);

        Config config = converter.from(json);

        assertEquals(true, config.get("boolean_true"));
        assertEquals(false, config.get("boolean_false"));
        assertEquals(123456, config.get("number"));
        assertEquals("string value", config.get("string"));
        assertEquals("entry", config.get("level_1.level_2.an"));
        assertEquals("value", config.get("level_1.another"));
        List list = (List) config.get("list");
        assertEquals(1, list.get(0));
        assertEquals(2, list.get(1));
        assertEquals(3, list.get(2));
    }

    @Test
    @SuppressWarnings({"unchecked", "RedundantCast"})
    public void to_flatConfig_hierarchicalJsonObject() throws ConfigConversionException {
        Config config = new Config();
        config.put("boolean_true", true);
        config.put("boolean_false", false);
        config.put("number", 123456);
        config.put("string", "string value");
        config.put("level_1.level_2.an", "entry");
        config.put("level_1.another", "value");
        config.put("list", Arrays.asList(1, 2, 3));

        ObjectNode json = converter.to(config);

        assertEquals(true, ((BooleanNode) json.get("boolean_true")).booleanValue());
        assertEquals(false, ((BooleanNode) json.get("boolean_false")).booleanValue());
        assertEquals(123456, ((IntNode) json.get("number")).intValue());
        assertEquals("string value", ((TextNode) json.get("string")).textValue());
        ObjectNode level1 = (ObjectNode) json.get("level_1");
        ObjectNode level2 = (ObjectNode) level1.get("level_2");
        assertEquals("value", ((TextNode) level1.get("another")).textValue());
        assertEquals("entry", ((TextNode) level2.get("an")).textValue());
        ArrayNode list = (ArrayNode) json.get("list");
        assertEquals(1, ((IntNode) list.get(0)).intValue());
        assertEquals(2, ((IntNode) list.get(1)).intValue());
        assertEquals(3, ((IntNode) list.get(2)).intValue());
    }

}
