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
package com.github.pjungermann.config.reference;

import org.junit.Test;

import java.io.File;

import static com.github.pjungermann.config.OSUtils.toOSPath;
import static org.junit.Assert.assertEquals;

/**
 * Tests for {@link SourceLine}.
 *
 * @author Patrick Jungermann
 */
public class SourceLineTest {

    @Test
    public void constructor_always_createsInstanceWithProvidedValues() {
        SourceLine sourceLine = new SourceLine(new File("my/file.ext"), 21);

        assertEquals(new File("my/file.ext"), sourceLine.file);
        assertEquals(21, sourceLine.line);
    }

    @Test
    public void toString_always_createsClickableReferenceForIDE() {
        SourceLine sourceLine = new SourceLine(new File("fake/file.ext"), 42);

        // at least in IntelliJ IDEA, references like (file.name:line) are clickable
        // if the config validation gets done inside of an IDE, this can get very handy
        // as with one click you can get to the right line of your specification file
        assertEquals(toOSPath("fake/file.ext(file.ext:42)"), sourceLine.toString());
    }
}
