package com.github.pjungermann.config.utils;

import org.junit.Test;

import java.io.File;
import java.util.List;
import java.util.stream.Stream;

import static com.github.pjungermann.config.OSUtils.toOSPath;
import static java.util.stream.Collectors.toList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Tests for {@link FilesResolver}.
 *
 * @author patrick.jungermann
 * @since 2016-07-25
 */
public class FilesResolverTest {

    private static final File TEST_DIR = new File("src/test/resources/FileUtilsTest");
    private static final File TEST_FILE = new File("src/test/resources/FileUtilsTest/file1.txt");

    @Test
    public void apply_fileAndNotRecursive_returnFileItself() {
        Stream<File> fileStream = new FilesResolver(false).apply(TEST_FILE);
        List<String> fileList = fileStream
                .map(File::toString)
                .collect(toList());

        assertEquals(1, fileList.size());
    }

    @Test
    public void apply_fileAndRecursive_returnFileItself() {
        Stream<File> fileStream = new FilesResolver(true).apply(TEST_FILE);
        List<String> fileList = fileStream
                .map(File::toString)
                .collect(toList());

        assertEquals(1, fileList.size());
    }

    @Test
    public void apply_dirAndNotRecursive_returnAllFilesFromDir() {
        Stream<File> fileStream = new FilesResolver(false).apply(TEST_DIR);
        List<String> fileList = fileStream
                .map(File::toString)
                .collect(toList());

        assertEquals(2, fileList.size());
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/file1.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/file2.txt")));
    }

    @Test
    public void apply_dirAndRecursive_returnAllFilesFromDirRecursive() {
        Stream<File> fileStream = new FilesResolver(true).apply(TEST_DIR);
        List<String> fileList = fileStream
                .map(File::toString)
                .collect(toList());

        assertEquals(8, fileList.size());
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/file1.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/file2.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/dir1/file3.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/dir1/file4.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/file5.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/file6.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/dir21/file7.txt")));
        assertTrue(fileList.contains(toOSPath("src/test/resources/FileUtilsTest/dir2/dir21/file8.txt")));
    }
}
