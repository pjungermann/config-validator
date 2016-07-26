package com.github.pjungermann.config.specification.constraint.multi;

import groovy.lang.IntRange;
import org.jetbrains.annotations.NotNull;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Builder to create a {@link CollectionKey}
 * from a "meta" key defining rules for a collection's
 * entries.
 *
 * @author patrick.jungermann
 * @since 2016-07-26
 */
public class CollectionKeyBuilder {

    public static final Pattern PATTERN =
            // {collectionKey}.[*]
            // {collectionKey}.[index]
            // {collectionKey}.[from..to]
            // {collectionKey}.[*].{propertyKey}
            // {collectionKey}.[index].{propertyKey}
            // {collectionKey}.[from..to].{propertyKey}
            Pattern.compile("(.*)\\.\\[(?:(\\*)|(\\d+)(?:\\.\\.(\\d+))?)\\](?:\\.(.*))?");

    public static CollectionKey build(@NotNull final String key) {
        final Matcher matcher = PATTERN.matcher(key);
        if (!matcher.matches()) {
            return null;
        }

        final String collectionKey = matcher.group(1);
        final IntRange entrySelection = toRange(
                matcher.group(2),
                matcher.group(3),
                matcher.group(4)
        );
        final String entryPropertyKey = matcher.group(5);

        return new CollectionKey(key, collectionKey, entrySelection, entryPropertyKey);
    }

    public static IntRange toRange(String all, String index1, String index2) {
        if (all != null) {
            return new IntRange(true, 0, -1);
        }

        if (index2 == null) {
            index2 = index1;
        }

        return new IntRange(
                true,
                Integer.parseInt(index1, 10),
                Integer.parseInt(index2, 10));
    }
}
