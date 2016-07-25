package com.github.pjungermann.config.specification.types;

import com.github.pjungermann.config.Config;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * Tests for {@link AsTypeConverter}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class AsTypeConverterTest {

    AsTypeConverter converter;

    @Before
    public void setUp() {
        converter = new AsTypeConverter();
    }

    @Test
    public void register_always_storeConversion() {
        converter.register("fake-key", Double.class);

        assertTrue(converter.keyAsTypeMapping.containsKey("fake-key"));
        assertEquals(Double.class, converter.keyAsTypeMapping.get("fake-key"));
    }

    @Test
    public void register_existingEntry_overrideExistingEntries() {
        converter.register("fake-key", Double.class);
        converter.register("fake-key", Long.class);

        assertTrue(converter.keyAsTypeMapping.containsKey("fake-key"));
        assertEquals(Long.class, converter.keyAsTypeMapping.get("fake-key"));
    }

    @Test
    public void getKeys_always_returnKeysWithConversion() {
        converter.register("fake-key1", Double.class);
        converter.register("fake-key2", Long.class);

        Set<String> keys = converter.getKeys();

        assertEquals(2, keys.size());
        assertTrue(keys.contains("fake-key1"));
        assertTrue(keys.contains("fake-key2"));
    }

    @Test
    public void isConversionCommand_valid_returnTrue() {
        assertTrue(converter.isConversionCommand("as"));
    }

    @Test
    public void isConversionCommand_invalidCase_returnFalse() {
        assertFalse(converter.isConversionCommand("AS"));
        assertFalse(converter.isConversionCommand("As"));
    }

    @Test
    public void isConversionCommand_wrongCommand_returnFalse() {
        assertFalse(converter.isConversionCommand("convertTo"));
    }

    @Test
    public void isValidConversionConfig_always_onlyAcceptNonNullClass() {
        assertTrue(converter.isValidConversionConfig(Object.class));
        assertTrue(converter.isValidConversionConfig(Double.class));
        assertTrue(converter.isValidConversionConfig(String.class));

        assertFalse(converter.isValidConversionConfig(null));
        assertFalse(converter.isValidConversionConfig(new Object()));
        assertFalse(converter.isValidConversionConfig(123));
        assertFalse(converter.isValidConversionConfig("invalid"));
    }

    @Test
    public void convert() {
        converter.register("no-entry", Double.class);
        converter.register("exists.convertable.double.1", Double.class);
        converter.register("exists.convertable.double.2", Double.class);
        converter.register("exists.convertable.long.1", Long.class);
        converter.register("exists.convertable.long.2", Long.class);
        converter.register("exists.convertable.set.list", Set.class);
        converter.register("exists.convertable.set.string", Set.class);
        converter.register("exists.convertable.string.double", String.class);
        converter.register("exists.convertable.array.list", Object[].class);
        converter.register("exists.invalid.double", Double.class);
        converter.register("exists.invalid.long", Long.class);
        converter.register("exists.invalid.set", Set.class);

        Config config = new Config();
        config.put("exists.convertable.double.1", "23.4");
        config.put("exists.convertable.double.2", "42");
        config.put("exists.convertable.long.1", "42");
        config.put("exists.convertable.long.2", "23.4");
        config.put("exists.convertable.set.list", Arrays.asList(1, 2, 3));
        config.put("exists.convertable.set.string", "string");
        config.put("exists.convertable.string.double", 12.34D);
        config.put("exists.convertable.array.list", Arrays.asList(1, 2, 3));
        config.put("exists.invalid.double", "invalid");
        config.put("exists.invalid.long", "invalid");

        assertTrue(config.errors.isEmpty());
        converter.convert(config);

        assertTrue(config.get("exists.convertable.double.1") instanceof Double);
        assertEquals(23.4D, config.get("exists.convertable.double.1"));
        assertTrue(config.get("exists.convertable.double.2") instanceof Double);
        assertEquals(42D, config.get("exists.convertable.double.2"));
        assertTrue(config.get("exists.convertable.long.1") instanceof Long);
        assertEquals(42L, config.get("exists.convertable.long.1"));
        assertTrue(config.get("exists.convertable.long.2") instanceof Long);
        assertEquals(23L, config.get("exists.convertable.long.2"));
        assertTrue(config.get("exists.convertable.set.list") instanceof Set);
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(new HashSet<>(Arrays.asList(1, 2, 3)), config.get("exists.convertable.set.list"));
        assertTrue(config.get("exists.convertable.set.string") instanceof Set);
        //noinspection AssertEqualsBetweenInconvertibleTypes
        assertEquals(new HashSet<>(Arrays.asList("s", "t", "r", "i", "n", "g")), config.get("exists.convertable.set.string"));
        assertTrue(config.get("exists.convertable.string.double") instanceof String);
        assertEquals("12.34", config.get("exists.convertable.string.double"));
        assertTrue(config.get("exists.convertable.array.list") instanceof Object[]);
        assertArrayEquals(new Object[]{1, 2, 3}, (Object[]) config.get("exists.convertable.array.list"));
        assertFalse(config.get("exists.invalid.double") instanceof Double);
        assertFalse(config.get("exists.invalid.long") instanceof Long);

        assertEquals(2, config.errors.size());
        assertEquals(
                "TypeConversionFailedError(key=exists.invalid.double, value=invalid, value.class=class java.lang.String, type=class java.lang.Double)",
                config.errors.get(0).toString());
        assertEquals(
                "TypeConversionFailedError(key=exists.invalid.long, value=invalid, value.class=class java.lang.String, type=class java.lang.Long)",
                config.errors.get(1).toString());
    }
}
