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
package com.github.pjungermann.config.specification.constraint.inetAddress;

import com.github.pjungermann.config.specification.constraint.Constraint;
import com.github.pjungermann.config.specification.constraint.GenericConstraintTest;

import java.nio.CharBuffer;
import java.util.Arrays;

import static java.util.Collections.singletonList;

/**
 * Tests for {@link InetAddressConstraint}.
 *
 * As {@link InetAddressConstraint} uses {@link org.apache.commons.validator.routines.InetAddressValidator}
 * under the hood, some test cases got copied from its test class {@code InetAddressValidatorTest}.
 *
 * @author patrick.jungermann
 * @since 2016-07-20
 */
@SuppressWarnings("PMD.AvoidUsingHardCodedIP")
public class InetAddressConstraintTest extends GenericConstraintTest<InetAddressConstraint> {

    /**
     * @return the {@link Constraint} under test.
     */
    @Override
    protected Class<InetAddressConstraint> getConstraintClass() {
        return InetAddressConstraint.class;
    }

    /**
     * @return whether the {@link Constraint} is supposed to skip / ignore null values
     */
    @Override
    protected boolean skipsNullValues() {
        return true;
    }

    /**
     * @return whether the {@link Constraint} is supposed to skip / ignore blank values
     */
    @Override
    protected boolean skipsBlankValues() {
        return true;
    }

    /**
     * @return types supported for the values.
     */
    @Override
    protected Class[] supportedTypes() {
        return new Class[]{
                CharSequence.class,
                String.class,
                StringBuffer.class,
                StringBuilder.class,
                CharBuffer.class
        };
    }

    /**
     * @return types not supported for the values.
     */
    @Override
    protected Class[] unsupportedTypes() {
        return new Class[]{
                Object.class,
                Integer.class,
                Long.class
        };
    }

    /**
     * @return expectation configs which are not valid for this {@link Constraint}.
     */
    @Override
    protected Object[] getInvalidExpectationConfigs() {
        return new Object[]{
                null,
                new Object(),
                123
        };
    }

    /**
     * Sets up the test data
     */
    @Override
    protected void testDataSetUp() {
        // IPv4
        Object[] validIPv4 = new Object[]{
                "0.0.0.0",
                "12.34.56.78",
                "255.255.255.255",
                // the following test cases are take from InetAddressValidatorTest
                "24.25.231.12",
                "135.14.44.12",
                "213.25.224.32",
                "229.35.159.6",
                "248.85.24.92"
        };
        Object[] invalidIPv4 = new Object[]{
                "255.255.255.256",
                "255.255.256.255",
                "255.256.255.255",
                "256.255.255.255",
                "012.34.56.78",
                // the following test cases are take from InetAddressValidatorTest
                "2.41.32.324",
                "154.123.441.123",
                "201.543.23.11",
                "231.54.11.987",
                "250.21.323.48",
                "124.14.32.abc",
                "23.64.12",
                "26.34.23.77.234"
        };
        // IPv6
        Object[] validIPv6 = new Object[]{
                // the following test cases are take from InetAddressValidatorTest
                "2001:0438:FFFE:0000:0000:0000:0000:0A35",
                "::1", // loopback, compressed
                "0:0:0:0:0:0:0:1", // loopback, full
                "::", // unspecified, compressed
                "0:0:0:0:0:0:0:0", // unspecified, full
                "2001:DB8::8:800:200C:417A", // unicast, compressed
                "2001:DB8:0:0:8:800:200C:417A", // unicast, full
                "FF01::101", // multicast, compressed
                "FF01:0:0:0:0:0:0:101", // multicast, full
                "fe80::217:f2ff:fe07:ed62",
                "2001:0000:1234:0000:0000:C1C0:ABCD:0876",
                "3ffe:0b00:0000:0000:0001:0000:0000:000a",
                "FF02:0000:0000:0000:0000:0000:0000:0001",
                "0000:0000:0000:0000:0000:0000:0000:0001",
                "0000:0000:0000:0000:0000:0000:0000:0000",
                "2::10",
                "ff02::1",
                "fe80::",
                "2002::",
                "2001:db8::",
                "2001:0db8:1234::",
                "::ffff:0:0",
                "1:2:3:4:5:6:7:8",
                "1:2:3:4:5:6::8",
                "1:2:3:4:5::8",
                "1:2:3:4::8",
                "1:2:3::8",
                "1:2::8",
                "1::8",
                "1::",
                "::8",
                // IPv4 as dotted quads
                "1:2:3:4:5:6:1.2.3.4",
                "1:2:3:4:5::1.2.3.4",
                "1::1.2.3.4",
        };
        Object[] invalidIPv6 = new Object[]{
                // the following test cases are take from InetAddressValidatorTest
                "02001:0000:1234:0000:0000:C1C0:ABCD:0876", // extra 0 not allowed
                "2001:0000:1234:0000:00001:C1C0:ABCD:0876", // extra 0 not allowed,
                "2001:0000:1234:0000:0000:C1C0:ABCD:0876 0", // junk afterwards
                "2001:0000:1234: 0000:0000:C1C0:ABCD:0876", // space
                "3ffe:0b00:0000:0001:0000:0000:000a", // 7 segments
                "2001:DB8:0:0:8:800:200C:417A:221", // 9 segments
                "FF02:0000:0000:0000:0000:0000:0000:0000:0001", // 9 segments
                "FF01::101::2", // only 1 compressed part allowed
                "3ffe:b00::1::a", // only 1 compressed part allowed
                "::1111:2222:3333:4444:5555:6666::", // only 1 compressed part allowed
                "1::255.255.255.256",
                ":",
                ":::"
        };

        with(false)
                .valid("anything")
                .valid(validIPv4)
                .valid(invalidIPv4)
                .valid(validIPv6)
                .valid(invalidIPv6)
                .buildAndAdd();

        with(singletonList("IPv4"))
                .valid(validIPv4)
                .invalid(invalidIPv4)
                .invalid(validIPv6)
                .invalid(invalidIPv6)
                .buildAndAdd();

        with(singletonList("IPv6"))
                .invalid(validIPv4)
                .invalid(invalidIPv4)
                .valid(validIPv6)
                .invalid(invalidIPv6)
                .buildAndAdd();

        with(Arrays.asList("IPv4", "IPv6"))
                .valid(validIPv4)
                .invalid(invalidIPv4)
                .valid(validIPv6)
                .invalid(invalidIPv6)
                .buildAndAdd();

        with(true)
                .valid(validIPv4)
                .invalid(invalidIPv4)
                .valid(validIPv6)
                .invalid(invalidIPv6)
                .buildAndAdd();
    }
}
