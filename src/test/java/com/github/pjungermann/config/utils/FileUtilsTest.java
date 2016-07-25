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

import java.io.File;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static com.github.pjungermann.config.OSUtils.toOSPath;
import static java.util.stream.Collectors.toList;
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

    @Test
    public void listFiles_testDir_returnAllTestFilesAndFolderContained() {
        Stream<Path> pathStream = FileUtils.listFiles(new File("src/test/resources/FileUtilsTest").toPath());
        List<String> pathList = pathStream
                .map(Path::toString)
                .collect(toList());

        assertEquals(4, pathList.size());
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir1")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/file1.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/file2.txt")));
    }

    @Test
    public void filesInDir_testDir_returnAllContainedFilesRecursively() {
        Stream<Path> pathStream = FileUtils.filesInDir(new File("src/test/resources/FileUtilsTest").toPath());
        List<String> pathList = pathStream
                .map(Path::toString)
                .collect(toList());

        assertEquals(8, pathList.size());
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/file1.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/file2.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir1/file3.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir1/file4.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/file5.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/file6.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/dir21/file7.txt")));
        assertTrue(pathList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/dir21/file8.txt")));
    }
}
