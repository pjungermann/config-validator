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
package com.github.pjungermann.config.types.ini;

import com.github.pjungermann.config.Config;
import com.github.pjungermann.config.KeyBuilder;
import com.github.pjungermann.config.types.ConfigConverter;
import org.ini4j.Ini;
import org.ini4j.Profile.Section;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Map;

/**
 * Converter from {@link Ini} to {@link Config} and vise versa.
 *
 * @author Patrick Jungermann
 */
@Singleton
public class IniConverter implements ConfigConverter<Ini> {

    private KeyBuilder keyBuilder;

    @Inject
    public void setKeyBuilder(@NotNull final KeyBuilder keyBuilder) {
        this.keyBuilder = keyBuilder;
    }

    @NotNull
    @Override
    public Config from(@NotNull final Ini convertible) {
        final Config config = new Config();
        convertible.forEach((key, section) -> addSection(config, keyBuilder.toPrefix(key), section));

        return config;
    }

    protected void addSection(final Map<String, Object> config, final String keyPrefix, final Section section) {
        section.forEach((key, value) -> config.put(keyPrefix + key, value));

        for (final String childKey : section.childrenNames()) {
            addSection(config, keyBuilder.toPrefix(keyPrefix + childKey), section.getChild(childKey));
        }
    }

    @NotNull
    @Override
    public Ini to(@NotNull final Config config) {
        final Ini ini = new Ini();
        config.forEach((key, value) -> addConfigEntry(ini, key, value));

        return ini;
    }

    protected void addConfigEntry(@NotNull final Ini ini,
                                  @NotNull final String key,
                                  @Nullable final Object value) {
        final SectionOption sectionOption = new SectionOption(key);
        if (sectionOption.sectionName.contains("" + Ini.PATH_SEPARATOR)) {
            // skip:
            // we add the hierarchical key as flat original version
            // as well as flat standard version; see KeyBuilder#getSeparator()
            // otherwise this would result in a duplicate entry
            // (the library is not handling that itself)
            return;
        }

        if (sectionOption.optionName == null) {
            throw new IllegalArgumentException("The key needs to be in format: {sectionName}.{optionName} but was: " + key + "");
        }

        Section section = ini.get(sectionOption.sectionName);
        if (section == null) {
            section = ini.add(sectionOption.sectionName);
        }

        addConfigEntry(section, sectionOption.optionName, value);
    }

    protected void addConfigEntry(@NotNull final Section section,
                                  @NotNull final String key,
                                  @Nullable final Object value) {
        final SectionOption sectionOption = new SectionOption(key);
        if (sectionOption.optionName == null) {
            section.add(key, value);
            return;
        }

        final Section child = section.addChild(sectionOption.sectionName);
        addConfigEntry(child, sectionOption.optionName, value);
    }

    protected class SectionOption {
        final String sectionName;
        final String optionName;

        SectionOption(String key) {
            int separatorIndex = key.indexOf(keyBuilder.getSeparator());
            if (separatorIndex == -1) {
                sectionName = key;
                optionName = null;
                return;
            }

            sectionName = key.substring(0, separatorIndex);
            optionName = key.substring(separatorIndex + keyBuilder.getSeparator().length());
        }
    }

}
