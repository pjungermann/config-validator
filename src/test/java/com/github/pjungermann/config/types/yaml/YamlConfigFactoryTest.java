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

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.DefaultKeyBuilder;
import com.github.pjungermann.config.types.BaseConfigFactoryTest;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Date;

/**
 * Tests for {@link YamlConfigFactory}.
 *
 * @author Patrick Jungermann
 */
public class YamlConfigFactoryTest extends BaseConfigFactoryTest<YamlConfigFactory> {

    @Override
    public String[] getSupportedTypes() {
        return new String[]{"yaml", "yml"};
    }

    @Override
    public YamlConfigFactory createFactory() throws Exception {
        YamlConverter converter = new YamlConverter();
        converter.setKeyBuilder(new DefaultKeyBuilder());

        YamlConfigFactory factory = new YamlConfigFactory();
        factory.setConverter(converter);

        return factory;
    }

    @Override
    public String getConfigFile() {
        return "sub-dir/config.yaml";
    }

    @Override
    public void validateConfig(final Config config, final String profile, final Config context) throws IOException {
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

}
