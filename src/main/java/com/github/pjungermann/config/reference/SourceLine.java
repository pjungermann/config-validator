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
package com.github.pjungermann.config.reference;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.Objects;

/**
 * Reference to a specific line of a source file like a specification file.
 *
 * @author Patrick Jungermann
 */
public class SourceLine implements Comparable<SourceLine> {

    /**
     * The referenced source {@link File}.
     */
    public final File file;

    /**
     * The line within the {@link #file}.
     */
    public final int line;

    /**
     * @param file    The referenced source {@link File}.
     * @param line    The line within the {@link #file}
     */
    public SourceLine(@NotNull final File file, final int line) {
        this.file = file;
        this.line = line;
    }

    /**
     * @return string representation, clickable reference for IDEs
     */
    @NotNull
    @Override
    public String toString() {
        // clickable reference for IDEs (i.e. at IntelliJ IDEA)
        return file.toString() + "(" + file.getName() + ":" + line + ")";
    }

    @Override
    public boolean equals(@Nullable Object o) {
        return o != null && (this == o || o instanceof SourceLine && compareTo((SourceLine) o) == 0);
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, line);
    }

    @Override
    public int compareTo(@NotNull final SourceLine o) {
        final int byFile = file.compareTo(o.file);
        if (byFile != 0) return byFile;

        return line < o.line ? -1 : line == o.line ? 0 : 1;
    }
}
