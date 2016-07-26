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
package com.github.pjungermann.config.types.yaml;

import com.github.pjungermann.config.CollectedAssertions;
import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import org.junit.Before;
import org.junit.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Tests for {@link YamlConverter}.
 *
 * @author Patrick Jungermann
 */
public class YamlConverterTest extends CollectedAssertions {

    public final File configSource = new File("src/test/resources/configs/sub-dir/config.yaml");

    public YamlConverter converter;

    @Before
    public void setUp() {
        converter = new YamlConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());
    }

    @Test
    @SuppressWarnings("unchecked")
    public void from_always_correctConfig() throws IOException {
        try (
                FileInputStream stream = new FileInputStream(configSource);
                InputStreamReader reader = new InputStreamReader(stream, UTF_8)
        ) {
            LinkedHashMap<String, Object> yaml = (LinkedHashMap<String, Object>) new Yaml().load(reader);

            Config config = converter.from(yaml);

            validateConfig(config);
        }
    }

    public void validateConfig(final Config config) throws IOException {
        Date date = Date.from(
                OffsetDateTime.of(
                        LocalDate.of(2001, 1, 23),
                        LocalTime.MIDNIGHT,
                        ZoneOffset.ofHours(0)
                ).toInstant());

        assertEquals(25, config.size());

        assertEquals(34843, config.get("invoice"));
        assertEquals(date, config.get("date"));
        assertEquals(4443.52D, config.get("total"));
        assertEquals(251.42D, config.get("tax"));
        assertEquals(
                "Late afternoon is best. Backup contact is Nancy Billsmer @ 338-4338.\n",
                config.get("comments"));

        assertEquals(
                "458 Walkman Dr.\nSuite #292\n",
                config.get("ship-to.address.lines"));
        assertEquals(48046, config.get("ship-to.address.postal"));
        assertEquals("Royal Oak", config.get("ship-to.address.city"));
        assertEquals("MI", config.get("ship-to.address.state"));
        assertEquals("Chris", config.get("ship-to.given"));
        assertEquals("Dumars", config.get("ship-to.family"));

        assertEquals(
                "458 Walkman Dr.\nSuite #292\n",
                config.get("bill-to.address.lines"));
        assertEquals(48046, config.get("bill-to.address.postal"));
        assertEquals("Royal Oak", config.get("bill-to.address.city"));
        assertEquals("MI", config.get("bill-to.address.state"));
        assertEquals("Chris", config.get("bill-to.given"));
        assertEquals("Dumars", config.get("bill-to.family"));

        assertEquals("BL394D", config.get("product.0.sku"));
        assertEquals("Basketball", config.get("product.0.description"));
        assertEquals(4, config.get("product.0.quantity"));
        assertEquals(450D, config.get("product.0.price"));

        assertEquals("BL4438H", config.get("product.1.sku"));
        assertEquals("Super Hoop", config.get("product.1.description"));
        assertEquals(1, config.get("product.1.quantity"));
        assertEquals(2392D, config.get("product.1.price"));
    }

    @Test
    public void to_always_convertConfigIntoLinkedHashMap() {
        Config subConfig = new Config();
        subConfig.put("entry", "sub-value");

        Config config = new Config();
        config.put("sub", subConfig);
        config.put("list", Arrays.asList(1, 2, 3));

        LinkedHashMap<String, Object> result = converter.to(config);

        assertEquals(2, result.size());
        assertEquals(Arrays.asList(1, 2, 3), result.get("list"));
        Object subValue = result.get("sub");
        assertTrue(subValue instanceof Config);
        errors.checkSucceeds(() -> {
            //noinspection ConstantConditions
            assertEquals("sub-value", ((Config) subValue).get("entry"));
            return null;
        });
    }

}
