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
package com.github.pjungermann.config.utils;

import org.junit.Test;

import java.io.File;

import static org.junit.Assert.*;

/**
 * Tests for {@link FileUtils}.
 *
 * @author Patrick Jungermann
 */
public class FileUtilsTest {

    @Test
    public void getType_filenameWithoutExtension_returnFilenameItself() {
        String result = FileUtils.getType("filename");

        assertEquals("filename", result);
    }

    @Test
    public void getType_filenameEndsOnDot_returnFilenameItself() {
        String result = FileUtils.getType("filename.");

        assertEquals("filename.", result);
    }

    @Test
    public void getType_filenameEndsOnDotWithAnotherDot_returnSubstringStartingAfterThe2ndLastDot() {
        String result = FileUtils.getType("filename.extension.");

        assertEquals("extension.", result);
    }

    @Test
    public void getType_filenameWithExtension_returnExtension() {
        String result = FileUtils.getType("filename.extension");

        assertEquals("extension", result);
    }

    @Test
    public void getType_filenameWithExtensionWhichIsNotLowerCaseOnly_returnExtensionInLowerCase() {
        String result = FileUtils.getType("filename.EXTension");

        assertEquals("extension", result);
    }

    @Test
    public void getType_filenameWithMultipleDots_returnPartAfterLastDot() {
        String result = FileUtils.getType("file.name.with.extension");

        assertEquals("extension", result);
    }

    @Test
    public void getType_fileWithoutExtension_returnFilenameItself() {
        String result = FileUtils.getType(new File("path/filename"));

        assertEquals("filename", result);
    }

    @Test
    public void getType_fileEndsOnDot_returnFilenameItself() {
        String result = FileUtils.getType(new File("path/filename."));

        assertEquals("filename.", result);
    }

    @Test
    public void getType_fileEndsOnDotWithAnotherDot_returnSubstringStartingAfterThe2ndLastDot() {
        String result = FileUtils.getType(new File("path/filename.extension."));

        assertEquals("extension.", result);
    }

    @Test
    public void getType_fileWithExtension_returnExtension() {
        String result = FileUtils.getType(new File("path/filename.extension"));

        assertEquals("extension", result);
    }

    @Test
    public void getType_fileWithExtensionWhichIsNotLowerCaseOnly_returnExtensionInLowerCase() {
        String result = FileUtils.getType(new File("path/filename.EXTension"));

        assertEquals("extension", result);
    }

    @Test
    public void getType_fileWithMultipleDots_returnPartAfterLastDot() {
        String result = FileUtils.getType(new File("path/file.name.with.extension"));

        assertEquals("extension", result);
    }

    @Test
    public void isOfType_singleTypeAndNoMatch_returnFalse() {
        assertFalse(FileUtils.isOfType(new File("no.match"), "ext"));
    }

    @Test
    public void isOfType_singleTypeAndMatch_returnTrue() {
        assertTrue(FileUtils.isOfType(new File("file.with.ext"), "ext"));
    }

    @Test
    public void isOfType_multipleTypesAndNoMatch_returnFalse() {
        assertFalse(FileUtils.isOfType(new File("no.match"), "ext1", "ext2", "ext3"));
    }

    @Test
    public void isOfType_multipleTypesAndFirstTypeMatches_returnTrue() {
        assertTrue(FileUtils.isOfType(new File("file.with.ext1"), "ext1", "ext2", "ext3"));
    }

    @Test
    public void isOfType_multipleTypesAndLastTypeMatches_returnTrue() {
        assertTrue(FileUtils.isOfType(new File("file.with.ext3"), "ext1", "ext2", "ext3"));
    }

}
