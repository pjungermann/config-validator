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
package com.github.pjungermann.config.utils;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link NameUtils}.
 *
 * @author Patrick Jungermann
 */
public class NameUtilsTest {

    @Test
    public void getNaturalName_always_returnNaturalNameForSimpleNameOfClass() {
        assertEquals("Name Utils", NameUtils.getNaturalName(NameUtils.class));
    }

    @Test
    public void getNaturalName_classAndNoTrailingName_returnNaturalNameForSimpleNameOfClass() {
        assertEquals("Name Utils", NameUtils.getNaturalName(NameUtils.class, null));
    }

    @Test
    public void getNaturalName_classAndTrailingName_returnNaturalNameForSimpleNameOfClassWithoutTrailingName() {
        assertEquals("Name", NameUtils.getNaturalName(NameUtils.class, "Utils"));
    }

    @Test
    public void getNaturalName_classAndNonExistingTrailingName_returnNaturalNameForSimpleNameOfClass() {
        assertEquals("Name Utils", NameUtils.getNaturalName(NameUtils.class, "foo"));
    }

    @Test
    public void getNaturalName_classAndTrailingNameUnrelatedToCaseChange_returnNaturalNameForSimpleNameOfClass() {
        assertEquals("Name Util", NameUtils.getNaturalName(NameUtils.class, "s"));
    }

    @Test
    public void getNaturalName_emptyString_returnEmptyString() {
        assertEquals("", NameUtils.getNaturalName(""));
    }

    @Test
    public void getNaturalName_camelCaseName_returnNameSplitByEachWord() {
        assertEquals("My Fake Name", NameUtils.getNaturalName("MyFakeName"));
    }

    @Test
    public void getNaturalName_upperCaseOnly_returnNoSplitWithoutCaseChange() {
        assertEquals("UPPER", NameUtils.getNaturalName("UPPER"));
    }

    @Test
    public void getNaturalName_firstWordUpperCaseOnly_returnNoSplitWithoutCaseChange() {
        assertEquals("UPPER Lower", NameUtils.getNaturalName("UPPERLower"));
    }

    @Test
    public void getPropertyName_always_returnPropertyNameForSimpleNameOfClass() {
        assertEquals("nameUtils", NameUtils.getPropertyName(NameUtils.class));
    }

    @Test
    public void getPropertyName_classAndNoTrailingName_returnPropertyNameForSimpleNameOfClass() {
        assertEquals("nameUtils", NameUtils.getPropertyName(NameUtils.class, null));
    }

    @Test
    public void getPropertyName_classAndTrailingName_returnPropertyNameForSimpleNameOfClassWithoutTrailingName() {
        assertEquals("name", NameUtils.getPropertyName(NameUtils.class, "Utils"));
    }

    @Test
    public void getPropertyName_classAndNonExistingTrailingName_returnPropertyNameForSimpleNameOfClass() {
        assertEquals("nameUtils", NameUtils.getPropertyName(NameUtils.class, "foo"));
    }

    @Test
    public void getPropertyName_classAndTrailingNameUnrelatedToCaseChange_returnPropertyNameForSimpleNameOfClass() {
        assertEquals("nameUtil", NameUtils.getPropertyName(NameUtils.class, "s"));
    }

    @Test
    public void getPropertyName_emptyString_returnEmptyString() {
        assertEquals("", NameUtils.getPropertyName(""));
    }

    @Test
    public void getPropertyName_camelCaseName_returnPropertyNameVersionStartingWithLowerCase() {
        assertEquals("myFakeName", NameUtils.getPropertyName("MyFakeName"));
    }

    @Test
    public void getPropertyName_camelCaseNameStartingWithLowerCase_returnWithoutChange() {
        assertEquals("myFakeName", NameUtils.getPropertyName("myFakeName"));
    }

    @Test
    public void getPropertyName_upperCaseOnly_returnAllLowerCase() {
        assertEquals("upper", NameUtils.getPropertyName("UPPER"));
    }

    @Test
    public void getPropertyName_firstWordUpperCaseOnly_returnFirstWordAllLowerCase() {
        assertEquals("upperLower", NameUtils.getPropertyName("UPPERLower"));
    }
}
