/*
 * Copyright 2016 Patrick Jungermann
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

import java.io.File;
import java.nio.file.Path;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Resolves all files ({@link File#isFile() of type file}) for a {@link File} as a {@link Stream}.
 * If the recursive option was chosen and the {@link File}
 * is a directory, all its contained {@link File files}
 * will get returned.
 *
 * @author Patrick Jungermann
 */
public class FilesResolver implements Function<File, Stream<File>> {

    private final boolean recursive;

    public FilesResolver(final boolean recursive) {
        this.recursive = recursive;
    }

    @Override
    public Stream<File> apply(final File file) {
        if (file.isFile()) {
            return Stream.of(file);
        }

        return Stream.of(file)
                .map(File::toPath)
                .flatMap(recursive ? FileUtils::filesInDir : FileUtils::listFiles)
                .map(Path::toFile)
                .filter(File::isFile);
    }
}
